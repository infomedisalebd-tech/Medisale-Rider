package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DeliveryStatus
import com.example.data.model.HandoverLog
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.Rider
import com.example.data.model.Shop
import com.example.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        User::class,
        Rider::class,
        Shop::class,
        OrderEntity::class,
        OrderItemEntity::class,
        HandoverLog::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deliveryDao(): DeliveryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medidelivery_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.deliveryDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        ensureAdminExists(database.deliveryDao())
                    }
                }
            }

            private suspend fun ensureAdminExists(dao: DeliveryDao) {
                val admin = dao.getUserByEmail("info.medisalebd@gmail.com")
                if (admin == null) {
                    dao.insertUser(
                        User(
                            email = "info.medisalebd@gmail.com",
                            password = "Jubayer6",
                            name = "Jubayer (Admin)",
                            role = User.ROLE_ADMIN,
                            phone = "01700000000"
                        )
                    )
                }
            }

            private suspend fun populateInitialData(dao: DeliveryDao) {
                ensureAdminExists(dao)

                if (dao.getRiderCount() > 0) return

                val now = System.currentTimeMillis()
                val todayStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(now))

                // 1. Seed Riders (ডেলিভারি ম্যান) with Credentials
                val rider1Id = dao.insertRider(
                    Rider(
                        name = "Rahim Hossain",
                        phone = "01711234567",
                        vehicleType = "Motorcycle (ঢাকা-হ-১২৩৪)",
                        zone = "Dhanmondi / Mohammadpur",
                        loginEmail = "rahim@medisale.com",
                        loginPassword = "123"
                    )
                )
                dao.insertUser(
                    User(
                        email = "rahim@medisale.com",
                        password = "123",
                        name = "Rahim Hossain",
                        role = User.ROLE_RIDER,
                        riderId = rider1Id,
                        phone = "01711234567"
                    )
                )

                val rider2Id = dao.insertRider(
                    Rider(
                        name = "Karim Uddin",
                        phone = "01819876543",
                        vehicleType = "Motorcycle (ঢাকা-ল-৫৬৭৮)",
                        zone = "Mirpur / Pallabi",
                        loginEmail = "karim@medisale.com",
                        loginPassword = "123"
                    )
                )
                dao.insertUser(
                    User(
                        email = "karim@medisale.com",
                        password = "123",
                        name = "Karim Uddin",
                        role = User.ROLE_RIDER,
                        riderId = rider2Id,
                        phone = "01819876543"
                    )
                )

                val rider3Id = dao.insertRider(
                    Rider(
                        name = "Tanvir Ahmed",
                        phone = "01912348901",
                        vehicleType = "Bicycle / Eco Van",
                        zone = "Gulshan / Banani",
                        loginEmail = "tanvir@medisale.com",
                        loginPassword = "123"
                    )
                )
                dao.insertUser(
                    User(
                        email = "tanvir@medisale.com",
                        password = "123",
                        name = "Tanvir Ahmed",
                        role = User.ROLE_RIDER,
                        riderId = rider3Id,
                        phone = "01912348901"
                    )
                )

                val rider4Id = dao.insertRider(
                    Rider(
                        name = "Shakil Khan",
                        phone = "01615554433",
                        vehicleType = "Motorcycle (ঢাকা-হ-৯৯৮৮)",
                        zone = "Uttara / Airport",
                        loginEmail = "shakil@medisale.com",
                        loginPassword = "123"
                    )
                )
                dao.insertUser(
                    User(
                        email = "shakil@medisale.com",
                        password = "123",
                        name = "Shakil Khan",
                        role = User.ROLE_RIDER,
                        riderId = rider4Id,
                        phone = "01615554433"
                    )
                )

                // 2. Seed Shops (দোকান / ফার্মেসি)
                val shop1Id = dao.insertShop(
                    Shop(name = "Lazz Pharma (Dhanmondi Branch)", ownerOrManager = "Mr. Harun", phone = "01712001122", address = "House 24, Road 27, Dhanmondi", area = "Dhanmondi")
                )
                val shop2Id = dao.insertShop(
                    Shop(name = "Tamanna Pharmacy (Mirpur 10)", ownerOrManager = "Dr. Asif", phone = "01813998877", address = "Plot 12, Block D, Mirpur 10", area = "Mirpur")
                )
                val shop3Id = dao.insertShop(
                    Shop(name = "Popular Medicine Corner", ownerOrManager = "Babul Mia", phone = "01914776655", address = "Shantinagar Plaza, Dhaka", area = "Shantinagar")
                )
                val shop4Id = dao.insertShop(
                    Shop(name = "Gulshan Model Pharmacy", ownerOrManager = "Mr. Kamal", phone = "01618882211", address = "Circle 2, Gulshan Avenue", area = "Gulshan")
                )
                val shop5Id = dao.insertShop(
                    Shop(name = "Al-Madina Drug House", ownerOrManager = "Haji Yunus", phone = "01719223344", address = "Sector 7, Main Road, Uttara", area = "Uttara")
                )
            }
        }
    }
}
