package com.example.robi.budgetize.backend.APIs.viewpagerforbankaccounts;

public class CardItem {

    private long bankID;
    private int mTextResource;
    private int mTitleResource;

    private String mText;
    private String mTitle;

    private String mLinkStatus;

    public CardItem(int title, int text, String mLinkStatus) {
        mTitleResource = title;
        mTextResource = text;
        this.mLinkStatus = mLinkStatus;
    }

    public CardItem(String title, String text, String mLinkStatus, long bankID) {
        this.mTitle = title;
        this.mText = text;
        this.mLinkStatus = mLinkStatus;
        this.bankID = bankID;
    }

    public long getBankID() {
        return bankID;
    }

    public void setBankID(long bankID) {
        this.bankID = bankID;
    }

    public String getLinkStatus() {
        return mLinkStatus;
    }

    public void setLinkStatus(String mLinkStatus) {
        this.mLinkStatus = mLinkStatus;
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