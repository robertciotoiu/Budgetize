package com.example.robi.budgetize.ui.viewpager;


import androidx.cardview.widget.CardView;

public interface LinkedBanksCardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}