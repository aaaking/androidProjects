package com.crl.zzh.customrefreshlayout.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Created by 周智慧 on 2017/10/30.
 */

@Dao
interface UserDao {
    @get:Query("SELECT * FROM users")
    val all: Maybe<List<User>>

    @Query("SELECT * FROM users WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): Flowable<List<User>>

    @Query("SELECT * FROM users WHERE first_name LIKE :first AND " + "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Query("SELECT * FROM users where uid = :id")
    fun findById(id: Int): Maybe<User>


    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)

    @Update
    fun updateUsers(vararg users: User)
}