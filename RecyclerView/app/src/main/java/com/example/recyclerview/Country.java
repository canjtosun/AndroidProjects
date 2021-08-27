package com.example.recyclerview;

public class Country {

    private String countryName, capital, website;
    private int phoneCode, image, moreInfo;

    public Country(String countryName, String capital, int phoneCode, int image, String website, int moreInfo) {
        this.countryName = countryName;
        this.capital = capital;
        this.phoneCode = phoneCode;
        this.image = image;
        this.website = website;
        this.moreInfo = moreInfo;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCapital() {
        return capital;
    }

    public int getPhoneCode() {
        return phoneCode;
    }

    public int getImage() {
        return image;
    }

    public String getWebsite() {
        return website;
    }

    public int getMoreInfo() {
        return moreInfo;
    }


}
