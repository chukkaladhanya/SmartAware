# SmartAware - Network Monitor App 📱

A minimal Android application that monitors WiFi and cellular network changes, sends smart notifications, and logs network events.

## Features ✨

### 1. Real-time Network Monitoring
- WiFi connection status
- Cellular connection status  
- WiFi signal strength detection
- Cellular network type detection (2G/3G/4G/5G)

### 2. Smart Notifications (3 Types)
1. **WiFi Connected**: "WiFi Connected - Signal: 85% (Excellent)"
2. **Cellular Connected**: "Using Mobile Data - 4G Network"
3. **Periodic WiFi Reminder** (every 6 hours): "Turn on WiFi to save battery!"

### 3. Event Logging (4 Event Types)
- ✅ WiFi Turned ON
- ❌ WiFi Turned OFF
- ✅ Cellular Turned ON
- ❌ Cellular Turned OFF

### 4. Clean UI
- Material 3 Design with dark theme
- Current network status card with signal strength
- Event logs with timestamps
- Pull-to-refresh functionality

## Tech Stack 🛠️

- **Language**: 100% Kotlin
- **UI**: Jetpack Compose + Material 3
- **Database**: Room (local event storage)
- **Background Work**: WorkManager + Foreground Service
- **Min SDK**: 30 (Android 11)
- **Target SDK**: 34 (Android 14)

## Prerequisites 📋

### Required
1. **Android Studio** (latest version)
   - Download: https://developer.android.com/studio
   - Includes Android SDK, NDK, and Java JDK

2. **Android SDK**
   - Min SDK: 30
   - Compile SDK: 34

### Environment Variables (if building from command line)
```bash
# Windows
set ANDROID_HOME=C:\Users\YourUsername\AppData\Local\Android\Sdk
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr

# macOS/Linux
export ANDROID_HOME=$HOME/Library/Android/sdk  # or ~/Android/Sdk on Linux
export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
```

## Building the Project 🔨

### Option 1: Using Android Studio (Recommended)

1. **Open Project**
   ```
   File → Open → Select SmartAware folder
   ```

2. **Sync Gradle**
   - Android Studio will automatically sync Gradle files
   - Wait for the sync to complete

3. **Build APK**
   ```
   Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```
   
4. **Install on Device/Emulator**
   ```
   Run → Run 'app'
   ```

### Option 2: Command Line

1. **Set Environment Variables** (see above)

2. **Build Debug APK**
   ```bash
   # Windows
   .\gradlew.bat assembleDebug

   # macOS/Linux
   ./gradlew assembleDebug
   ```

3. **Output Location**
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

4. **Install APK**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## Permissions 🔒

The app requests **minimal, safe permissions**:

✅ **ACCESS_NETWORK_STATE** - Check network connectivity  
✅ **ACCESS_WIFI_STATE** - Get WiFi state info  
✅ **CHANGE_WIFI_STATE** - Get WiFi signal strength  
✅ **FOREGROUND_SERVICE** - Background monitoring  
✅ **POST_NOTIFICATIONS** - Send notifications (Android 13+)  
✅ **READ_PHONE_STATE** - Detect cellular network type  
✅ **INTERNET** - (Not used, but standard for network apps)



## How It Works 🔍

### 1. Network Monitoring
- **NetworkMonitorService**: Foreground service that runs continuously
- **NetworkStateReceiver**: BroadcastReceiver that listens for network state changes
- Triggers notifications when WiFi/Cellular connects

### 2. Periodic Reminders
- **WiFiReminderWorker**: WorkManager periodic task (every 6 hours)
- Checks if WiFi is connected
- Sends reminder if WiFi hasn't been used

### 3. Event Logging
- All network events are stored in Room database
- Events include timestamps and details (signal strength, network type)
- Dashboard displays recent 100 events

## Usage 📖

1. **Launch App**
   - Grant notification permission when prompted
   - App starts monitoring automatically

2. **Dashboard**
   - View current network status
   - See WiFi signal strength (if connected)
   - Scroll through event logs

3. **Background Monitoring**
   - App monitors network even when closed
   - Persistent notification shows "Monitoring network status..."
   - Notifications appear when network changes

4. **Periodic Reminders**
   - Every 6 hours, checks if WiFi is being used
   - Sends gentle reminder to turn on WiFi for battery savings


## Troubleshooting 🔧

### Build Issues

**"JAVA_HOME not set"**
```bash
# Find your Android Studio JDK path
# Windows: C:\Program Files\Android\Android Studio\jbr
# macOS: /Applications/Android Studio.app/Contents/jbr/Contents/Home
# Linux: /opt/android-studio/jbr

# Set the environment variable
set JAVA_HOME=<path-to-jdk>  # Windows
export JAVA_HOME=<path-to-jdk>  # macOS/Linux
```

**"SDK location not found"**
- Create `local.properties` in project root:
  ```
  sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
  ```

### Runtime Issues

**Notifications not appearing**
- Grant notification permission in app settings
- Check Do Not Disturb mode

**Service stops after app is killed**
- Disable battery optimization for SmartAware
- Settings → Apps → SmartAware → Battery → Unrestricted

**Events not logging**
- Ensure app has network state permissions
- Check if service is running (persistent notification should be visible)

## Future Enhancements 💡

- [ ] Export logs to CSV
- [ ] Customizable reminder frequency
- [ ] WiFi network name in logs
- [ ] Data usage tracking
- [ ] Dark/Light theme toggle
- [ ] Log filtering by date range


