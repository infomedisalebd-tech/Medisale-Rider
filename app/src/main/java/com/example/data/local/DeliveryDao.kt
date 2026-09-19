package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderWithItems
import com.example.data.model.Rider
import com.example.data.model.Shop
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryDao {

    // --- Users & Authentication ---
    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE (LOWER(email) = LOWER(:emailOrPhone) OR phone = :emailOrPhone) AND password = :password LIMIT 1")
    suspend fun authenticate(emailOrPhone: String, password: String): User?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): User?

    @Query("SELECT * FROM users WHERE riderId = :riderId LIMIT 1")
    suspend fun getUserByRiderId(riderId: Long): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: Long)

    @Query("DELETE FROM users WHERE riderId = :riderId")
    suspend fun deleteUserByRiderId(riderId: Long)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    // --- Orders ---
    @Transaction
    @Query("SELECT * FROM orders ORDER BY orderDate DESC, id DESC")
    fun getAllOrdersWithItems(): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderWithItemsById(orderId: Long): OrderWithItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Update
    suspend fun updateOrderItems(items: List<OrderItemEntity>)

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteItemsByOrderId(orderId: Long)

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrderById(orderId: Long)

    @Query("DELETE FROM orders WHERE invoiceNumber LIKE 'SL-51%' OR invoiceNumber LIKE 'INV-2026%' OR notes LIKE '%demo%'")
    suspend fun deleteDemoOrders()

    @Query("DELETE FROM order_items WHERE orderId NOT IN (SELECT id FROM orders)")
    suspend fun deleteOrphanOrderItems()

    @Query("DELETE FROM orders")
    suspend fun deleteAllOrders()

    @Query("DELETE FROM order_items")
    suspend fun deleteAllOrderItems()

    @Transaction
    suspend fun insertFullOrder(order: OrderEntity, items: List<OrderItemEntity>): Long {
        val orderId = insertOrder(order)
        val itemsWithId = items.map { it.copy(orderId = orderId) }
        insertOrderItems(itemsWithId)
        return orderId
    }

    @Transaction
    suspend fun updateFullOrder(order: OrderEntity, items: List<OrderItemEntity>) {
        updateOrder(order)
        deleteItemsByOrderId(order.id)
        val itemsWithId = items.map { it.copy(orderId = order.id) }
        insertOrderItems(itemsWithId)
    }

    // --- Riders ---
    @Query("SELECT * FROM riders ORDER BY name ASC")
    fun getAllRiders(): Flow<List<Rider>>

    @Query("SELECT * FROM riders")
    suspend fun getAllRidersList(): List<Rider>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRider(rider: Rider): Long

    @Update
    suspend fun updateRider(rider: Rider)

    @Query("DELETE FROM riders WHERE id = :riderId")
    suspend fun deleteRiderById(riderId: Long)

    @Query("SELECT COUNT(*) FROM riders")
    suspend fun getRiderCount(): Int

    // --- Shops ---
    @Query("SELECT * FROM shops ORDER BY name ASC")
    fun getAllShops(): Flow<List<Shop>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: Shop): Long

    @Update
    suspend fun updateShop(shop: Shop)

    @Query("DELETE FROM shops WHERE id = :shopId")
    suspend fun deleteShopById(shopId: Long)

    @Query("SELECT COUNT(*) FROM shops")
    suspend fun getShopCount(): Int

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getOrderCount(): Int

    @Update
    suspend fun updateOrders(orders: List<OrderEntity>)

    // --- Handover Logs ---
    @Query("SELECT * FROM handover_logs ORDER BY timestamp DESC")
    fun getAllHandoverLogs(): Flow<List<com.example.data.model.HandoverLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHandoverLog(log: com.example.data.model.HandoverLog): Long
}
