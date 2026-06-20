plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinCompose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.googleServices)
}

android {
    namespace = "com.techflow.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.techflow.app"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Room - persistencia local, guarda los productos aunque se cierre la app
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Retrofit - hace las llamadas HTTP a la API pública de productos
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)

    // Hilt - inyección de dependencias, evita crear objetos manualmente
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Navigation Compose - navegación entre pantallas pasando el ID del producto
    implementation(libs.navigation.compose)

    // ViewModel y StateFlow - gestiona el estado de la UI sin perderlo al rotar pantalla
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Coroutines - operaciones asíncronas como consultar Room o llamar a la API
    implementation(libs.coroutines.android)

    // Coroutines Play Services - convierte las Task de Firebase en funciones suspend con .await()
    implementation(libs.coroutines.play.services)

    // Coil - carga imágenes desde URLs en los productos
    implementation(libs.coil.compose)

    // Firebase BOM - asegura que todas las librerías Firebase usen versiones compatibles entre sí
    implementation(platform(libs.firebase.bom))
    // Firebase Authentication KTX - registro, login, persistencia de sesión y cierre de sesión
    implementation(libs.firebase.auth)
    // Cloud Firestore KTX - sincroniza el inventario en la nube separado por UID de usuario
    implementation(libs.firebase.firestore)
    // Firebase Cloud Messaging KTX - recibe notificaciones push en primer y segundo plano
    implementation(libs.firebase.messaging)
}
