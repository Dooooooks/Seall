# Graph Report - Seall  (2026-09-21)

## Corpus Check
- 22 files · ~8,990 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 29 file(s) not represented in the graph (top: .bin 10, .lock 7, .properties 7)

## Summary
- 111 nodes · 215 edges · 15 communities (9 shown, 2 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 3 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- TestThemeScreen.kt
- OrderViewModel
- AGENTS.md
- MainActivity.kt
- OrderRepository
- gradlew
- Order
- PaymentFilter
- MainScreen
- SeallDatabase
- DashboardTab.kt

## God Nodes (most connected - your core abstractions)
1. `Order` - 39 edges
2. `OrderViewModel` - 22 edges
3. `OrderDao` - 17 edges
4. `OrderRepository` - 13 edges
5. `MainScreen()` - 9 edges
6. `PaymentFilter` - 8 edges
7. `OrderCard()` - 7 edges
8. `DashboardTab()` - 7 edges
9. `AppTab` - 6 edges
10. `CalendarTab()` - 6 edges

## Surprising Connections (you probably didn't know these)
- `MainScreen()` --calls--> `CalendarTab()`  [INFERRED]
  app/src/main/java/com/example/seall/ui/screens/MainScreen.kt → app/src/main/java/com/example/seall/ui/screens/CalendarTab.kt
- `MainScreen()` --calls--> `DashboardTab()`  [INFERRED]
  app/src/main/java/com/example/seall/ui/screens/MainScreen.kt → app/src/main/java/com/example/seall/ui/screens/DashboardTab.kt
- `MainScreen()` --calls--> `HomeTab()`  [INFERRED]
  app/src/main/java/com/example/seall/ui/screens/MainScreen.kt → app/src/main/java/com/example/seall/ui/screens/HomeTab.kt
- `MainActivity` --references--> `OrderViewModel`  [EXTRACTED]
  app/src/main/java/com/example/seall/MainActivity.kt → app/src/main/java/com/example/seall/ui/viewmodel/OrderViewModel.kt
- `OrderRepository` --references--> `Order`  [EXTRACTED]
  app/src/main/java/com/example/seall/data/repository/OrderRepository.kt → app/src/main/java/com/example/seall/data/model/Order.kt

## Import Cycles
- None detected.

## Communities (15 total, 2 thin omitted)

### Community 0 - "TestThemeScreen.kt"
Cohesion: 0.60
Nodes (5): Color, Modifier, MetricCard(), OrderRowCard(), TestThemeScreen()

### Community 1 - "OrderViewModel"
Cohesion: 0.17
Nodes (7): AndroidViewModel, OrderViewModel, Factory, Application, StateFlow, T, ViewModelProvider

### Community 3 - "MainActivity.kt"
Cohesion: 0.48
Nodes (4): MainActivity, SeallTheme(), Bundle, ComponentActivity

### Community 6 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 9 - "Order"
Cohesion: 0.20
Nodes (3): Flow, OrderDao, Order

### Community 10 - "PaymentFilter"
Cohesion: 0.17
Nodes (10): Modifier, OrderCard(), CalendarTab(), Modifier, HomeTab(), Modifier, PaymentFilter, ALL (+2 more)

### Community 11 - "MainScreen"
Cohesion: 0.31
Nodes (8): RapidEntryWizardModal(), AppTab, CALENDAR, DASHBOARD, HOME, Modifier, SeallBottomBar(), MainScreen()

### Community 12 - "SeallDatabase"
Cohesion: 0.47
Nodes (3): SeallDatabase, Context, RoomDatabase

### Community 13 - "DashboardTab.kt"
Cohesion: 0.57
Nodes (7): AnalyticsHeroCard(), AnalyticsMiniCard(), DashboardTab(), DebtorRowCard(), Color, Modifier, ImageVector

## Knowledge Gaps
- **7 isolated node(s):** `HOME`, `DASHBOARD`, `CALENDAR`, `ALL`, `PAID` (+2 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 20 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **2 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Order` connect `Order` to `TestThemeScreen.kt`, `OrderViewModel`, `OrderRepository`, `PaymentFilter`, `MainScreen`, `SeallDatabase`, `DashboardTab.kt`?**
  _High betweenness centrality (0.518) - this node is a cross-community bridge._
- **Why does `OrderViewModel` connect `OrderViewModel` to `TestThemeScreen.kt`, `MainActivity.kt`, `Order`, `PaymentFilter`, `MainScreen`?**
  _High betweenness centrality (0.240) - this node is a cross-community bridge._
- **Why does `OrderDao` connect `Order` to `SeallDatabase`, `OrderRepository`?**
  _High betweenness centrality (0.089) - this node is a cross-community bridge._
- **What connects `HOME`, `DASHBOARD`, `CALENDAR` to the rest of the system?**
  _7 weakly-connected nodes found - possible documentation gaps or missing edges._