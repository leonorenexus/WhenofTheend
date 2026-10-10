# WHEN OF THE END
Minimalist emergency check-in app: a 48-hour inactivity countdown.
Native Android · Kotlin · Jetpack Compose · Material 3 · minSdk 26 · package `com.when.oftheend`

> Not compiled in the authoring environment (no network for Gradle). First build happens on GitHub Actions.
> If it fails, send the build log and only the broken file(s) will be fixed.

## 1. Design system
- Colors: Black #000000, Deep #0A0A0A, Charcoal #171717, White #FFFFFF, Soft #F5F5F5, Glass = white 10-14%, Glass border = white ~22%. Greys (#2E2E2E, #6E6E6E) are used only for block shadows. No accent colors.
- Neo-Brutalism: 3-4 dp borders, square corners, oversized Black-weight uppercase type, offset block shadows (6 dp), buttons translate into their shadow when pressed, asymmetric 2-line title.
- Type: SansSerif Black for headings/buttons, Monospace for the countdown and technical labels.
- 3D: procedural rotating wireframe hourglass drawn on Canvas (lathe profile, perspective, depth-faded lines, floor shadow, orbit ring + moon). Bright rings are the "sand" and move from upper to lower bulb as the window elapses. One rotation per 60 s; fully static when reduced motion is on.
- Glass: translucent gradient panels with a thin white border layered behind the hero. Limitation: Compose has no backdrop blur without extra libraries, so no blur is applied.
- Status is always icon + text (check-circle ACTIVE, warning CHECK-IN REQUIRED, X OVERDUE), never color alone.

## 2. Repository structure
```
.github/workflows/build.yml
settings.gradle.kts  build.gradle.kts  gradle.properties  .gitignore
app/build.gradle.kts
app/src/main/AndroidManifest.xml
app/src/main/res/values/{strings,colors,themes}.xml
app/src/main/res/drawable/ic_launcher_foreground.xml
app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
app/src/main/java/com/when/oftheend/
  MainActivity.kt  AppViewModel.kt  Store.kt  Model.kt  Format.kt  Actions.kt
  Theme.kt  Components.kt  Hero.kt
  HomeScreen.kt  OverdueScreen.kt  HistoryScreen.kt  ContactsScreen.kt  SettingsScreen.kt
```
`when` is a Kotlin keyword, so every file declares ``package com.`when`.oftheend`` (backticks). The applicationId/namespace is plain `com.when.oftheend`. All files share one package, so no imports of it are needed.
Toolchain: JDK 17, Gradle 8.9 (provisioned by the workflow, no wrapper needed), AGP 8.5.2, Kotlin 2.0.20, compileSdk/targetSdk 34, Compose BOM 2024.09.00.

## 3. Implemented vs future
Implemented: persistent check-in timestamp; countdown from stored deadline; check-in resets the deadline; status recomputed on every open; ACTIVE / CHECK-IN REQUIRED (final 6 h, or last quarter of short intervals) / OVERDUE; corrupted-data recovery (resets and shows a notice); history (last 200); trusted contacts (max 5, explicit consent required to save); tap-to-call/text from the Overdue screen (opens dialer/SMS composer, user must press call/send); settings (interval 12/24/48/72 h, reduce motion, haptics, clear history, erase app data); no permissions at all.
Future / NOT implemented: reminder notifications, background alarms, reboot receiver, automatic messages to contacts. The stored anchors below are the foundation for them.
Never: factory reset or erasing anything outside the app. An ordinary app cannot do that without device-owner privileges, and this app does not try.

## 4. Time handling
Each check-in stores wall time, `SystemClock.elapsedRealtime()` and the device boot counter (`Settings.Global.BOOT_COUNT`).
- Same boot: elapsed = elapsedRealtime - stored anchor. Monotonic, so changing the system clock cannot move the deadline.
- After reboot (or no boot counter): elapsed = now - stored wall time (clamped at 0). A clock set backwards can delay OVERDUE; a clock set forwards can hasten it. This is a limitation.
- Nothing runs while the phone is off or the app is closed. Overdue is detected when the app is next opened. Exact enforcement is NOT guaranteed.
- Future: `BOOT_COMPLETED` receiver + `AlarmManager` (exact-alarm permission on Android 12+) to re-arm reminders; WorkManager as a fallback.

## 5. Build on an Android phone (GitHub only)
1. In the GitHub app/website, create a new repository (e.g. `when-of-the-end`).
2. Unzip nothing: upload `WhenOfTheEnd-src.zip` as-is to the repo root (Add file > Upload files). The workflow unpacks it.
3. Add file > Create new file. Name it `.github/workflows/build.yml` (typing the slashes creates the folders). Paste the workflow from `build.yml`, then Commit.
4. Open the Actions tab. The "Build Debug APK" run starts on that commit (or use Run workflow). Wait ~5-8 min.
5. Open the finished run > Artifacts > `WhenOfTheEnd-debug-apk` (a zip containing `app-debug.apk`). Download it, extract it with your Files app.
6. Open the APK and allow "Install unknown apps" for your browser/Files app when asked. Debug builds are unsigned for release; Play Protect may warn.
If you later upload the project unzipped with real folders, the workflow detects `settings.gradle.kts` in the root and builds it directly.

## 6. Verification checklist
- [ ] Actions run is green; `app-debug.apk` artifact exists.
- [ ] First launch: countdown starts near 48:00:00 (shows 47:59:59 after 1 s), state ACTIVE.
- [ ] "I AM STILL HERE" resets to ~48:00:00, updates LAST CHECK-IN, adds a History entry; button visibly presses into its shadow.
- [ ] Close and reopen: remaining time is still correct. Force-stop: same.
- [ ] Settings > interval 12H: state switches according to elapsed time; back to 48H restores.
- [ ] To see OVERDUE quickly: check in, then set the system date 3 days ahead and reboot (wall-clock path), or wait out the interval.
- [ ] OVERDUE screen shows elapsed time, check-in works, contact buttons open dialer/SMS composer without sending.
- [ ] Contacts: Save is disabled until name, phone and consent are set.
- [ ] Reduce Motion stops the hourglass; Android "Remove animations" does the same.
- [ ] Settings > Erase all app data resets app only.
- [ ] TalkBack reads the countdown as hours/minutes/seconds and status as text.
