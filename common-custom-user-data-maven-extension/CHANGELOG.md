# Changelog
All notable changes to this project will be documented in this file.

## [Unreleased]

## [1.2] - 2020-08-31
### Added
- Add support for TravisCI
- Add support for tagging IDEA based builds

### Removed
- Remove build scan tag for Maven version

## [1.1.1] - 2020-07-22
### Fixed
- Add `hint` to `CommonCustomUserDataMavenExtension` to allow multiple `AbstractMavenLifecycleParticipant` implementations on the classpath

## [1.1] - 2020-06-18
### Added
- Evaluate `gradle-enterprise-custom-user-data.groovy` custom user data Groovy script

## [1.0.1] - 2020-06-12
### Fixed
- Improve lookup of `BuildScanApi`

### Removed
- Remove dependency on SLF4J.

## [1.0] - 2020-05-25
Initial release.

[Unreleased]: https://github.com/olivierlacan/keep-a-changelog/compare/common-custom-user-data-maven-extension-1.2...HEAD
[1.2]: https://github.com/gradle/gradle-enterprise-build-config-samples/compare/common-custom-user-data-maven-extension-1.1.1...common-custom-user-data-maven-extension-1.2
[1.1.1]: https://github.com/gradle/gradle-enterprise-build-config-samples/compare/common-custom-user-data-maven-extension-1.1...common-custom-user-data-maven-extension-1.1.1
[1.1]: https://github.com/gradle/gradle-enterprise-build-config-samples/compare/common-custom-user-data-maven-extension-1.0.1...common-custom-user-data-maven-extension-1.1
[1.0.1]: https://github.com/gradle/gradle-enterprise-build-config-samples/compare/common-custom-user-data-maven-extension-1.0...common-custom-user-data-maven-extension-1.0.1
[1.0]: https://github.com/gradle/gradle-enterprise-build-config-samples/releases/tag/common-custom-user-data-maven-extension-1.0