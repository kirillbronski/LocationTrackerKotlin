package com.foxminded.android.trackerapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AccountDao {

    @Query("SELECT * FROM account")
    fun getAll(): MutableList<AccountEntity>

    @Query("SELECT * FROM account WHERE accountInfo = :login")
    fun getByLogin(login: String): AccountEntity

    @Query("DELETE FROM account")
    fun deleteAll(): Int?

    @Insert
    fun insert(accountEntity: AccountEntity)

}