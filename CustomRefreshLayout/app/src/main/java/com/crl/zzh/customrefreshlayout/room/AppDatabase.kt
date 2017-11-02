package com.crl.zzh.customrefreshlayout.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Created by 周智慧 on 2017/10/30.
 */

@Database(entities = arrayOf(User::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
