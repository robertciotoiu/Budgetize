package com.example.robi.budgetize.ui.adapters.viewpager;



import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.robi.budgetize.ui.fragments.LinkBanksCardFragment;

import java.util.ArrayList;
import java.util.List;

public class LinkBanksCardFragmentPagerAdapter extends FragmentStatePagerAdapter implements LinkedBanksCardAdapter {

    private List<LinkBanksCardFragment> mFragments;
    private float mBaseElevation;

    public LinkBanksCardFragmentPagerAdapter(FragmentManager fm, float baseElevation) {
        super(fm);
        mFragments = new ArrayList<>();
        mBaseElevation = baseElevation;

        for(int i = 0; i< 5; i++){
            addCardFragment(new LinkBanksCardFragment());
        }
    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mFragments.get(position).getCardView();
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        mFragments.set(position, (LinkBanksCardFragment) fragment);
        return fragment;
    }

    public void addCardFragment(LinkBanksCardFragment fragment) {
        mFragments.add(fragment);
    }

}