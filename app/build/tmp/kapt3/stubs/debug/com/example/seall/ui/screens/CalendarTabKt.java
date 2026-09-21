package com.example.seall.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u00b9\u0001\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\b2\u0006\u0010\t\u001a\u00020\n2\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\b2\b\u0010\f\u001a\u0004\u0018\u00010\r2\u0014\u0010\u000e\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\r\u0012\u0004\u0012\u00020\u00010\b2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\b2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\b2\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0002\u0010\u0012\u001a\u00020\u0013H\u0007\u00a2\u0006\u0002\u0010\u0014\u00a8\u0006\u0015"}, d2 = {"CalendarTab", "", "filteredOrders", "", "Lcom/example/seall/data/model/Order;", "searchQuery", "", "onSearchChange", "Lkotlin/Function1;", "paymentFilter", "Lcom/example/seall/ui/viewmodel/PaymentFilter;", "onFilterChange", "selectedDateMillis", "", "onDateSelected", "onTogglePaid", "onEditOrder", "onDeleteOrder", "modifier", "Landroidx/compose/ui/Modifier;", "(Ljava/util/List;Ljava/lang/String;Lkotlin/jvm/functions/Function1;Lcom/example/seall/ui/viewmodel/PaymentFilter;Lkotlin/jvm/functions/Function1;Ljava/lang/Long;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;)V", "app_debug"})
public final class CalendarTabKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void CalendarTab(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.seall.data.model.Order> filteredOrders, @org.jetbrains.annotations.NotNull()
    java.lang.String searchQuery, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSearchChange, @org.jetbrains.annotations.NotNull()
    com.example.seall.ui.viewmodel.PaymentFilter paymentFilter, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.example.seall.ui.viewmodel.PaymentFilter, kotlin.Unit> onFilterChange, @org.jetbrains.annotations.Nullable()
    java.lang.Long selectedDateMillis, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onDateSelected, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.example.seall.data.model.Order, kotlin.Unit> onTogglePaid, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.example.seall.data.model.Order, kotlin.Unit> onEditOrder, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.example.seall.data.model.Order, kotlin.Unit> onDeleteOrder, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}