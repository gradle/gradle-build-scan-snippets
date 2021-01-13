/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.gradle;

import com.gradle.enterprise.gradleplugin.GradleEnterpriseExtension;
import com.gradle.enterprise.gradleplugin.GradleEnterprisePlugin;
import com.gradle.scan.plugin.BuildScanExtension;
import com.gradle.scan.plugin.BuildScanPlugin;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.initialization.Settings;
import org.gradle.caching.configuration.BuildCacheConfiguration;

public class CommonCustomUserDataGradlePlugin implements Plugin<Object> {
    public void apply(Object target) {
        if (target instanceof Settings) {
            applySettingsPlugin((Settings) target);
        } else if (target instanceof Project) {
            applyProjectPlugin((Project) target);
        }
    }

    private void applySettingsPlugin(Settings settings) {
        settings.getPlugins().withType(GradleEnterprisePlugin.class, __ -> {
            BuildScanExtension buildScan = settings.getExtensions().getByType(GradleEnterpriseExtension.class).getBuildScan();
            CustomBuildScanConfig.configureBuildScan(buildScan, settings.getGradle());

            BuildCacheConfiguration buildCache = settings.getBuildCache();
            CustomBuildCacheConfig.configureBuildCache(buildCache);
        });
    }

    private void applyProjectPlugin(Project project) {
        if (!project.equals(project.getRootProject())) {
            throw new GradleException("Common custom user data plugin may only be applied to root project");
        }
        project.getPlugins().withType(BuildScanPlugin.class, __ -> {
            BuildScanExtension buildScan = project.getExtensions().getByType(GradleEnterpriseExtension.class).getBuildScan();
            CustomBuildScanConfig.configureBuildScan(buildScan, project.getGradle());
            // Build cache configuration cannot be accessed from a project plugin
        });
    }
}
