plugins {
    alias(vclibs.plugins.android.application)
    alias(vclibs.plugins.kotlin.android)
    alias(vclibs.plugins.kotlin.compose)
    alias(vclibs.plugins.ksp)
    alias(vclibs.plugins.hilt)
    alias(vclibs.plugins.kotlin.serialization)
    alias(vclibs.plugins.detekt)
    alias(vclibs.plugins.jacoco)
}

android {
    namespace = "com.lemonqwest.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.lemonqwest.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.lemonqwest.app.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs += listOf(
            "-Xcontext-parameters",
            "-Xjvm-default=all"
        )
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
            // Modern JVM configuration to avoid sharing warnings in tests
            it.jvmArgs(
                "-Xshare:off",
                "-XX:+HeapDumpOnOutOfMemoryError",
                "-Xmx2048m",
                "-XX:MaxMetaspaceSize=512m"
            )
            // Disable JUnit parallel execution for test isolation
            it.systemProperty("junit.jupiter.execution.parallel.enabled", "false")
            it.systemProperty("junit.jupiter.execution.parallel.mode.default", "same_thread")
            it.systemProperty("junit.jupiter.execution.parallel.mode.classes.default", "same_thread")
            it.maxParallelForks = 1
        }
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
        animationsDisabled = true
    }

    testCoverage {
        jacocoVersion = vclibs.versions.jacoco.get()
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    source.setFrom(
        files(
            "src/main/java",
            "src/test/java",
            "src/androidTest/java"
        )
    )
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(vclibs.androidx.core.ktx)
    implementation(vclibs.androidx.lifecycle.runtime.ktx)
    implementation(vclibs.androidx.activity.compose)
    implementation(platform(vclibs.androidx.compose.bom))
    implementation(vclibs.bundles.compose)

    // Explicit Kotlin standard library dependency (use catalog version)
    implementation(vclibs.kotlin.stdlib)

    // Navigation
    implementation(vclibs.androidx.navigation.compose)

    // Hilt
    implementation(vclibs.hilt.android)
    implementation(vclibs.androidx.hilt.navigation.compose)
    ksp(vclibs.hilt.android.compiler)

    // Room
    implementation(vclibs.bundles.room)
    ksp(vclibs.androidx.room.compiler)

    // DataStore
    implementation(vclibs.androidx.datastore.preferences)

    // Serialization
    implementation(vclibs.kotlinx.serialization.json)

    // Security
    implementation(vclibs.bcrypt)

    // Logging
    implementation(vclibs.timber)

    // Static Analysis
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${vclibs.versions.detekt.get()}")

    // Testing
    testImplementation(vclibs.junit)
    testImplementation(vclibs.bundles.junit.jupiter)
    testImplementation(vclibs.bundles.testing.unit)
    testImplementation(vclibs.hilt.android.testing)
    kspTest(vclibs.hilt.android.compiler)
    testRuntimeOnly(vclibs.junit.platform.launcher)
    testRuntimeOnly(vclibs.junit.jupiter.engine)
    testRuntimeOnly(vclibs.junit.vintage.engine)

    // Android instrumentation testing
    androidTestImplementation(vclibs.androidx.junit)
    androidTestImplementation(vclibs.androidx.espresso.core)
    androidTestImplementation(platform(vclibs.androidx.compose.bom))
    androidTestImplementation(vclibs.androidx.ui.test.junit4)
    androidTestImplementation(vclibs.bundles.junit.jupiter)
    androidTestImplementation(vclibs.androidx.test.core)
    androidTestImplementation(vclibs.androidx.test.core.ktx)
    androidTestImplementation(vclibs.androidx.room.testing)
    androidTestImplementation("io.mockk:mockk-android:${vclibs.versions.mockk.get()}")
    androidTestImplementation(vclibs.hilt.android.testing)
    kspAndroidTest(vclibs.hilt.android.compiler)

    debugImplementation(vclibs.androidx.ui.tooling)
    debugImplementation(vclibs.androidx.ui.test.manifest)

    // Coroutine testing - using version catalog versions
    testImplementation(vclibs.coroutines.test)
    androidTestImplementation(vclibs.coroutines.test)

    // MockK for mocking in unit tests - using version catalog version
    testImplementation(vclibs.turbine)
    testImplementation(vclibs.mockk)

    // Kotlinx datetime for test utilities
    testImplementation(vclibs.kotlinx.datetime)
}


// Testing Configuration
tasks.withType<Test> {
    useJUnitPlatform()
}

// Detekt Configuration
detekt {
    toolVersion = "1.23.7"
    config.setFrom(file("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
        md.required.set(true)
    }
    autoCorrect = true
}

// Add detekt format task using modern task registration
tasks.register<io.gitlab.arturbosch.detekt.Detekt>("detektFormat") {
    description = "Formats code with detekt"
    parallel = true
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = true
    setSource(files("src/main/java", "src/test/java"))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/build/**")
    exclude("**/generated/**")
    reports {
        html.required.set(false)
        xml.required.set(false)
        txt.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
}

// Task dependencies for quality checks using modern API
tasks.named("preBuild") {
    dependsOn("detekt")
}

// JaCoCo Test Coverage Configuration
jacoco {
    toolVersion = "0.8.8"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    group = "Reporting"
    description = "Generate Jacoco coverage reports for Debug build"

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/di/**/*.*",
        "**/hilt_aggregated_deps/**",
        "**/*_Factory*.*",
        "**/*_HiltComponents*.*",
        "**/*_ComponentTreeDeps*.*",
        "**/DaggerHiltApplicationComponent*.*",
    )

    val debugTree = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug")
    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree("${layout.buildDirectory.get()}").include("**/*.exec", "**/*.ec"))

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(fileFilter)
            }
        })
    )
}

// Coverage verification
tasks.register<JacocoCoverageVerification>("jacocoCoverageVerification") {
    dependsOn("jacocoTestReport")
    group = "verification"
    description = "Verify test coverage meets minimum thresholds"

    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal() // 80% coverage minimum
            }
        }
        rule {
            limit {
                counter = "BRANCH"
                minimum = "0.70".toBigDecimal() // 70% branch coverage minimum
            }
        }
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/di/**/*.*",
        "**/hilt_aggregated_deps/**",
        "**/*_Factory*.*",
        "**/*_HiltComponents*.*",
        "**/*_ComponentTreeDeps*.*",
        "**/DaggerHiltApplicationComponent*.*",
    )

    val debugTree = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug")
    classDirectories.setFrom(
        files(debugTree).map {
            fileTree(it) {
                exclude(fileFilter)
            }
        }
    )
}
