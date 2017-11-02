package com.crl.zzh.customrefreshlayout.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by 周智慧 on 2017/10/30.
 */

@Entity(tableName = "users")
class User(@field:ColumnInfo(name = "first_name") var firstName: String?,
           @field:ColumnInfo(name = "last_name") var lastName: String?) {

    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}
