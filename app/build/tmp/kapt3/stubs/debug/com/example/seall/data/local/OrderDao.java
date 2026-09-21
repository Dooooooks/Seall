package com.example.seall.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\fH\'J\u000e\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\fH\'J\u0018\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\f2\u0006\u0010\b\u001a\u00020\tH\'J$\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\f2\u0006\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0013\u001a\u00020\tH\'J\u000e\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u000f0\fH\'J\u000e\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\fH\'J\u0014\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\fH\'J\u000e\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u000f0\fH\'J\u0016\u0010\u0019\u001a\u00020\t2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\f2\u0006\u0010\u001b\u001a\u00020\u001cH\'J\u0016\u0010\u001d\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001e\u0010\u001e\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u001f\u001a\u00020 H\u00a7@\u00a2\u0006\u0002\u0010!\u00a8\u0006\""}, d2 = {"Lcom/example/seall/data/local/OrderDao;", "", "deleteOrder", "", "order", "Lcom/example/seall/data/model/Order;", "(Lcom/example/seall/data/model/Order;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteOrderById", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllOrders", "Lkotlinx/coroutines/flow/Flow;", "", "getCombinedTotal", "", "getOrderById", "getOrdersByDateRange", "startTime", "endTime", "getPaidTotal", "getUnpaidCount", "", "getUnpaidOrders", "getUnpaidTotal", "insertOrder", "searchOrders", "query", "", "updateOrder", "updatePaymentStatus", "isPaid", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface OrderDao {
    
    @androidx.room.Query(value = "SELECT * FROM orders ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.seall.data.model.Order>> getAllOrders();
    
    @androidx.room.Query(value = "SELECT * FROM orders WHERE id = :id LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.example.seall.data.model.Order> getOrderById(long id);
    
    @androidx.room.Query(value = "SELECT * FROM orders WHERE isPaid = 0 ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.seall.data.model.Order>> getUnpaidOrders();
    
    @androidx.room.Query(value = "SELECT * FROM orders WHERE createdAt >= :startTime AND createdAt <= :endTime ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.seall.data.model.Order>> getOrdersByDateRange(long startTime, long endTime);
    
    @androidx.room.Query(value = "SELECT * FROM orders WHERE customerName LIKE \'%\' || :query || \'%\' ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.seall.data.model.Order>> searchOrders(@org.jetbrains.annotations.NotNull()
    java.lang.String query);
    
    @androidx.room.Query(value = "SELECT COALESCE(SUM(price), 0.0) FROM orders WHERE isPaid = 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Double> getPaidTotal();
    
    @androidx.room.Query(value = "SELECT COALESCE(SUM(price), 0.0) FROM orders WHERE isPaid = 0")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Double> getUnpaidTotal();
    
    @androidx.room.Query(value = "SELECT COALESCE(SUM(price), 0.0) FROM orders")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Double> getCombinedTotal();
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM orders WHERE isPaid = 0")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Integer> getUnpaidCount();
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertOrder(@org.jetbrains.annotations.NotNull()
    com.example.seall.data.model.Order order, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateOrder(@org.jetbrains.annotations.NotNull()
    com.example.seall.data.model.Order order, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOrder(@org.jetbrains.annotations.NotNull()
    com.example.seall.data.model.Order order, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM orders WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOrderById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE orders SET isPaid = :isPaid WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updatePaymentStatus(long id, boolean isPaid, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}