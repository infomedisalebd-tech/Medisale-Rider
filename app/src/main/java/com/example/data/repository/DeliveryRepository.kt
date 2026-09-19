package com.example.data.repository

import com.example.data.local.DeliveryDao
import com.example.data.model.HandoverLog
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderWithItems
import com.example.data.model.Rider
import com.example.data.model.Shop
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

class DeliveryRepository(private val dao: DeliveryDao) {

    val allUsers: Flow<List<User>> = dao.getAllUsers()
    val allOrders: Flow<List<OrderWithItems>> = dao.getAllOrdersWithItems()
    val allRiders: Flow<List<Rider>> = dao.getAllRiders()
    val allShops: Flow<List<Shop>> = dao.getAllShops()
    val allHandoverLogs: Flow<List<HandoverLog>> = dao.getAllHandoverLogs()

    suspend fun getAllRidersList(): List<Rider> {
        return dao.getAllRidersList()
    }

    // --- Authentication ---
    suspend fun authenticate(emailOrPhone: String, password: String): User? {
        return dao.authenticate(emailOrPhone.trim(), password.trim())
    }

    suspend fun getUserByEmail(email: String): User? {
        return dao.getUserByEmail(email.trim())
    }

    suspend fun getUserByRiderId(riderId: Long): User? {
        return dao.getUserByRiderId(riderId)
    }

    suspend fun addUser(user: User): Long {
        return dao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        dao.updateUser(user)
    }

    suspend fun deleteUser(userId: Long) {
        dao.deleteUserById(userId)
    }

    // --- Orders ---

    suspend fun getOrderById(orderId: Long): OrderWithItems? {
        return dao.getOrderWithItemsById(orderId)
    }

    suspend fun createOrder(order: OrderEntity, items: List<OrderItemEntity>): Long {
        return dao.insertFullOrder(order, items)
    }

    suspend fun updateOrder(order: OrderEntity, items: List<OrderItemEntity>) {
        dao.updateFullOrder(order, items)
    }

    suspend fun updateOrderSingle(order: OrderEntity) {
        dao.updateOrder(order)
    }

    suspend fun updateOrders(orders: List<OrderEntity>) {
        dao.updateOrders(orders)
    }

    suspend fun addHandoverLog(log: HandoverLog): Long {
        return dao.insertHandoverLog(log)
    }

    suspend fun deleteOrder(orderId: Long) {
        dao.deleteOrderById(orderId)
    }

    suspend fun deleteDemoOrders() {
        dao.deleteDemoOrders()
        dao.deleteOrphanOrderItems()
    }

    suspend fun deleteAllOrders() {
        dao.deleteAllOrders()
        dao.deleteAllOrderItems()
    }

    suspend fun addRider(rider: Rider): Long {
        return dao.insertRider(rider)
    }

    suspend fun updateRider(rider: Rider) {
        dao.updateRider(rider)
    }

    suspend fun deleteRider(riderId: Long) {
        dao.deleteRiderById(riderId)
    }

    suspend fun addShop(shop: Shop): Long {
        return dao.insertShop(shop)
    }

    suspend fun updateShop(shop: Shop) {
        dao.updateShop(shop)
    }

    suspend fun deleteShop(shopId: Long) {
        dao.deleteShopById(shopId)
    }
}
