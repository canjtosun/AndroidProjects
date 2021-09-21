package com.example.googleproject;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

public class Converters {

    @TypeConverter
    public String fromUserAddressToString(UserAddress userAddress){
        return new Gson().toJson(userAddress);
    }

    @TypeConverter
    public UserAddress fromStringToUserAddress(String address){
        return new Gson().fromJson(address, UserAddress.class);
    }
}
