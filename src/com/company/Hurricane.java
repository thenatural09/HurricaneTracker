package com.company;

/**
 * Created by Troy on 10/4/16.
 */
public class Hurricane {
    int id;
    String name;
    String location;
    int category;
    String image;
    String author;

    public Hurricane(int id,String name, String location, int category, String image,String author) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.category = category;
        this.image = image;
        this.author = author;
    }
}
