# Local acceptance checks

Run from the repository root with JDK 17, Android SDK Platform 35 and Build Tools 34.0.0:

    .\gradlew.bat clean assembleDebug assembleRelease lintDebug testDebugUnitTest

The debug APK is under app/build/outputs/apk/debug. There is an unsigned release
output and a separate emulator-only copy called rudi-release-local-test.apk.
The latter was signed locally with the Android debug key and passed APK signature
verification. The public release requires the team's release key.

## Results on 18 November 2026

| Check | Result |
| --- | --- |
| Debug and release assembly | Passed on Gradle 8.9 and JDK 17, including a separate fresh local clone of 5d8aae3. |
| Lint and unit tests | Lint had zero errors and ten warnings for the pinned versions and unused template resources. Four unit tests passed. |
| Disposal and filters | Laptop guidance opened. The Centres screen kept the device filter, showed WEEE in Nairobi, and showed the empty state for Nakuru. |
| Validation and draft | Invalid quantity and missing pickup details were blocked. Text and quantity survived rotation. Confirming the date after rotating with the picker open updated the draft. |
| CRUD and persistence | Debug Room inspection confirmed one row after create and same ID and creation time after edit. Data survived force-stop. Keep request preserved it. Confirmed delete after rotating the dialog removed the row and it remained removed after relaunch. |
| Signed release | APK installed on Pixel 8. Create, read, edit, delete and restart checks passed through the UI. |
| WorkManager | System job registered and an actual pickup reminder posted. |
| Directory API | Online refresh passed. Offline refresh retained Room data and exposed Retry. Reconnecting and retrying cleared the offline notice. |
| Maps | The Nairobi centre's Maps button opened an external handler on Pixel 9. |
| Large text and keyboard | The item form scrolled to Continue at 150% system text size with the keyboard open on Pixel 9. |
| Calendar | Google Calendar received the intent and started account setup. Final event editing/saving needs a configured calendar app. |

GitHub publication, official release signing, the team's collaboration evidence and
individual technical defence are separate from local app verification.
