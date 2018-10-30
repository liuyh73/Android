package com.example.liuyh73.healthyfoods;

import java.io.Serializable;

public class Collection implements Serializable {
    private String Name;
    private String Abbr;
    private String Category;
    private String Nutritive;
    private String BgColor;
    private boolean isCollected;
    public Collection(String Name, String Abbr, String Category, String Nutritive, String BgColor) {
        this.Name = Name;
        this.Abbr = Abbr;
        this.Category = Category;
        this.Nutritive = Nutritive;
        this.BgColor = BgColor;
        this.isCollected = false;
    }

    public String getName() {
        return this.Name;
    }

    public String getAbbr() {
        return this.Abbr;
    }

    public String getCategory() {
        return this.Category;
    }

    public String getNutritive() {
        return this.Nutritive;
    }

    public String getBgColor() {
        return this.BgColor;
    }

    public boolean getIsCollected() {return this.isCollected;}

    public void setIsCollected(boolean isCollected){
        this.isCollected = isCollected;
    }
}
