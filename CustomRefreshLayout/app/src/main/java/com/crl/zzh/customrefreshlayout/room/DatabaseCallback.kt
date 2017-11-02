package com.crl.zzh.customrefreshlayout.room

/**
 * Created by 周智慧 on 2017/10/30.
 */

interface DatabaseCallback {

    fun onUsersLoaded(users: List<User>)

    fun onUserDeleted()

    fun onUserAdded()

    fun onDataNotAvailable()

    fun onUserUpdated()
}
