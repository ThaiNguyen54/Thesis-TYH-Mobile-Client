package com.example.tryyourhair.Models;

import java.util.ArrayList;

public class HairstyleDataCallFromAPI {
    public boolean success;
    public String message;
    public ArrayList < data > Hairstyles;

    public void setSuccess(boolean success) {
        this.success = success;
    }
    public boolean getSuccess() {
        return success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setHairstyles(ArrayList<HairstyleDataCallFromAPI.data> hairstyles) {
        Hairstyles = hairstyles;
    }

    public ArrayList<HairstyleDataCallFromAPI.data> getHairstyles() {
        return Hairstyles;
    }

    public class data {
        public String Name;
        public String _id;
        public String Url;
        public String Des;
        public Integer Trending;

        public Integer getTrending() {return  Trending;}
        public void setTrending(Integer trending) {Trending = trending;}

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String get_id() {
            return _id;
        }

        public void setUrl(String url) {
            Url = url;
        }

        public String getUrl() {
            return Url;
        }

        public void setDes(String des) {
            Des = des;
        }

        public String getDes() {
            return Des;
        }
    }

}
