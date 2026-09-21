# Graph Report - Seall  (2026-09-21)

## Corpus Check
- 30 files · ~18,457 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 29 file(s) not represented in the graph (top: .bin 10, .lock 7, .properties 7)

## Summary
- 183 nodes · 377 edges · 17 communities (10 shown, 3 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 4 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `68e6ae4b`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Ingredient
- OrderViewModel
- AGENTS.md
- StocksTab.kt
- OrderRepository
- gradlew
- Order
- ArchivesTab.kt
- MainScreen
- SeallDatabase
- DashboardTab.kt
- StockItem
- CsvHelper

## God Nodes (most connected - your core abstractions)
1. `Order` - 46 edges
2. `OrderViewModel` - 30 edges
3. `OrderRepository` - 27 edges
4. `Ingredient` - 26 edges
5. `StockItem` - 24 edges
6. `OrderDao` - 19 edges
7. `MainScreen()` - 11 edges
8. `IngredientDao` - 10 edges
9. `DashboardTab()` - 9 edges
10. `StockDao` - 8 edges

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

## Communities (17 total, 3 thin omitted)

### Community 0 - "Ingredient"
Cohesion: 0.19
Nodes (3): IngredientDao, Flow, Ingredient

### Community 1 - "OrderViewModel"
Cohesion: 0.12
Nodes (12): AndroidViewModel, Color, Modifier, MetricCard(), OrderRowCard(), TestThemeScreen(), OrderViewModel, Factory (+4 more)

### Community 3 - "StocksTab.kt"
Cohesion: 0.39
Nodes (8): IngredientCard(), Modifier, StockCard(), StockFormDialog(), StocksTab(), StocksTabSegment, INGREDIENTS, PRODUCTS

### Community 6 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 9 - "Order"
Cohesion: 0.13
Nodes (7): Flow, OrderDao, Order, Modifier, OrderCard(), HomeTab(), Modifier

### Community 10 - "ArchivesTab.kt"
Cohesion: 0.27
Nodes (6): ArchivedDay, ArchivedDayCard(), ArchivedDayDetailView(), ArchivesTab(), Modifier, DateUtils

### Community 11 - "MainScreen"
Cohesion: 0.14
Nodes (14): MainActivity, QuickEditDialog(), RapidEntryWizardModal(), AppTab, ARCHIVES, DASHBOARD, HOME, STOCKS (+6 more)

### Community 12 - "SeallDatabase"
Cohesion: 0.38
Nodes (3): SeallDatabase, Context, RoomDatabase

### Community 13 - "DashboardTab.kt"
Cohesion: 0.33
Nodes (11): AnalyticsHeroCard(), AnalyticsMiniCard(), ClientSettlement, DashboardPeriod, LIFETIME, TODAY, DashboardTab(), DebtorRowCard() (+3 more)

### Community 14 - "StockItem"
Cohesion: 0.19
Nodes (3): Flow, StockDao, StockItem

## Knowledge Gaps
- **9 isolated node(s):** `HOME`, `DASHBOARD`, `ARCHIVES`, `STOCKS`, `TODAY` (+4 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 32 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Order` connect `Order` to `OrderViewModel`, `OrderRepository`, `ArchivesTab.kt`, `MainScreen`, `SeallDatabase`, `DashboardTab.kt`, `CsvHelper`?**
  _High betweenness centrality (0.380) - this node is a cross-community bridge._
- **Why does `OrderViewModel` connect `OrderViewModel` to `Ingredient`, `Order`, `MainScreen`, `StockItem`?**
  _High betweenness centrality (0.192) - this node is a cross-community bridge._
- **Why does `Ingredient` connect `Ingredient` to `OrderViewModel`, `StocksTab.kt`, `OrderRepository`, `ArchivesTab.kt`, `SeallDatabase`, `DashboardTab.kt`?**
  _High betweenness centrality (0.192) - this node is a cross-community bridge._
- **What connects `HOME`, `DASHBOARD`, `ARCHIVES` to the rest of the system?**
  _9 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `OrderViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.12 - nodes in this community are weakly interconnected._
- **Should `Order` be split into smaller, more focused modules?**
  _Cohesion score 0.1330049261083744 - nodes in this community are weakly interconnected._
- **Should `MainScreen` be split into smaller, more focused modules?**
  _Cohesion score 0.14285714285714285 - nodes in this community are weakly interconnected._