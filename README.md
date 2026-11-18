# Rudi E-Waste

Rudi is a Kotlin Android app for urban Kenyan households and small offices with old or damaged electronics. People often store unwanted devices indefinitely, mix them with general rubbish, or turn to informal collectors. Finding current drop-off instructions can take several searches, and discarded devices can expose personal data or pose avoidable environmental and battery risks. Rudi gives device-specific preparation steps, a filterable directory and locally saved pickup plans with reminders.

## Run locally

Use Android Studio Ladybug or a compatible IntelliJ setup with Android support, JDK 17, Android SDK Platform 35 and Build Tools 34.0.0. The app supports Android 8.0 (API 26) and above. The wrapper uses Gradle 8.9. Android Gradle Plugin is 8.7.2, Kotlin is 2.1.20 and KSP is 2.1.20-1.0.32.

Set the IDE Gradle JVM to JDK 17 and select your Android SDK, or set JAVA_HOME and ANDROID_HOME before running:

    .\gradlew.bat clean assembleDebug assembleRelease lintDebug testDebugUnitTest

On macOS/Linux, use ./gradlew. Install app/build/outputs/apk/debug/app-debug.apk on a test device.

## How it works

- Home opens a disposal guide or the pickup form and lists the three latest requests.
- For disposal, the user enters their device details, reads preparation steps and opens Centres with that device selected.
- For pickup, the user enters the item, address and preferred Nairobi date/slot. Review validates and saves a local plan. Requests opens detail with the Room ID and supports edit, delete and optional calendar export.
- The UI uses View Binding and observes LiveData from its ViewModels. DraftViewModel uses SavedStateHandle for unsaved form state. PickupViewModel and CentreViewModel call repositories, which call Room DAOs or the Retrofit service.
- Room database rudi.db has pickup_requests and collection_centres. The first table uses a generated request ID and keeps its original timestamp on updates. The second has fixed directory IDs, city and accepted-device fields. Lists update when their tables change.
- Centres seeds offline data, then fetches [centres.json](https://raw.githubusercontent.com/salyani123/Rudi/main/api/centres.json) with Retrofit/Gson. Valid rows are upserted. When the request fails, Room still supplies the saved directory and users can retry. Refresh adds and updates records, and does not remove old ones.
- WorkManager schedules unique work per request. It replaces reminders on edit, cancels them on delete, and rereads the saved row before notifying. Timing is approximate and Android 13+ requires notification permission. The date/slot is evaluated in Africa/Nairobi.

A pickup is a personal plan and needs provider confirmation. Check a centre's address, hours and accepted devices before a real visit. Nakuru has no verified listing in the bundled directory. The calendar editor lets users save their own event, which Rudi does not update after edits.

See [local acceptance checks](docs/local-verification.md) for verification and [Stage 5 handover](docs/stage-5-handover.md) for the earlier stage review. Centre sources are recorded with each row in [the directory](api/centres.json).
