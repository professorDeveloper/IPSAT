plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

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
        versionCode = 2
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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

    lint {
        checkReleaseBuilds = false
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.activity:activity:1.9.3")
    implementation("androidx.paging:paging-runtime-ktx:3.3.5")
    implementation("androidx.media3:media3-datasource-okhttp:1.5.1")
    implementation("com.google.firebase:firebase-crashlytics:19.4.1")
    implementation("com.google.firebase:firebase-database:21.0.0")
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
    debugImplementation("com.github.chuckerteam.chucker:library:4.0.0")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.0.0")
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
    implementation("com.airbnb.android:lottie:5.2.0")

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
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.github.Blatzar:NiceHttp:0.4.4")// So wee Need this Libraries
    implementation("com.github.skydoves:progressview:1.1.3")
    implementation("org.apache.commons:commons-compress:1.21")

    val exo_version = "2.17.1"
    implementation("com.google.android.exoplayer:exoplayer:$exo_version")
    implementation("com.google.android.exoplayer:exoplayer-ui:$exo_version")
    implementation("com.google.android.exoplayer:exoplayer-hls:$exo_version")
    implementation("com.google.android.exoplayer:extension-mediasession:$exo_version")
    // OkHttp library
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // ExoPlayer OkHttp extension
    implementation("com.google.android.exoplayer:extension-okhttp:$exo_version")

    implementation("com.github.Ferfalk:SimpleSearchView:0.2.1")

    //Biometric
    implementation("androidx.biometric:biometric:1.1.0")

    // tapadoo
    implementation("com.github.tapadoo:alerter:7.2.4")

    //Room ORM
    // Room Components
    //noinspection GradleDependency
    implementation("androidx.room:room-runtime:2.6.1")
    //noinspection GradleDependency,KaptUsageInsteadOfKsp
    kapt("androidx.room:room-compiler:2.6.1")
    //noinspection GradleDependency
    implementation("androidx.room:room-ktx:2.6.1")


    // Horizontal bar charts
    implementation("com.diogobernardino:williamchart:3.10.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //Scalfon Image
    implementation("com.github.stfalcon-studio:StfalconImageViewer:v1.0.1")


    // Coil
    val coil_version = "1.4.0"
    implementation("io.coil-kt:coil:$coil_version")
    implementation("io.coil-kt:coil-gif:$coil_version")
    implementation("io.coil-kt:coil-svg:$coil_version")
    //
    implementation("com.github.zend10:OverlapImageListView:v1.0.1")


    //BugSnag
    implementation("com.bugsnag:bugsnag-android:6.+")
    implementation("com.bugsnag:bugsnag-android-performance:1.+")

    //MarkdownView
    implementation("io.noties.markwon:core:v4.6.2")


    val markwon_version = "4.6.2"
    implementation("io.noties.markwon:core:$markwon_version")
    implementation("io.noties.markwon:image:$markwon_version")
    implementation("io.noties.markwon:html:$markwon_version")
    implementation("io.noties.markwon:ext-strikethrough:$markwon_version")
    implementation("io.noties.markwon:inline-parser:$markwon_version")
    implementation("org.mozilla:rhino:1.7.13")
}