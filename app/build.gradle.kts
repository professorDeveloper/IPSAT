plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")

}

android {
    namespace = "com.ip_tv.ipsat"
    compileSdk = 35
    kapt {
        correctErrorTypes = true
    }
    defaultConfig {
        applicationId = "com.ip_tv.ipsat"
        minSdk = 26
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isShrinkResources =true
            isMinifyEnabled = true
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
    buildFeatures {
        viewBinding = true

    }
    packagingOptions {
        exclude("AndroidManifest.xml")
        exclude("lib/arm64-v8a/libcardioDecider.so")
        exclude("lib/arm64-v8a/libcardioRecognizer.so")
        exclude("lib/arm64-v8a/libcardioRecognizer_tegra2.so")
        exclude("lib/arm64-v8a/libopencv_core.so")
        exclude("lib/arm64-v8a/libopencv_imgproc.so")
        exclude("lib/armeabi/libcardioDecider.so")
        exclude("lib/armeabi-v7a/libcardioDecider.so")
        exclude("lib/armeabi-v7a/libcardioRecognizer.so")
        exclude("lib/armeabi-v7a/libcardioRecognizer_tegra2.so")
        exclude ("lib/armeabi-v7a/libopencv_core.so")
        exclude("lib/armeabi-v7a/libopencv_imgproc.so")
        exclude("lib/mips/libcardioDecider.so")
        exclude("lib/x86/libcardioDecider.so")
        exclude("lib/x86/libcardioRecognizer.so")
        exclude("lib/x86/libcardioRecognizer_tegra2.so")
        exclude("lib/x86/libopencv_core.so")
        exclude("lib/x86/libopencv_imgproc.so")
        exclude("lib/x86_64/libcardioDecider.so")
        exclude("lib/x86_64/libcardioRecognizer.so")
        exclude("lib/x86_64/libcardioRecognizer_tegra2.so")
        exclude("lib/x86_64/libopencv_core.so")
        exclude("lib/x86_64/libopencv_imgproc.so")
    }

    lint {
        checkReleaseBuilds =false
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.activity:activity:1.9.3")
    implementation("androidx.paging:paging-runtime-ktx:3.3.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    // Koin
    implementation("io.insert-koin:koin-android:3.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.navigation:navigation-runtime-ktx:2.8.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.4")

    // sdp
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")

    // DI
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")


    // preference
    implementation("androidx.preference:preference-ktx:1.2.1")

    //REST - APIService
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // secure
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")


    // exo player
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //Chucker
    debugImplementation( "com.github.chuckerteam.chucker:library:4.0.0")
    releaseImplementation( "com.github.chuckerteam.chucker:library-no-op:4.0.0")
//
    /**
     * Glide
     * */
    implementation("com.github.bumptech.glide:glide:4.15.1")
    kapt("com.github.bumptech.glide:compiler:4.15.1")
    //
    implementation("com.github.bumptech.glide:okhttp3-integration:4.15.1")
    implementation("jp.wasabeef:glide-transformations:4.3.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.flaviofaria:kenburnsview:1.0.7")

    // LinearProgress
    //
    //Lottie
    implementation ("com.airbnb.android:lottie:5.2.0")

    implementation("com.tbuonomo:dotsindicator:5.1.0")

    //Shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")  // Add Shimmer

    implementation("com.github.Ferfalk:SimpleSearchView:0.2.1")
    implementation("com.github.mancj:MaterialSearchBar:0.8.5")
    implementation("org.jsoup:jsoup:1.15.1")
    val dialogx_version = "0.0.49"
    implementation("com.kongzue.dialogx:DialogX:${dialogx_version}")

    implementation("com.github.Blatzar:NiceHttp:0.4.4")

    // exo player
    api ("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation ("com.github.Blatzar:NiceHttp:0.4.4")// So wee Need this Libraries

    implementation("org.apache.commons:commons-compress:1.21")
    val exo_version = "2.19.1"
    implementation( "com.google.android.exoplayer:exoplayer:$exo_version")
    implementation("com.google.android.exoplayer:exoplayer-ui:$exo_version")
    implementation( "com.google.android.exoplayer:exoplayer-hls:$exo_version")
    implementation("com.google.android.exoplayer:extension-mediasession:$exo_version")

    implementation("com.github.Ferfalk:SimpleSearchView:0.2.1")

}