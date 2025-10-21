import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false

    alias(libs.plugins.ktlint) apply true
    alias(libs.plugins.detekt) apply true
    alias(libs.plugins.kover) apply true
}

ktlint {
    filter {
        exclude("**/generated/**") // Exclude generated files
    }

    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
    }
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = true // activate all available (even unstable) rules.
    // point to your custom config defining rules to run, overwriting default behavior
    config.setFrom("$projectDir/config/detekt.yml")
//    baseline = file("$projectDir/baseline.xml")
    ignoreFailures = false // a way of suppressing issues before introducing detekt
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    include("**/*.kt")
    config.setFrom("$projectDir/detekt.yml")
    buildUponDefaultConfig = true
    allRules = true
    ignoreFailures = false
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

tasks.register("detektAll") {
    allprojects {
        this@register.dependsOn(tasks.withType<Detekt>())
    }
}

kover {
    reports {
        filters {
            excludes {
                classes("*.BuildConfig", "*.Generated*")
            }
        }
        verify {
            rule {
                disabled = false
                bound {
                    minValue = 1 // TODO: change to 80
                }
            }
        }
    }
}

tasks.register("koverAll") {
    group = "verification"
    description = "Run Kover reports and verification across all modules"

    allprojects {
        tasks.matching { it.name.endsWith("UnitTest") }.forEach { testTask ->
            this@register.dependsOn(testTask)
        }
        tasks.matching { it.name.endsWith("jvmTest") }.forEach { testTask ->
            this@register.dependsOn(testTask)
        }
        tasks.matching { it.name.startsWith("koverVerify") }.forEach { verifyTask ->
            this@register.dependsOn(verifyTask)
        }
        tasks.matching { it.name.startsWith("kover") }.forEach { koverTask ->
            if (koverTask.name != "koverAll") {
                this@register.dependsOn(koverTask)
            }
        }
    }
}

subprojects {
    apply(
        plugin =
            rootProject.libs.plugins.ktlint
                .get()
                .pluginId,
    )

    apply(
        plugin =
            rootProject.libs.plugins.detekt
                .get()
                .pluginId,
    )

    apply(
        plugin =
            rootProject.libs.plugins.kover
                .get()
                .pluginId,
    )

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        debug.set(true)
    }

    pluginManager.withPlugin(
        rootProject.libs.plugins.kover
            .get()
            .pluginId,
    ) {
        kover {
            reports {
                filters {
                    excludes {
                        classes("*.BuildConfig", "*.Generated*")
                    }
                }
                verify {
                    rule {
                        disabled = false
                        bound {
                            minValue = 1 // TODO: change to 80
                        }
                    }
                }
            }
        }
    }
}
