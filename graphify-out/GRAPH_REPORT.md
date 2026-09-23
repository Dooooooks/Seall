# Graph Report - Seall  (2026-09-23)

## Corpus Check
- 36 files · ~27,008 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 7 file(s) not represented in the graph (top: .xml 2, .properties 2, (none) 1)

## Summary
- 267 nodes · 583 edges · 18 communities (10 shown, 4 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 4 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `91a01e3d`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Ingredient
- OrderViewModel
- AGENTS.md
- OrderRepository
- gradlew
- Order
- ArchivesTab.kt
- MainScreen.kt
- StockIngredient
- DashboardTab.kt
- StockItem
- OrderItem
- CsvHelper
- ✨ Features

## God Nodes (most connected - your core abstractions)
1. `Order` - 58 edges
2. `StockItem` - 50 edges
3. `StockIngredient` - 41 edges
4. `OrderViewModel` - 40 edges
5. `OrderRepository` - 34 edges
6. `Ingredient` - 23 edges
7. `OrderDao` - 19 edges
8. `CsvHelper` - 14 edges
9. `OrderItem` - 13 edges
10. `MainScreen()` - 13 edges

## Surprising Connections (you probably didn't know these)
- `MainScreen()` --calls--> `ArchivesTab()`  [INFERRED]
  app/src/main/java/com/example/seall/ui/screens/MainScreen.kt → app/src/main/java/com/example/seall/ui/screens/ArchivesTab.kt
- `MainScreen()` --calls--> `DashboardTab()`  [INFERRED]
  app/src/main/java/com/example/seall/ui/screens/MainScreen.kt → app/src/main/java/com/example/seall/ui/screens/DashboardTab.kt
- `MainScreen()` --calls--> `HomeTab()`  [INFERRED]
  app/src/main/java/com/example/seall/ui/screens/MainScreen.kt → app/src/main/java/com/example/seall/ui/screens/HomeTab.kt
- `MainScreen()` --calls--> `StocksTab()`  [INFERRED]
  app/src/main/java/com/example/seall/ui/screens/MainScreen.kt → app/src/main/java/com/example/seall/ui/screens/StocksTab.kt
- `MainActivity` --references--> `OrderViewModel`  [EXTRACTED]
  app/src/main/java/com/example/seall/MainActivity.kt → app/src/main/java/com/example/seall/ui/viewmodel/OrderViewModel.kt

## Import Cycles
- None detected.

## Communities (18 total, 4 thin omitted)

### Community 0 - "Ingredient"
Cohesion: 0.12
Nodes (6): IngredientDao, Flow, SeallDatabase, Ingredient, Context, RoomDatabase

### Community 1 - "OrderViewModel"
Cohesion: 0.08
Nodes (12): AndroidViewModel, Color, Modifier, MetricCard(), OrderRowCard(), TestThemeScreen(), OrderViewModel, Factory (+4 more)

### Community 6 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 9 - "Order"
Cohesion: 0.11
Nodes (8): Flow, OrderDao, Order, Modifier, OrderCard(), HomeTab(), Modifier, IngredientCostHelperTest

### Community 10 - "ArchivesTab.kt"
Cohesion: 0.27
Nodes (6): ArchivedDay, ArchivedDayCard(), ArchivedDayDetailView(), ArchivesTab(), Modifier, DateUtils

### Community 11 - "MainScreen.kt"
Cohesion: 0.13
Nodes (18): MainActivity, QuickEditDialog(), RapidEntryWizardModal(), AppTab, ARCHIVES, DASHBOARD, HOME, STOCKS (+10 more)

### Community 12 - "StockIngredient"
Cohesion: 0.18
Nodes (9): Flow, StockIngredientDao, StockIngredient, Modifier, QuickStockQuantityDialog(), StockCard(), StockFormDialog(), StockIngredientFormDialog() (+1 more)

### Community 13 - "DashboardTab.kt"
Cohesion: 0.32
Nodes (12): AnalyticsHeroCard(), AnalyticsMiniCard(), ClientSettlement, DashboardPeriod, LIFETIME, TODAY, DashboardTab(), DebtorRowCard() (+4 more)

### Community 14 - "StockItem"
Cohesion: 0.16
Nodes (7): Flow, StockDao, StockItem, IngredientCostHelper, IngredientUsageSummary, MutableUsage, ProductRecipeSummary

### Community 18 - "✨ Features"
Cohesion: 0.12
Nodes (15): 💾 Backup & Data Portability (CSV), Building & Running, 👥 Customer Tabs & Settlement (Debtor Tracking), 🗄️ Database Architecture, 📦 Dynamic Stock & Inventory Control, ✨ Features, 🚀 Getting Started, 📖 Overview (+7 more)

## Knowledge Gaps
- **19 isolated node(s):** `HOME`, `DASHBOARD`, `ARCHIVES`, `STOCKS`, `TODAY` (+14 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 51 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **4 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Order` connect `Order` to `Ingredient`, `OrderViewModel`, `OrderRepository`, `ArchivesTab.kt`, `MainScreen.kt`, `StockIngredient`, `DashboardTab.kt`, `StockItem`, `CsvHelper`?**
  _High betweenness centrality (0.259) - this node is a cross-community bridge._
- **Why does `StockItem` connect `StockItem` to `Ingredient`, `OrderViewModel`, `OrderRepository`, `Order`, `ArchivesTab.kt`, `MainScreen.kt`, `StockIngredient`, `DashboardTab.kt`, `CsvHelper`?**
  _High betweenness centrality (0.184) - this node is a cross-community bridge._
- **Why does `OrderViewModel` connect `OrderViewModel` to `Ingredient`, `Order`, `MainScreen.kt`, `StockIngredient`, `StockItem`?**
  _High betweenness centrality (0.154) - this node is a cross-community bridge._
- **What connects `HOME`, `DASHBOARD`, `ARCHIVES` to the rest of the system?**
  _19 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Ingredient` be split into smaller, more focused modules?**
  _Cohesion score 0.1168091168091168 - nodes in this community are weakly interconnected._
- **Should `OrderViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.08108108108108109 - nodes in this community are weakly interconnected._
- **Should `Order` be split into smaller, more focused modules?**
  _Cohesion score 0.10588235294117647 - nodes in this community are weakly interconnected._