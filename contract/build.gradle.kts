plugins {
    // Switching from alias() to id() to resolve the classpath conflict
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.talq2me.contract"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // A contract library only needs the dependencies for the code it contains.
    // In this case, that's just core-ktx for the .toUri() extension function.
    implementation(libs.androidx.core.ktx)
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.talq2me.baeren" // You can choose your own group
            artifactId = "settings-contract"   // The name of the library
            version = "1.0.0"                // The version

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
