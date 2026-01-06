import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-parcelize")
}

android {
    namespace = "co.com.mypt"
    compileSdk = 35

    defaultConfig {
        applicationId = "co.com.mypt"
        minSdk = 24
        targetSdk = 35
        versionCode = 12
        versionName = "1.11"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }
    buildFeatures {
        buildConfig = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
        viewBinding = true
    }
    ndkVersion = "26.1.10909125"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.places)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.ui.graphics.android)
    //implementation(libs.pose.detection.accurate)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.animation.core.android)
    //implementation(libs.firebase.messaging)
    //implementation(libs.firebase.auth.ktx)
    //implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //phone otp
    implementation(libs.play.services.auth)
    implementation(libs.play.services.auth.api.phone)

    implementation ("jp.wasabeef:picasso-transformations:2.4.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    //for weight meassurement
    //implementation ("com.github.drynk-app:weighing-scales:1.0.1")
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.29")
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")
    implementation ("com.github.misosvec:SingleRowCalendar:1.0.0")
    //implementation ("com.github.YvesCheung.RollingText:RollingText:1.3.0")
    implementation("com.tbuonomo:dotsindicator:5.1.0")
    //implementation ("ke.tang:ruler:1.0.5")
    implementation ("com.github.TangKe:ruler:1.0.5")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    //implementation ("com.github.hadibtf:SemiCircleArcProgressBar:1.1.1")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("androidx.preference:preference-ktx:1.2.1")
    implementation ("com.github.sparrow007:carouselrecyclerview:1.2.6")
    implementation ("com.github.bumptech.glide:glide:5.0.5")
    implementation ("androidx.media3:media3-exoplayer:1.8.0")
    implementation ("androidx.media3:media3-ui:1.8.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.4.9")
    implementation ("androidx.core:core-splashscreen:1.0.1")
   // implementation(project(":openCVsdk"))
    implementation ("ai.tabby:tabby-android:1.1.13")

    //gmail integration
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-messaging")
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation ("com.facebook.android:facebook-login:16.0.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
    implementation ("com.hbb20:ccp:2.7.0")
    implementation("com.google.maps.android:android-maps-utils:2.2.5")
}