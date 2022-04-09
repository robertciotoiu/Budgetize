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
     long addLinkedBank(LinkedBank linkedBank);

    @Query("SELECT * FROM linked_banks")
     LiveData<List<LinkedBank>> getAllLinkedBanks();

    @Query("SELECT COUNT(*) FROM linked_banks where link_status='LINKED'")
     long noLinkedBanks();

    @Query("SELECT * FROM linked_banks where id=:id")
     LiveData<LinkedBank> getLinkedBank(long id);

    @Query("SELECT link_status FROM linked_banks where id=:id")
     String getLinkedBankStatus(long id);

    @Query("SELECT obp_bank_id FROM linked_banks where id=:id")
     String getLinkedBankOBPID(long id);

    @Query("UPDATE linked_banks SET link_status=:link_status WHERE id=:id")
     long updateLinkStatus(long id, String link_status);

    @Query("DELETE FROM linked_banks where id=:id")
     int deleteLinkedBank(long id);
}
