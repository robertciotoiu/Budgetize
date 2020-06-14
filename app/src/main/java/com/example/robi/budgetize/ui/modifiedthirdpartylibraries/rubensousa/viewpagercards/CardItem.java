package com.example.robi.budgetize.ui.modifiedthirdpartylibraries.rubensousa.viewpagercards;

public class CardItem {

    private long bankID;
    private int mTextResource;
    private int mTitleResource;
    private String mText;
    private String mTitle;
    private String mLinkStatus;
    private String mBankLogo;

    public CardItem(int title, int text, String mLinkStatus) {
        mTitleResource = title;
        mTextResource = text;
        this.mLinkStatus = mLinkStatus;
    }

    public CardItem(String title, String text, String mLinkStatus, String mBankLogo, long bankID) {
        this.mTitle = title;
        this.mText = text;
        this.mLinkStatus = mLinkStatus;
        this.bankID = bankID;
        this.mBankLogo = mBankLogo;
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

    public String getmBankLogo() {
        return mBankLogo;
    }

    public void setmBankLogo(String mBankLogo) {
        this.mBankLogo = mBankLogo;
    }
}