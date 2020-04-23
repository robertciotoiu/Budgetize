package com.example.robi.budgetize.backend.APIs.viewpagerforbankaccounts;

public class CardItem {

    private int mTextResource;
    private int mTitleResource;

    private String mText;
    private String mTitle;

    public CardItem(int title, int text) {
        mTitleResource = title;
        mTextResource = text;
    }

    public CardItem(String title, String text)
    {
        this.mTitle = title;
        this.mText = text;
    }

    public String getText() {
        return mText;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getTextResource() {
        return mTextResource;
    }

    public int getTitleResource() {
        return mTitleResource;
    }
}