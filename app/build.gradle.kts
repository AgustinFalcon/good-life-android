import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    jacoco
}

android {
    namespace = "com.agusstkd.goodlife"
    compileSdk = 35 // Cambiado a versión estable

    defaultConfig {
        applicationId = "com.agusstkd.goodlife"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

// Configuración para Kotlin 2.0+
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}


jacoco {
    toolVersion = "0.8.12"
}

val logicCoverageIncludes = listOf(
    "com/agusstkd/goodlife/core/datetime/DateProvider*",
    "com/agusstkd/goodlife/core/datetime/LocalDateExtensions*",
    "com/agusstkd/goodlife/core/dispatcher/**",
    "com/agusstkd/goodlife/core/extensions/**",
    "com/agusstkd/goodlife/core/pagination/**",
    "com/agusstkd/goodlife/core/result/**",
    "com/agusstkd/goodlife/core/session/**",
    "com/agusstkd/goodlife/core/util/**",
    "com/agusstkd/goodlife/domain/auth/**",
    "com/agusstkd/goodlife/domain/usecase/**",
    "com/agusstkd/goodlife/presentation/navigation/core/ComposeNavigationController*",
    "com/agusstkd/goodlife/presentation/screen/**/*ViewModel*"
)

val logicCoverageExcludes = listOf(
    "**/*Screen*",
    "**/*Component*",
    "**/*Dialog*",
    "**/*BottomSheet*",
    "**/*Header*",
    "**/*Card*",
    "**/*Theme*",
    "**/presentation/theme/**",
    "**/presentation/navigation/core/ComposeNavigationControllerImpl*",
    "**/presentation/screen/splash/**",
    "**/presentation/screen/tabs/profile/**",
    "**/presentation/**/model/**",
    "**/data/remote/dto/**",
    "**/data/local/entity/**",
    "**/data/local/database/**",
    "**/data/local/dao/**",
    "**/di/**",
    "**/*Module*",
    "**/*App*",
    "**/*Activity*",
    "**/*Owner*",
    "**/*Route*",
    "**/*Graph*",
    "**/*Dto*",
    "**/*Response*",
    "**/*Request*",
    "**/*Result*",
    "**/*UiState*",
    "**/*UiAction*",
    "**/*ComposableSingletons*",
    "**/*Preview*",
    "**/BuildConfig.*",
    "**/R.class",
    "**/R$*.class",
    "**/Manifest*.*"
)

fun logicClassDirectories() = files(
    fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
        include(logicCoverageIncludes)
        exclude(logicCoverageExcludes)
    },
    fileTree(layout.buildDirectory.dir("intermediates/javac/debug/classes")) {
        include(logicCoverageIncludes)
        exclude(logicCoverageExcludes)
    }
)

tasks.register<JacocoReport>("logicDebugUnitTestCoverageReport") {
    dependsOn("testDebugUnitTest")

    group = "verification"
    description = "Generates JaCoCo coverage for Android logic classes only."

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    classDirectories.setFrom(logicClassDirectories())
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        include("jacoco/testDebugUnitTest.exec")
    })
}

tasks.register<JacocoCoverageVerification>("logicDebugUnitTestCoverageVerification") {
    dependsOn("logicDebugUnitTestCoverageReport")

    group = "verification"
    description = "Fails when Android logic coverage drops below the scoped baseline."

    classDirectories.setFrom(logicClassDirectories())
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        include("jacoco/testDebugUnitTest.exec")
    })

    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.named("check") {
    dependsOn("logicDebugUnitTestCoverageVerification")
}


dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.arch.core.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Serialization
    implementation(libs.kotlin.serialization.json)

    // Lottie
    implementation(libs.lottie)

    // Iconos de material
    implementation(libs.androidx.material.icons.extended)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.adapter)

    // OkHttp Logging
    implementation(libs.okhttp.logging)

    // Room Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Koin - Dependency Injection
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.collections.immutable)

    // DateTime (KMP-ready)
    implementation(libs.kotlinx.datetime)

    // Biometric Authentication
    implementation(libs.androidx.biometric)

    // Encrypted SharedPreferences (para guardar credenciales seguras)
    implementation(libs.androidx.security.crypto)

    // Icons
    implementation(libs.androidx.material.icons.extended)

    // Coil
    implementation(libs.coil.compose)
}
