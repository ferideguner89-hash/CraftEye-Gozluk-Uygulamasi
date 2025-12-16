// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.5" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
// build.gradle (Proje Seviyesi)

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // ... diğer classpath'leriniz (varsa)
        classpath ("com.android.tools.build:gradle:8.3.0")
        // 🔥 İSTENEN CLASSPATH KODU BURAYA EKLENMELİ
        classpath ("com.google.gms:google-services:4.4.4")

        // VEYA daha güncel ve stabil versiyonu kullanın:
        // classpath 'com.google.gms:google-services:4.4.1'
    }

}


