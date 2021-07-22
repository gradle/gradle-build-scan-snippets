package com.gradle.maven.common.user.data.extension;

import org.apache.maven.MavenExecutionException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.logging.Logger;

import java.util.Optional;

import static java.util.Comparator.comparing;

final class ApiAccessor {

    private static final String MAVEN_EXT_REALM_ID = "maven.ext";
    private static final String GRADLE_ENTERPRISE_MAVEN_EXTENSION_COORDINATES = "com.gradle:gradle-enterprise-maven-extension";
    private static final String OWN_PACKAGE = "com.gradle.maven.common.user.data.extension";
    private static final String GRADLE_ENTERPRISE_API_PACKAGE = "com.gradle.maven.extension.api";
    private static final String GRADLE_ENTERPRISE_LISTENER_CLASS = GRADLE_ENTERPRISE_API_PACKAGE + ".GradleEnterpriseListener";

    static void registerExtension(Class<?> extensionClass, PlexusContainer container, Logger logger) throws Exception {
        ClassLoader classLoader = extensionClass.getClassLoader();
        if (classLoader instanceof ClassRealm) {
            ClassRealm extensionRealm = (ClassRealm) classLoader;
            Optional<ClassRealm> maybeGERealm = maybeGERealmIfImportsNeeded(extensionRealm);
            if (maybeGERealm.isPresent()) {
                // Depending on how the GE Maven extension is registered, its exported packages might not be available to this extension.
                importPackage(maybeGERealm.get(), extensionRealm, GRADLE_ENTERPRISE_API_PACKAGE);
            }
            try {
                extensionClass.getClassLoader().loadClass(GRADLE_ENTERPRISE_LISTENER_CLASS);
            } catch (ClassNotFoundException ignore) {
                // Abort
                return;
            }
            if (!container.hasComponent(GRADLE_ENTERPRISE_LISTENER_CLASS)) {
                // Depending on how the CCUDME is registered, the CommonCustomUserDataGradleEnterpriseListener might already be injected into Plexus
                container.addComponent(new CommonCustomUserDataGradleEnterpriseListener(logger), GRADLE_ENTERPRISE_LISTENER_CLASS);
            }
            if (maybeGERealm.isPresent()) {
                importPackage(extensionRealm, maybeGERealm.get(), OWN_PACKAGE);
            }
        }
    }

    /**
     * See https://svn.apache.org/repos/infra/websites/production/maven/content/reference/maven-classloading.html for a brief introduction to Maven classloading
     */
    private static Optional<ClassRealm> maybeGERealmIfImportsNeeded(ClassRealm extensionRealm) {
        if (!MAVEN_EXT_REALM_ID.equals(extensionRealm.getId())) {
            return extensionRealm.getWorld().getRealms().stream()
                    .filter(realm -> realm.getId().contains(GRADLE_ENTERPRISE_MAVEN_EXTENSION_COORDINATES) || realm.getId().equals(MAVEN_EXT_REALM_ID))
                    .max(comparing((ClassRealm realm) -> realm.getId().length()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Workaround for https://issues.apache.org/jira/browse/MNG-6906
     */
    private static void importPackage(ClassRealm sourceRealm, ClassRealm destinationRealm, String pkg) throws MavenExecutionException {
        try {
            destinationRealm.importFrom(sourceRealm.getId(), pkg);
        } catch (Exception e) {
            throw new MavenExecutionException("Could not import package '" + pkg + "' from realm '" + sourceRealm.getId() + "' to realm '" + destinationRealm.getId() + "'", e);
        }
    }

    private ApiAccessor() {
    }

}
