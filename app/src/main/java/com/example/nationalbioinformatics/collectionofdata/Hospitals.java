package com.example.nationalbioinformatics.collectionofdata;

public class Hospitals {
    String state;
    String name;
    String city;
    String ownership;

    public String getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getOwnership() {
        return ownership;
    }

    public Hospitals(String state, String name, String city, String ownership) {
        this.state = state;
        this.name = name;
        this.city = city;
        this.ownership = ownership;
    }
}
