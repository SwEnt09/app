import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.ncorti.ktfmt.gradle") version "0.16.0"
    id("org.sonarqube") version "4.4.1.3373"
    kotlin("plugin.serialization") version "1.9.22"
    id("jacoco")
    // Hilt
    kotlin("kapt")
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.github.swent.echo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.github.swent.echo"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.github.swent.echo.hilt.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
    testCoverage {
        jacocoVersion = "0.8.8"
    }

    testOptions {
        // Necessary to make mockk work on integration tests
        packaging {
            jniLibs {
                useLegacyPackaging = true
            }
        }
        // Necessary for Robolectric to work
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    installation {
        installOptions.addAll(listOf("-g", "-r"))
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.gotrue)
    implementation(libs.ktor.client.android)
    implementation(libs.kotlinx.serialization.json)

    // Google Authentication
    implementation(libs.androidx.credentials)
    implementation (libs.googleid)
    implementation(libs.supabase.compose.auth)

    // Map Libre
    implementation(libs.android.sdk)
    implementation(libs.ramani.maplibre)
    
    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.android.compiler)

    //navigation
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    kapt(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.slf4j.simple)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.turbine)

    // Hilt
    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.android.compiler)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)

    // Hilt
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.android.compiler)

    //navigation
    androidTestImplementation(libs.androidx.navigation.testing)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

sonar {
    properties {
        property("sonar.projectKey", "echo-swent_echo")
        property("sonar.organization", "echo-swent")
        property("sonar.host.url", "https://sonarcloud.io")
        // Comma-separated paths to the various directories containing the *.xml JUnit report files. Each path may be absolute or relative to the project base directory.
        property("sonar.junit.reportPaths", "${project.layout.buildDirectory.get()}/test-results/testDebugUnitTest/")
        // Paths to xml files with Android Lint issues. If the main flavor is changed, this file will have to be changed too.
        property("sonar.androidLint.reportPaths", "${project.layout.buildDirectory.get()}/reports/lint-results-debug.xml")
        // Paths to JaCoCo XML coverage report files.
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
	// To prevent sonar depending on compile tasks, which will be removed in version 5.x. As a consequence, we will need to make sure that
	// the project is compiled before running checks.
	property("sonar.gradle.skipCompile", "true")
    }
}

ktfmt {
    // KotlinLang style - 4 space indentation - From kotlinlang.org/docs/coding-conventions.html
    kotlinLangStyle()
}

// Hilt: Allow references to generated code
kapt {
    correctErrorTypes = true
}


// Jacoco configuration
tasks.withType<Test> {
    // Configure Jacoco for each tests
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register("jacocoTestReport", JacocoReport::class) {
    mustRunAfter("testDebugUnitTest", "connectedDebugAndroidTest")

    reports {
        xml.required = true
        html.required = true
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/*Hilt*.*",
        "hilt_aggregated_deps/**",
        "**/*_Factory.class2",
        "**/*_MembersInjector.class",
    )

    val debugTree = fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    val mainSrc = "${project.layout.projectDirectory}/src/main/java"
    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.layout.buildDirectory.get()) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        include("outputs/code_coverage/debugAndroidTest/connected/*/coverage.ec")
    })
}

// Avoid redundant tests, debug is sufficient
tasks.withType<Test> {
    onlyIf {
        !name.toLowerCaseAsciiOnly().contains("release")
    }
}

