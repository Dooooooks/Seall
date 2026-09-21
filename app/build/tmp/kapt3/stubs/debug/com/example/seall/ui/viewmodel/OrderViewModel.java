package com.example.seall.ui.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\r\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0013\u0018\u0000 A2\u00020\u0001:\u0001AB\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u001e\u0010.\u001a\u00020/2\u0006\u00100\u001a\u00020\u000f2\u0006\u00101\u001a\u00020\u00142\u0006\u00102\u001a\u00020\u000bJ\u0006\u00103\u001a\u00020/J\u000e\u00104\u001a\u00020/2\u0006\u00105\u001a\u00020\tJ\u0012\u00106\u001a\u00020/2\n\b\u0002\u00107\u001a\u0004\u0018\u00010\tJ\u000e\u00108\u001a\u00020/2\u0006\u00109\u001a\u00020\rJ\u000e\u0010:\u001a\u00020/2\u0006\u0010;\u001a\u00020\u000fJ\u0015\u0010<\u001a\u00020/2\b\u0010=\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\u0002\u0010>J\u001e\u0010?\u001a\u00020/2\u0006\u00100\u001a\u00020\u000f2\u0006\u00101\u001a\u00020\u00142\u0006\u00102\u001a\u00020\u000bJ\u000e\u0010@\u001a\u00020/2\u0006\u00105\u001a\u00020\tR\u0016\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0019\u0010\u0017\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0016R\u001d\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u001a0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0016R\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0016R\u001d\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u001a0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0016R\u0017\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0016R\u0017\u0010!\u001a\b\u0012\u0004\u0012\u00020\r0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010#\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0016R\u0019\u0010%\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u0016R\u0017\u0010\'\u001a\b\u0012\u0004\u0012\u00020(0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u0016R\u001d\u0010*\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u001a0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\u0016R\u0017\u0010,\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\u0016\u00a8\u0006B"}, d2 = {"Lcom/example/seall/ui/viewmodel/OrderViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "repository", "Lcom/example/seall/data/repository/OrderRepository;", "(Landroid/app/Application;Lcom/example/seall/data/repository/OrderRepository;)V", "_editingOrder", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/example/seall/data/model/Order;", "_isWizardOpen", "", "_paymentFilter", "Lcom/example/seall/ui/viewmodel/PaymentFilter;", "_searchQuery", "", "_selectedDateMillis", "", "combinedTotal", "Lkotlinx/coroutines/flow/StateFlow;", "", "getCombinedTotal", "()Lkotlinx/coroutines/flow/StateFlow;", "editingOrder", "getEditingOrder", "filteredOrders", "", "getFilteredOrders", "isWizardOpen", "orders", "getOrders", "paidTotal", "getPaidTotal", "paymentFilter", "getPaymentFilter", "searchQuery", "getSearchQuery", "selectedDateMillis", "getSelectedDateMillis", "unpaidCount", "", "getUnpaidCount", "unpaidOrders", "getUnpaidOrders", "unpaidTotal", "getUnpaidTotal", "addOrder", "", "customerName", "price", "isPaid", "closeWizard", "deleteOrder", "order", "openWizard", "orderToEdit", "setPaymentFilter", "filter", "setSearchQuery", "query", "setSelectedDateMillis", "dateMillis", "(Ljava/lang/Long;)V", "submitOrder", "togglePayment", "Companion", "app_debug"})
public final class OrderViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.seall.data.repository.OrderRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.seall.data.model.Order>> orders = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.seall.data.model.Order>> unpaidOrders = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Double> paidTotal = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Double> unpaidTotal = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Double> combinedTotal = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> unpaidCount = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _searchQuery = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> searchQuery = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.seall.ui.viewmodel.PaymentFilter> _paymentFilter = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.seall.ui.viewmodel.PaymentFilter> paymentFilter = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _selectedDateMillis = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Long> selectedDateMillis = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isWizardOpen = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isWizardOpen = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.seall.data.model.Order> _editingOrder = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.seall.data.model.Order> editingOrder = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.seall.data.model.Order>> filteredOrders = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.seall.ui.viewmodel.OrderViewModel.Companion Companion = null;
    
    public OrderViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application, @org.jetbrains.annotations.NotNull()
    com.example.seall.data.repository.OrderRepository repository) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.seall.data.model.Order>> getOrders() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.seall.data.model.Order>> getUnpaidOrders() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Double> getPaidTotal() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Double> getUnpaidTotal() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Double> getCombinedTotal() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> getUnpaidCount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getSearchQuery() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.seall.ui.viewmodel.PaymentFilter> getPaymentFilter() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getSelectedDateMillis() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isWizardOpen() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.seall.data.model.Order> getEditingOrder() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.example.seall.data.model.Order>> getFilteredOrders() {
        return null;
    }
    
    public final void openWizard(@org.jetbrains.annotations.Nullable()
    com.example.seall.data.model.Order orderToEdit) {
    }
    
    public final void closeWizard() {
    }
    
    public final void submitOrder(@org.jetbrains.annotations.NotNull()
    java.lang.String customerName, double price, boolean isPaid) {
    }
    
    public final void addOrder(@org.jetbrains.annotations.NotNull()
    java.lang.String customerName, double price, boolean isPaid) {
    }
    
    public final void togglePayment(@org.jetbrains.annotations.NotNull()
    com.example.seall.data.model.Order order) {
    }
    
    public final void deleteOrder(@org.jetbrains.annotations.NotNull()
    com.example.seall.data.model.Order order) {
    }
    
    public final void setSearchQuery(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
    
    public final void setPaymentFilter(@org.jetbrains.annotations.NotNull()
    com.example.seall.ui.viewmodel.PaymentFilter filter) {
    }
    
    public final void setSelectedDateMillis(@org.jetbrains.annotations.Nullable()
    java.lang.Long dateMillis) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/example/seall/ui/viewmodel/OrderViewModel$Companion;", "", "()V", "provideFactory", "Landroidx/lifecycle/ViewModelProvider$Factory;", "application", "Landroid/app/Application;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.lifecycle.ViewModelProvider.Factory provideFactory(@org.jetbrains.annotations.NotNull()
        android.app.Application application) {
            return null;
        }
    }
}