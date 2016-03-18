# Usage
Ensure a running emulator or device. Then just run the gradle task `connectedDebugAndroidTest`.

To run within IntelliJ, make sure to set an Android SDK as the Module SDK for the `android-test` module. Then create a
AndroidTests run configuration. Select the `android-test` module, optionally the class and method to test. Set the test
runner to `android.support.test.runner.AndroidJUnitRunner`.