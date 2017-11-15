package com.crl.zzh.customrefreshlayout.room

import android.arch.persistence.room.Room
import android.content.Context

import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * Created by 周智慧 on 2017/10/30.
 */

class LocalCacheManager(private val context: Context) {
    private val db: AppDatabase

    init {
        db = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()
    }

    fun getUsers(databaseCallback: DatabaseCallback) {
        db.userDao().all.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { users -> databaseCallback.onUsersLoaded(users) }
    }

    fun loadByIds(userIds: IntArray) {
        db.userDao().loadAllByIds(userIds).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { println("loadByIds") }
    }


    fun addUser(databaseCallback: DatabaseCallback, firstName: String, lastName: String) {
        Completable.fromAction {
            val user = User(firstName, lastName)
            db.userDao().insertAll(user)
        }.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {}

            override fun onComplete() {
                databaseCallback.onUserAdded()
            }

            override fun onError(e: Throwable) {
                databaseCallback.onDataNotAvailable()
            }
        })
    }

    fun deleteUser(databaseCallback: DatabaseCallback, user: User) {
        Completable.fromAction { db.userDao().delete(user) }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onComplete() {
                databaseCallback.onUserDeleted()
            }

            override fun onError(e: Throwable) {
                databaseCallback.onDataNotAvailable()
            }
        })
    }


    fun updateUser(databaseCallback: DatabaseCallback, user: User) {
        user.firstName = "first name first name"
        user.lastName = "last name last name"
        Completable.fromAction { db.userDao().updateUsers(user) }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onComplete() {
                databaseCallback.onUserUpdated()
            }

            override fun onError(e: Throwable) {
                databaseCallback.onDataNotAvailable()
            }
        })
    }

    companion object {
        private val DB_NAME = "database-name"
        private var _instance: LocalCacheManager? = null

        fun getInstance(context: Context): LocalCacheManager {
            if (_instance == null) {
                _instance = LocalCacheManager(context)
            }
            return _instance!!
        }
    }
}
