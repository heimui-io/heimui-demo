<div align="center">

<img src="art/heimui-banner.jpg" alt="HeimUI Banner" width="100%" />

# 🐾 HeimUI Demo Showcase
### High-Performance Server-Driven UI (SDUI) for Compose Multiplatform

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.12.0-4285F4.svg?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/compose-multiplatform/)
[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?logo=android&logoColor=white)](https://android.com)
[![iOS](https://img.shields.io/badge/Platform-iOS-000000.svg?logo=apple&logoColor=white)](https://apple.com)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

</div>

---

## 🌟 Overview

**HeimUI Demo** is the official showcase application demonstrating the power, speed, and developer experience of the **HeimUI Server-Driven UI Engine** on **Android** and **iOS** using 100% shared Kotlin and Compose Multiplatform code.

With **HeimUI**, your backend controls layout hierarchies, navigation, styles, and validation rules in real time without requiring app store updates or re-releasing client binaries.

---

## 📱 Showcase Verticals Included

The demo app includes 5 production-grade SDUI use cases accessible from the top tab bar:

| Vertical | Use Case & SDUI Capabilities |
| :--- | :--- |
| 🛒 **Store & Deals (E-Commerce)** | Flash sale promotional hero cards, dynamic horizontal category filters (`LazyRow`), product cards, and bottom sheet checkout confirmation modals. |
| 💳 **Fintech KYC Verification** | Step-by-step onboarding with dynamic field validation, LUHN & document regex checkers, and conditional `visible_if` visibility (Company Tax ID vs Personal ID). |
| 🍔 **Food Delivery & Feed** | Restaurant list with ratings, badge tags, and a live order tracker card showing real-time delivery status steps. |
| 💎 **Paywall & Subscriptions** | Dynamic subscription plans with monthly/yearly switch, pricing tier comparison checklist, and instant OTA trial activation. |
| ⚡ **Component Storybook** | Live catalog of all native primitives (`Button`, `Card`, `Badge`, `TextField`, `Switch`, `Divider`, `Spacer`) + custom plugin widgets. |

---

## 🛠️ How to Run the Demo

### Prerequisites
* **Android Studio** (Ladybug / Meerkat or newer) or **IntelliJ IDEA**.
* **JDK 17** or **JDK 21**.
* **Xcode 16+** (for building and running on the iOS Simulator).

### 1. Build and Run on Android
```bash
./gradlew :androidApp:installDebug
```

### 2. Build and Run on iOS (Simulator)
Open `iosApp/iosApp.xcodeproj` in Xcode and press `Cmd + R`, or build via CLI:
```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -destination 'generic/platform=iOS Simulator' build
```

---

## 🚀 How to Integrate HeimUI in Your App

### 1. Add Repository & Dependency
In your project's `settings.gradle.kts` and `shared/build.gradle.kts`:
```kotlin
// shared/build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.heimui:heimui-core:0.0.1-alpha")
        }
    }
}
```

### 2. Render a Server-Driven Screen
```kotlin
@Composable
fun MyScreen() {
    HeimTheme(
        brandTokens = HeimBrandTokens(
            colors = mapOf(
                "primary" to Color(0xFF00E5FF),
                "surface" to Color(0xFF161D2F)
            )
        )
    ) {
        HeimScreen(
            screenId = "home_dashboard",
            onAction = { action ->
                println("Executed action: $action")
            }
        )
    }
}
```

---

## 🛡️ Enterprise Security & Resilience Architecture
* 🔒 **HMAC-SHA256 Payload Verification:** Cryptographic signature check over raw wire bytes to prevent MITM tampering.
* 🛑 **Pre-Parse Depth Scanner (`HeimPayloadGuard`):** Linear $O(N)$ depth scanner rejecting deep recursion (> 64 levels) before deserialization, protecting iOS against stack overflows.
* ⚡ **Circuit Breaker:** State machine guarding backend services against retry storms during outages.
* 📦 **Resilient Primitives:** Automatic parsing tolerance for strings in numeric fields (`"padding": "16"`).

---

## 📄 Brand & Design Guidelines
Review our design system, typography, and color tokens in [`art/BRAND_GUIDELINES.md`](art/BRAND_GUIDELINES.md).

---

## ⚖️ License
Licensed under the [Apache License, Version 2.0](LICENSE).
