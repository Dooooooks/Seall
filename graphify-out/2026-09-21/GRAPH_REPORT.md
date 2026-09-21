# Graph Report - Seall  (2026-09-21)

## Corpus Check
- 28 files · ~16,523 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 29 file(s) not represented in the graph (top: .bin 10, .lock 7, .properties 7)

## Summary
- 157 nodes · 313 edges · 16 communities (9 shown, 3 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 4 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `68e6ae4b`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- TestThemeScreen.kt
- OrderViewModel
- AGENTS.md
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
2. `OrderViewModel` - 26 edges
3. `StockItem` - 24 edges
4. `OrderRepository` - 20 edges
5. `OrderDao` - 19 edges
6. `MainScreen()` - 11 edges
7. `StockDao` - 8 edges
8. `DashboardTab()` - 8 edges
9. `AppTab` - 7 edges
10. `ArchivesTab()` - 7 edges

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

## Communities (16 total, 3 thin omitted)

### Community 0 - "TestThemeScreen.kt"
Cohesion: 0.60
Nodes (5): Color, Modifier, MetricCard(), OrderRowCard(), TestThemeScreen()

### Community 1 - "OrderViewModel"
Cohesion: 0.13
Nodes (7): AndroidViewModel, OrderViewModel, Factory, Application, StateFlow, T, ViewModelProvider

### Community 6 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 9 - "Order"
Cohesion: 0.14
Nodes (7): Flow, OrderDao, Order, Modifier, OrderCard(), HomeTab(), Modifier

### Community 10 - "ArchivesTab.kt"
Cohesion: 0.27
Nodes (6): ArchivedDay, ArchivedDayCard(), ArchivedDayDetailView(), ArchivesTab(), Modifier, DateUtils

### Community 11 - "MainScreen"
Cohesion: 0.17
Nodes (14): MainActivity, QuickEditDialog(), RapidEntryWizardModal(), AppTab, ARCHIVES, DASHBOARD, HOME, STOCKS (+6 more)

### Community 12 - "SeallDatabase"
Cohesion: 0.38
Nodes (3): SeallDatabase, Context, RoomDatabase

### Community 13 - "DashboardTab.kt"
Cohesion: 0.33
Nodes (11): AnalyticsHeroCard(), AnalyticsMiniCard(), ClientSettlement, DashboardPeriod, LIFETIME, TODAY, DashboardTab(), DebtorRowCard() (+3 more)

### Community 14 - "StockItem"
Cohesion: 0.17
Nodes (7): Flow, StockDao, StockItem, Modifier, StockCard(), StockFormDialog(), StocksTab()

## Knowledge Gaps
- **7 isolated node(s):** `HOME`, `DASHBOARD`, `ARCHIVES`, `STOCKS`, `TODAY` (+2 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 27 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Order` connect `Order` to `TestThemeScreen.kt`, `OrderViewModel`, `OrderRepository`, `ArchivesTab.kt`, `MainScreen`, `SeallDatabase`, `DashboardTab.kt`, `StockItem`, `CsvHelper`?**
  _High betweenness centrality (0.500) - this node is a cross-community bridge._
- **Why does `OrderViewModel` connect `OrderViewModel` to `TestThemeScreen.kt`, `Order`, `MainScreen`, `StockItem`?**
  _High betweenness centrality (0.187) - this node is a cross-community bridge._
- **Why does `StockItem` connect `StockItem` to `OrderViewModel`, `MainScreen`, `SeallDatabase`, `OrderRepository`?**
  _High betweenness centrality (0.151) - this node is a cross-community bridge._
- **What connects `HOME`, `DASHBOARD`, `ARCHIVES` to the rest of the system?**
  _7 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `OrderViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.13043478260869565 - nodes in this community are weakly interconnected._
- **Should `Order` be split into smaller, more focused modules?**
  _Cohesion score 0.14245014245014245 - nodes in this community are weakly interconnected._