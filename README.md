# Osmos Ads SDK Integration - Native Android App

A production-ready native Android application built in Kotlin showcasing integration with the **Osmos Ads system**. The project is designed using **Clean Architecture** patterns, **MVVM (ViewModel + StateFlow)** state management, and **Dagger Hilt** dependency injection. It dynamically fetches display banner ads, renders them respecting aspect ratio, tracks 50% visibility impressions, attributes click events, and provides a real-time event log stream across three interactive Compose-based tabs.

---

## 🛠️ Setup & Run

### Prerequisites
- Android Studio (Ladybug or higher)
- JDK 17
- Android SDK (Min SDK 21, Target SDK 34)
- An active Android Emulator or Physical Device connected via ADB
- Gradle version 8.8+

### Build & Run Instructions
1. Clone the repository and navigate to the project directory:
   ```bash
   cd ads_sdk_integration_android
   ```
2. Build the project using Gradle:
   ```bash
   ./gradlew.bat assembleDebug
   ```
3. Install the application on a connected device:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
4. Start the application:
   ```bash
   adb shell am start -n com.devagarwal.ads_sdk_integration_android/.MainActivity
   ```

---

## 📂 Architecture Explanation

The project is structured under strict **Clean Architecture** guidelines:

```
app/src/main/java/com/devagarwal/ads_sdk_integration_android/
├── core/                 # Shared constants, DI modules, error models, and logging utilities
│   ├── constants/        # ApiConstants, AppConstants
│   ├── error/            # Failure, ServerFailure, NetworkFailure
│   ├── logger/           # AppLogger (SharedFlow-driven logging)
│   └── utils/            # Result utility monad
├── sdk/                  # Modular SDK wrapper services (Ad fetch, Event tracker, Initializer)
├── analytics/            # Consolidated analytics logging layer
└── features/
    └── ads/              # Ads feature module
        ├── data/         # Remote DataSource, DTO models (AdModel, ElementModel, AdsResponseModel)
        ├── domain/       # Repository interface, Use Cases (FetchBannerAdUseCase, etc.)
        └── presentation/ # ViewModels, tab layouts, and reusable Compose components
```

- **Domain Layer**: Contains pure business logic and entity classes (`AdEntity`). Completely independent of third-party frameworks.
- **Data Layer**: Translates JSON DTOs (which vary in nesting structure) into clean domain entities.
- **SDK Layer**: Isolates the third-party native Osmos SDK dependencies.
- **Presentation Layer**: Implements **MVVM** using **Jetpack Compose**, **StateFlow** for state preservation, and **Dagger Hilt** for dependency injection.

---

## 🔍 Core Requirement Implementations

### 1. SDK Integration & Configuration
- **Modular Initialization**: Isolated in [`OsmosInitializer.kt`](app/src/main/java/com/devagarwal/ads_sdk_integration_android/sdk/OsmosInitializer.kt) with singleton lifecycle management.
- **Parameters configured**:
  - `CLIENT_ID = "10088010"`
  - `PRODUCT_ADS_HOST = "demo.o-s.io"`
  - `DISPLAY_ADS_HOST = "demo-ba.o-s.io"`

### 2. How Ad Fetching Works (AU-Based)
Ad fetching requests are handled using the Osmos SDK `fetchDisplayAdsWithAu` method:
- **Request Parameters**:
  - `CLI_UBID = "Any"`
  - `PAGE_TYPE = "demo_page"`
  - `AD_UNIT = "banner_ads"`
- **Recursive Parsing**:
  The SDK maps the response payload and extracts `ads.banner_ads[0]`. The mapping extracts:
  - `elements.value` → Ad Image URL
  - `elements.destination_url` → Landing URL (with tracking click fallback)
  - `impression_tracking_url` → Direct impression server ping URL
  - `click_tracking_url` → Direct click server ping URL

### 3. Aspect Ratio Banner Rendering
- **ImageView Rendering**: Implemented using the [`BannerAdWidget`](app/src/main/java/com/devagarwal/ads_sdk_integration_android/features/ads/presentation/component/BannerAdWidget.kt) utilizing `SubcomposeAsyncImage` from **Coil** for high-performance visual loading.
- **Aspect Ratio Locking**: Rather than stretching or compressing the image, the widget dynamically calculates the ratio as `width / height` and applies Compose's `Modifier.aspectRatio(ratio)` to perfectly display the ad.

### 4. How Impression Logic is Handled (50% Visibility)
- **Helper**: Implemented as a reusable wrapper in [`AdVisibilityWrapper.kt`](app/src/main/java/com/devagarwal/ads_sdk_integration_android/features/ads/presentation/component/AdVisibilityWrapper.kt).
- **50% Detector**: Tracks Composable viewport coordinates relative to the window visible display frame using `onGloballyPositioned`. It triggers when `visibleRatio >= 0.5f`.
- **Once-Per-Session Lock**: Tracks whether the impression has fired in its local state (`impressionFired`). Once visibility crosses 50%, it locks, fires the callback, and prevents further triggers.
- **Impression Trigger**: Registers the SDK impression event (`registerAdImpressionEvent`) and performs a concurrent, direct background HTTP GET ping to `impression_tracking_url` using OkHttp.

### 5. How Click Tracking is Handled
When the ad is tapped:
- **Event Dispatch**: Triggers the SDK native click event (`registerAdClickEvent`) and performs a concurrent background HTTP GET ping to `click_tracking_url`.
- **Landing Redirection**: Launches an implicit `Intent(Intent.ACTION_VIEW)` to open the landing page URL in the device's external web browser.

### 6. Event Logging & Diagnostics
- **Logcat**: Logged using the standard Android logging utility with the tag `OsmosDemo`.
- **Interactive UI Event Console**: A dedicated full-screen logs terminal tab is integrated. It uses a reactive log stream driven by `AppLogger`'s `MutableSharedFlow` to render real-time logs with level chips, search queries, copy to clipboard, and log clear triggers.
- **Tab-Based Verifier**: A diagnostics screen displays live status cards (impression and click checkbox indicators) and the exact URLs parsed by the SDK.

### 7. Error Handling & Resilience ("Ad not available")
- **Failures Handled**: SDK initialization exceptions, empty responses (no ads returned), network timeouts, and invalid/missing payload data.
- **Fallback UI**: If an error occurs, a fallback screen is displayed showing the user-friendly message **"Ad Loading Failed"** along with a clear description and a **Retry** button.

### 8. State Preservation across Tab Navigation
- **Replicating IndexedStack**: In Jetpack Compose, conditional visibility (`if (currentIndex == X)`) destroys composables, causing them to lose scroll and logs list states.
- **Parity Hack**: Instead, all tabs remain composed. Inactive tabs are collapsed using `Modifier.requiredSize(0.dp).clipToBounds()`. The `requiredSize` overrides layout dimensions so inactive screens do not intercept touch gestures, and `clipToBounds` prevents Compose from drawing children outside parent bounds.

---

## 📋 Assumptions Made

To build a robust integration, the following logical assumptions were established:
1. **Network Requirement**: Lack of network will route through a standard `Failure.NetworkFailure` and yield a friendly error fallback in the UI.
2. **Double Attribution Verification**: Direct HTTP GET requests to tracking URLs run concurrently with SDK native registering methods to guarantee event capture.
3. **Responsive Size Overrides**: If specific dimensions are omitted, a standard fallback `16:9` ratio is enforced to prevent layouts from breaking.
4. **UCLID Presence**: We assume each display ad contains a UCLID (either directly or embedded in URLs) to successfully log attribution events.
5. **Android 11+ App Intents**: Package query visibilities `<queries>` are declared in `AndroidManifest.xml` to support opening web URLs.

---

## ⚡ Challenges Faced & Resolutions

During integration, several critical issues were resolved to deliver a production-quality product:

### 1. Dagger Hilt Kotlin 2.2.0 Metadata Clash
- **The Challenge**: Using Kotlin `2.2.10` and Android Gradle Plugin (AGP) `8.9.0` caused Hilt annotation processing to crash with a bytecode parsing error, because Dagger Hilt's `kotlin-metadata-jvm` parser only supported Kotlin metadata up to version `2.1.0`.
- **The Resolution**: Downgraded Kotlin compiler version to `2.1.10` and AGP to `8.8.0` in `libs.versions.toml`, resolving Hilt compilation compatibility issues.

### 2. Inactive Tab Touch Interception in Compose
- **The Challenge**: Standard Compose layouts overlay screens. When using a size of `0.dp` to hide inactive views, tight parent constraints (like `fillMaxSize`) would override it, causing the console logs view to cover other tabs and intercept click gestures.
- **The Resolution**: Used `Modifier.requiredSize(0.dp)` to override parent constraints, making sure inactive layouts take exactly `0` width and height and let touches pass through.

### 3. Native Visual Overlap/Leak Bug
- **The Challenge**: Components from inactive screens (like icons and checkboxes) were still rendering on top of the active tab. This happened because Compose does not automatically clip layout contents. When a screen size collapsed to `0.dp`, its centered children still drew outside the bounds.
- **The Resolution**: Applied `.clipToBounds()` to the collapsed Box wrappers in `HomeScreen.kt`. This clips all children's drawing bounds to the `0x0` area, making them completely invisible.

### 4. Visibility Detector Premature Triggers
- **The Challenge**: The visibility wrapper evaluated a 100% visible ratio on startup and fired the impression instantly, before the ad expanded. This was because `SubcomposeAsyncImage` starts with a tiny default placeholder height (e.g., 36.dp) while loading. Since this small placeholder fits on screen, it triggered the impression immediately.
- **The Resolution**: Configured `BannerAdWidget` to track Coil's `onSuccess` loading state. We updated `AdVisibilityWrapper` to calculate the expected final height using the aspect ratio (`compWidth / expectedRatio`) and only allow the visibility check to trigger once the layout height settles to at least 50% of its final expected size.

### 5. Recursive JSON Map Parsing
- **The Challenge**: The SDK returns nested JSON payloads wrapped as strings. In Kotlin, parsing raw `JSONObject` and `JSONArray` instances does not automatically convert them to JVM collections, causing key/value type mismatch issues in the parsing logic.
- **The Resolution**: Added a recursive helper function `toKotlinType` inside `AdsResponseModel.kt` to recursively map JSON objects into standard JVM `Map` and `List` instances, enabling clean and type-safe data parsing.

### 6. Sealed Interface Inline Error
- **The Challenge**: Marking `Result.fold` as `inline` inside a sealed interface throws a compiler error because Kotlin does not support inline modifiers on virtual interface members.
- **The Resolution**: Moved `fold` to be a top-level extension function inside `Result.kt`, solving the compilation blocker.

---

## 🧪 Verification Plan

### Manual Verification Steps
1. Navigate to the **Ad Simulator** tab. Click **Load Display Ad**.
2. Scroll down slowly. Once the ad is at least 50% visible, the **Impression** checkbox in the **Ad Verifier** tab automatically turns green, and an impression is logged in the **Event Console** tab.
3. Click the ad. The browser will open the click tracking URL, and the **Click** checkbox in the **Ad Verifier** tab will turn green, logging the click in the **Event Console**.
