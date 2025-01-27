plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.safeargs)
    id("com.google.devtools.ksp")
    id("org.jetbrains.dokka") // Adiciona o Dokka
}

android {
    namespace = "com.example.barberapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.barberapp"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures.viewBinding = true
    // ou: viewBinding.enable = true

}

dependencies {
    api(libs.cameraview)

    // Image loading (usar uma OU outra)
    implementation(libs.coil)
    // implementation(libs.glide)

    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // https://github.com/hdodenhof/CircleImageView
    implementation(libs.circleimageview)

    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.gson)
}

// Configuração do Dokka
tasks.dokkaHtml {
    outputDirectory.set(file("$buildDir/dokka"))
    dokkaSourceSets {
        named("main") {
            // Configuração do Dokka para o sourceSet principal
            includeNonPublic.set(false) // Opcional: Inclui apenas membros públicos
            skipDeprecated.set(true) // Opcional: Ignora membros marcados como @Deprecated
            suppressInheritedMembers.set(true) // Ignora métodos herdados
            reportUndocumented.set(true) // Gera aviso para membros não documentados
            skipEmptyPackages.set(true) // Ignora pacotes sem classes documentadas
        }
    }
}

