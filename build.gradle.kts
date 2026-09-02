plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}
// Lets `-PheimuiCore=<version>` point the whole build at an unreleased SDK build, which is what
// heimui-core's `publishLocal` task produces.
//
// Gradle has no built-in way to override a version declared in the TOML, and it accepts an unread
// `-P` without complaint. Documenting the flag without wiring it was worse than not documenting it:
// the build succeeded and silently used the released version, so you were testing against the wrong
// SDK with nothing on screen to say so. Substituting at resolution time also covers the per-target
// artifacts (`-android`, `-iosarm64`, ...), which are versioned together but requested separately.
val heimuiCoreOverride: String? = providers.gradleProperty("heimuiCore").orNull
subprojects {
    configurations.configureEach {
        resolutionStrategy.eachDependency {
            if (heimuiCoreOverride != null && requested.group == "io.heimui") {
                useVersion(heimuiCoreOverride)
            }
        }
    }
}
