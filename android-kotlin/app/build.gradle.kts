plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
    alias(libs.plugins.jacoco)
}

android {
    namespace = "com.arthurslife.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.arthurslife.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.arthurslife.app.HiltTestRunner"
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
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
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
            it.jvmArgs("-Xshare:off", "-XX:+HeapDumpOnOutOfMemoryError")
        }
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
        animationsDisabled = true
    }

    testCoverage {
        jacocoVersion = libs.versions.jacoco.get()
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    // Explicit Kotlin standard library dependency
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // Room
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Security
    implementation(libs.bcrypt)

    // Logging
    implementation(libs.timber)

    // Static Analysis
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.7")

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.bundles.junit.jupiter)
    testImplementation(libs.bundles.testing.unit)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")

    // Android instrumentation testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.bundles.junit.jupiter)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation("io.mockk:mockk-android:1.13.10")
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Coroutine testing - using version catalog versions
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.coroutines.test)

    // MockK for mocking in unit tests - using version catalog version
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation(libs.mockk)

    // Kotlinx datetime for test utilities
    testImplementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
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
