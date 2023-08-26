package com.example.tryyourhair.Models;

public class HairStyle {
    private String Name;
    private String _id;
    private String Url;
    private String Des;

    public HairStyle(String Name, String _id, String url, String des) {
        this._id = _id;
        this.Name = Name;
        this.Url = url;
        this.Des = des;
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
}
