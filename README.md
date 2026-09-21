# Seall 🦭

> **A tailored, offline-first point-of-sale, stock tracking, and profit analytics companion crafted specifically for small baking and confectionery businesses.**

---

## 📖 Overview

**Seall** is a lightweight, mobile-first Android application designed to streamline daily sales and inventory operations for artisan bakers and small confection shops. Built from the ground up to solve the real-world friction of order taking, recipe ingredient cost tracking, customer credit settlements, and accurate net profit calculation—all without requiring an internet connection or subscription.

---

## ✨ Features

### ⚡ Rapid 3-Step Order Entry
- **Step 1: Customer Details** — Instant customer name entry with auto-focused inputs.
- **Step 2: Items & Pricing** — Dynamic product selector with inline `[+]` and `[-]` steppers, real-time stock cap enforcement (disables additions when out of stock or when max available inventory is reached), and automatic total calculation in ₱ (PHP).
- **Step 3: Payment Status** — One-tap selection between **Paid** and **Unpaid** with instant receipt summary.

### 📦 Dynamic Stock & Inventory Control
- Track on-hand quantities for every product (e.g., Brownies, Crinkles, Pastries).
- Quick stepper controls (`+` / `-`) directly on product cards for quick inventory adjustments.
- Dedicated stock quantity editor modal for bulk count updates.
- Real-time stock gating: prevents creating orders exceeding available inventory.

### 🧁 Recipe Ingredient Costing
- Associate recipe ingredients (e.g., Cocoa Powder, Eggs, Flour) directly to specific stock products.
- Track ingredient quantities and unit costs (₱) to establish true production cost per item.

### 📊 Real-Time Sales & Profit Analytics
- **Today vs. Lifetime Modes**: Switch between daily performance and cumulative sales records.
- **Net Earnings Calculation**: Automatically calculates true net profit by subtracting total ingredient expenses from gross sales:
  $$\text{Net Profit} = \text{Gross Revenue} - \text{Total Ingredient Costs}$$
- **Metrics Breakdown**: Displays Gross Revenue, Ingredient Expenses, Total Items Sold, and Paid vs. Unpaid revenue.

### 👥 Customer Tabs & Settlement (Debtor Tracking)
- Automatically aggregates unpaid orders grouped by customer name.
- Highlights pending balances and order counts.
- **One-Tap Settle**: Settle all outstanding tabs for a customer with a single confirmation.

### 💾 Backup & Data Portability (CSV)
- **Brand-Tap Sidebar**: Tap the **Seall** seal logo or title in the top bar to open a hidden management drawer.
- **Full Backup**: One-tap export and restoration of all orders, products, and ingredient records as a standard CSV format.
- Compatible with spreadsheet tools like Microsoft Excel and Google Sheets.

### 🎨 Polished Mobile Experience
- **Adaptive Theming**: Full support for both Light and Dark modes with high-contrast, accessible palettes.
- **Keyboard-Aware Layouts**: Built with `adjustResize` and IME insets so forms and save buttons remain fully accessible when the virtual keyboard is open.
- **100% Offline-First**: Zero tracking, zero cloud dependencies, and zero data leakage. All data stays local to the device.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel) with Kotlin Coroutines & `StateFlow`
- **Local Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) (SQLite) with schema migrations
- **Tooling & Build**: Gradle (Kotlin DSL), Android Gradle Plugin 8.7+
- **Min SDK**: 26 (Android 8.0 Oreo) | **Target SDK**: 34 (Android 14)

---

## 🗄️ Database Architecture

The application persists data across four interconnected Room entities:

1. **`Order`** (`orders` table):
   - Stores sales transactions: `id`, `customerName`, `price`, `isPaid`, `itemsSummary`, `itemsJson`, `totalItemCount`, and timestamp.
2. **`StockItem`** (`stocks` table):
   - Stores catalog products, prices, and available inventory counts.
3. **`StockIngredient`** (`stock_ingredients` table):
   - Recipe components linked to a parent `StockItem` via foreign key cascading.
4. **`Ingredient`** (`ingredients` table):
   - General purchase logs and expense records.

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug (2024.2.1) or newer
- **JDK**: OpenJDK 17 or newer
- **Android SDK**: API level 34

### Building & Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Dooooooks/Seall.git
   cd Seall
   ```

2. **Assemble the Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

3. **Install on a connected device / emulator**:
   ```bash
   ./gradlew installDebug
   ```

4. **Run Unit Tests**:
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 🤍 Dedication

> *"For the person who is always doing her best, I hope that every day is a profit day!"*  
> — **Lloydie**
