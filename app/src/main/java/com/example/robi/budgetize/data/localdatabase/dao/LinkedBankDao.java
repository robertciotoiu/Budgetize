package com.example.robi.budgetize.data.localdatabase.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.robi.budgetize.data.localdatabase.entities.LinkedBank;

import java.util.List;

@Dao
public interface LinkedBankDao {
    @Insert
    public long addLinkedBank(LinkedBank linkedBank);

    @Query("SELECT * FROM linked_banks")
    public LiveData<List<LinkedBank>> getAllLinkedBanks();

    @Query("SELECT * FROM linked_banks where id=:id")
    public LiveData<LinkedBank> getLinkedBank(long id);

    @Query("DELETE FROM linked_banks where id=:id")
    public int deleteLinkedBank(long id);
}
