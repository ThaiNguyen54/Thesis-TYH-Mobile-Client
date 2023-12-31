package com.example.tryyourhair.Models;

import java.util.Locale;

public class HairStyle {
    private String Name;
    private String _id;
    private String Url;
    private String Des;
    private Integer Trending;
    private String Celeb;
    private String Category;

    public HairStyle(String Name, String _id, String url, String des, Integer trending, String Celebrity, String Category) {
        this._id = _id;
        this.Name = Name;
        this.Url = url;
        this.Des = des;
        this.Trending = trending;
        this.Category = Category;
        this.Celeb = Celebrity;
    }

    public String getCelebrity() {
        return Celeb;
    }

    public void setCelebrity(String celebrity) {
        Celeb = celebrity;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        this.Url = url;
    }

    public String getDes() {
        return Des;
    }

    public void setDes(String description) {
        this.Des = description;
    }

    public Integer getTrending() {return Trending;}
    public void setTrending(Integer trending) {this.Trending = trending;}
}
