package com.example.httprestapiretrievingdata;

public class User {

    public int id;
    public String name,userName,email,phone,website;
    public Address address;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }

    public Address getAddress() {
        return address;
    }

    public class Address{
        String street;
        String city;

        public String getStreet() {
            return street;
        }

        public String getCity() {
            return city;
        }
    }



}
