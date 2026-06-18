# PDX Rail

**Live arrivals and vehicle locations for MAX Light Rail and Portland Streetcar.**  
No ads. No accounts. No subscriptions. Just the train.

[![Android](https://img.shields.io/badge/Android-Play_Store-3DDC84?logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=com.hzlgrn.pdxrail)
[![iOS](https://img.shields.io/badge/iOS-App_Store-000000?logo=apple&logoColor=white)](https://apps.apple.com/us/app/pdx-rail/id6759946671)
[![Web](https://img.shields.io/badge/Web-pdxrail.hzlgrn.com-1a4d99)](https://pdxrail.hzlgrn.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## What is this?

PDX Rail started with a simple problem: landing at PDX after a long flight and wanting to know, right now, when the Red Line was arriving without fighting through a cluttered app. So it got built.

It covers every MAX line (Blue, Red, Orange, Green, Yellow) and both Portland Streetcar loops (A Loop, B Loop, North/South). Tap a stop, see real arrivals and live vehicle positions on the map. That's the whole app.

---

## Platforms

This is a **Kotlin Multiplatform monorepo** targeting Android, iOS, and the web.

| Platform | Status | Source |
|---|---|---|
| Android | Live on Google Play | `app/` (KMP module) |
| iOS | Live on App Store | `iosApp/` + `app/` (KMP module) |
| Web | [pdxrail.hzlgrn.com](https://pdxrail.hzlgrn.com) | Static site |

---

## Tech Stack

| Layer | Library |
|---|---|
| UI | [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) 1.8.0 |
| Map | [MapLibre Compose](https://github.com/maplibre/maplibre-compose) 0.12.1 |
| Networking | [Ktor](https://github.com/ktorio/ktor) 3.4.1 (OkHttp on Android, Darwin on iOS) |
| Database | [SQLDelight](https://github.com/cashapp/sqldelight) 2.3.2 |
| DI | [Koin](https://github.com/InsertKoinIO/koin) 4.0.4 |
| Transit Data | [TriMet Developer API](https://developer.trimet.org) |

Kotlin 2.3.0 · Compose Material3 · minSdk 24 · iOS 16+

---

## Architecture

Two KMP modules share code across Android and iOS:

```
:app is Compose UI, ViewModel, navigation (commonMain + androidMain + iosMain)
:data is Repository, Ktor client, SQLDelight database, models
```

Data flows one way: the TriMet API → `PdxRailSystemRepository` → `PdxRailViewModel` → Compose UI. The ViewModel exposes `StateFlow`s for the map state, arrivals drawer, and stop selection. There are no Fragments, no View Binding, no NavGraph it's Compose all the way down.

The app ships with a pre-populated `pdxrail.db` (stop and line geometry) so the map renders immediately without a cold network round-trip.

---

## Getting Started

### Android

```bash
./gradlew app:assembleDebug       # build debug APK
./gradlew app:installDebug        # install to a connected device
./gradlew app:testDebugUnitTest   # run unit tests
```

Debug builds use placeholder API values and always produce `versionCode 1 / 0.0.0`. No keyring required.

### iOS

1. Open `iosApp/iosApp.xcodeproj` in Xcode.
2. Copy `iosApp/Secrets.xcconfig.example` → `iosApp/Secrets.xcconfig` and fill in your TriMet API key and base URL.
3. Build and run. Gradle builds the `ComposeApp` KMP framework and Xcode embeds it automatically.

### API Keys

Live arrivals require a [TriMet Developer API](https://developer.trimet.org) key. Add it to:
- **Android**: `com.hzlgrn.pdxrail.keyring` (see `com.hzlgrn.pdxrail.keyring.example`)
- **iOS**: `iosApp/Secrets.xcconfig`

Without a key the map tiles and stop geometry still load while live arrivals will fail.

---

## License

MIT see [LICENSE](LICENSE).

Transit data provided by [TriMet](https://trimet.org) under the [TriMet Developer API License](https://developer.trimet.org/terms_of_use.shtml).  
Full third-party credits in [LICENSES.md](LICENSES.md).
