rootProject.name = "isel-leic-ps-template"

include(":project-proposal")
include(":project-report")
include(":project-org")

plugins {
    /*
     * cannot use version catalog for this plugin because libs.versions.toml is only read after this
     * file is evaluated. Another option is to use a composite build, but that would be an overkill
     * for this project. Ref: https://discuss.gradle.org/t/how-to-use-version-catalog-in-the-root-settings-gradle-kts-file/44603/5
     */
    id("com.gradle.enterprise") version("3.16.2")
}

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}
