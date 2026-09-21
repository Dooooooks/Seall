# Graph Report - Seall  (2026-09-21)

## Corpus Check
- 33 files · ~22,468 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 7 file(s) not represented in the graph (top: .xml 2, .properties 2, (none) 1)

## Summary
- 214 nodes · 446 edges · 18 communities (11 shown, 3 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 4 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `7ed53355`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Ingredient
- OrderViewModel
- AGENTS.md
- SeallDatabase
- OrderRepository
- gradlew
- Order
- ArchivesTab.kt
- MainScreen
- StockIngredient
- DashboardTab.kt
- StockItem
- OrderItem
- TestThemeScreen.kt

## God Nodes (most connected - your core abstractions)
1. `Order` - 46 edges
2. `OrderViewModel` - 36 edges
3. `OrderRepository` - 33 edges
4. `StockItem` - 27 edges
5. `Ingredient` - 23 edges
6. `StockIngredient` - 23 edges
7. `OrderDao` - 19 edges
8. `MainScreen()` - 11 edges
9. `IngredientDao` - 10 edges
10. `StockIngredientDao` - 10 edges

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

## Communities (18 total, 3 thin omitted)

### Community 0 - "Ingredient"
Cohesion: 0.19
Nodes (3): IngredientDao, Flow, Ingredient

### Community 1 - "OrderViewModel"
Cohesion: 0.13
Nodes (7): AndroidViewModel, OrderViewModel, Factory, Application, StateFlow, T, ViewModelProvider

### Community 3 - "SeallDatabase"
Cohesion: 0.28
Nodes (3): SeallDatabase, Context, RoomDatabase

### Community 6 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 9 - "Order"
Cohesion: 0.11
Nodes (8): Flow, OrderDao, Order, Modifier, OrderCard(), HomeTab(), Modifier, CsvHelper

### Community 10 - "ArchivesTab.kt"
Cohesion: 0.27
Nodes (6): ArchivedDay, ArchivedDayCard(), ArchivedDayDetailView(), ArchivesTab(), Modifier, DateUtils

### Community 11 - "MainScreen"
Cohesion: 0.17
Nodes (14): MainActivity, QuickEditDialog(), RapidEntryWizardModal(), AppTab, ARCHIVES, DASHBOARD, HOME, STOCKS (+6 more)

### Community 12 - "StockIngredient"
Cohesion: 0.20
Nodes (3): Flow, StockIngredientDao, StockIngredient

### Community 13 - "DashboardTab.kt"
Cohesion: 0.33
Nodes (11): AnalyticsHeroCard(), AnalyticsMiniCard(), ClientSettlement, DashboardPeriod, LIFETIME, TODAY, DashboardTab(), DebtorRowCard() (+3 more)

### Community 14 - "StockItem"
Cohesion: 0.14
Nodes (9): Flow, StockDao, StockItem, Modifier, QuickStockQuantityDialog(), StockCard(), StockFormDialog(), StockIngredientFormDialog() (+1 more)

### Community 16 - "TestThemeScreen.kt"
Cohesion: 0.60
Nodes (5): Color, Modifier, MetricCard(), OrderRowCard(), TestThemeScreen()

## Knowledge Gaps
- **7 isolated node(s):** `HOME`, `DASHBOARD`, `ARCHIVES`, `STOCKS`, `TODAY` (+2 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 35 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Order` connect `Order` to `OrderViewModel`, `SeallDatabase`, `OrderRepository`, `ArchivesTab.kt`, `MainScreen`, `DashboardTab.kt`, `StockItem`, `TestThemeScreen.kt`?**
  _High betweenness centrality (0.363) - this node is a cross-community bridge._
- **Why does `OrderViewModel` connect `OrderViewModel` to `Ingredient`, `Order`, `MainScreen`, `StockIngredient`, `StockItem`, `TestThemeScreen.kt`?**
  _High betweenness centrality (0.206) - this node is a cross-community bridge._
- **Why does `OrderRepository` connect `OrderRepository` to `Ingredient`, `OrderViewModel`, `Order`, `StockIngredient`, `StockItem`?**
  _High betweenness centrality (0.153) - this node is a cross-community bridge._
- **What connects `HOME`, `DASHBOARD`, `ARCHIVES` to the rest of the system?**
  _7 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `OrderViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.13438735177865613 - nodes in this community are weakly interconnected._
- **Should `Order` be split into smaller, more focused modules?**
  _Cohesion score 0.11363636363636363 - nodes in this community are weakly interconnected._
- **Should `StockItem` be split into smaller, more focused modules?**
  _Cohesion score 0.14153846153846153 - nodes in this community are weakly interconnected._