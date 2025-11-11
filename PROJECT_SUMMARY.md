# BaerenSettingsProvider Project Summary and Design

This document summarizes the development process, final architecture, and key decisions made for the BaerenSettingsProvider project.

## 1. Project Goal

The primary objective is to create a dedicated Android application (`BaerenSettingsProvider`) that acts as a central `ContentProvider`. This provider securely manages and shares common settings (user profile, parent PIN, and parent email) with other applications running on the same device.

## 2. Final Architecture

The project was structured into two separate modules to ensure a clean separation between the implementation and its public interface:

1.  **`:app` Module:** A standard Android application module that contains the `BaerenSettingsProvider` class. This module holds the actual data logic, including the `SharedPreferences` implementation. It is the "owner" of the data.
2.  **`:contract` Module:** A lightweight Android library module. Its sole purpose is to contain the `SettingsContract.kt` file. This "contract" defines the public API for the `ContentProvider`, including the `Uri` and the data column keys.

**Rationale:** This architecture is the standard Android pattern for `ContentProvider` interaction. It allows client apps to communicate with the provider without needing any knowledge of its internal implementation. The contract library is the only piece that needs to be shared with consumer apps.

## 3. Sharing the Contract with Client Apps

To allow other apps to use the provider, the `:contract` module is published as a library.

1.  **Publishing:** The `contract` module is configured with the `maven-publish` Gradle plugin to be published to the local Maven repository (`~/.m2/repository`). This is achieved by running the `:contract:publishToMavenLocal` Gradle task.
2.  **Consumption:** In any client app that needs to access the settings, the following steps are taken:
    *   `mavenLocal()` is added as a repository in the client's `settings.gradle.kts`.
    *   The contract library is added as a dependency in the client's `build.gradle.kts`: `implementation("com.talq2me.baeren:settings-contract:1.0.0")`.
    *   The client app can then import the contract: `import com.talq2me.contract.SettingsContract`.

## 4. Key Implementation Snippets

### Querying Data from a Client App

This code demonstrates the correct, scope-safe way to retrieve settings.

```kotlin
// 1. Declare nullable variables outside the block
var userProfile: String? = null
var parentPin: String? = null
var parentEmail: String? = null

val contentResolver: ContentResolver = context.contentResolver
val projection = arrayOf(
    SettingsContract.KEY_PROFILE,
    SettingsContract.KEY_PIN,
    SettingsContract.KEY_PARENT_EMAIL
)

val cursor = contentResolver.query(
    SettingsContract.CONTENT_URI,
    projection, null, null, null
)

// 2. Assign values inside the block
cursor?.use {
    if (it.moveToFirst()) {
        userProfile = it.getString(it.getColumnIndexOrThrow(SettingsContract.KEY_PROFILE))
        parentPin = it.getString(it.getColumnIndexOrThrow(SettingsContract.KEY_PIN))
        parentEmail = it.getString(it.getColumnIndexOrThrow(SettingsContract.KEY_PARENT_EMAIL))
    }
}

// 3. Now you can safely use the retrieved values
userProfile?.let { profileValue ->
    val background = createDailyBackgroundImageView(profileValue)
    // ...
}
```

## 5. Troubleshooting Journey & Key Decisions

Several technical challenges were overcome during development.

*   **Phantom IDE Errors:** A major hurdle was the Android Studio editor showing compilation errors (e.g., "Unresolved reference") while the Gradle build succeeded. This was diagnosed as a corrupted IDE cache.
    *   **Resolution:** The problem was ultimately solved by using the **File > Invalidate Caches / Restart...** menu option in Android Studio.

*   **Gradle Build Failures:** The process of refactoring into a multi-module project caused several Gradle sync issues.
    *   **Resolution:** We corrected the `contract/build.gradle.kts` file by:
        1.  Replacing an invalid `compileSdk { version = release(36) }` with the correct `compileSdk = 36`.
        2.  Fixing a plugin classpath conflict by changing plugin declarations from `alias(...)` to the more direct `id("com.android.library")`.

*   **Git Repository Setup:** The project was initialized as a Git repository and linked to GitHub.
    *   **Initial Push:** A `git push --force` was required for the first push because the remote repository was created with a README, causing divergent histories.
    *   **Cleaning the Repository:** After the initial commit, ignored files (like the `.idea` directory) were present in the repo. We cleaned this by running `git rm -r --cached .`, adding the `.gitignore`, and committing the "clean" state.
