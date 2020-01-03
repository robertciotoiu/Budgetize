package com.example.robi.investorsapp.database.wallet;

import android.arch.persistence.room.Insert;

public interface WalletDao {

    @Insert
    public void addWallet(Wallet wallet);
}
