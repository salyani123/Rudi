# Local acceptance checks

Run from the repository root with JDK 17, Android SDK Platform 35 and Build Tools 34.0.0:

    .\gradlew.bat clean assembleDebug assembleRelease lintDebug testDebugUnitTest

The debug APK is under app/build/outputs/apk/debug. The release output is unsigned unless a release signing configuration is supplied. Keep signing keys and credentials out of Git.

The group guide's release gate also includes a fresh checkout, live API/offline behaviour, large text, keyboard, rotation, CRUD after reinstall/restart and an installed signed APK. The lecturer's collaboration and technical defence marks require actual team contributions and live explanations.
