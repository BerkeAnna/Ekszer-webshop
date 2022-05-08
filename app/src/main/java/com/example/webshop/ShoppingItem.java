package com.example.webshop;

public class ShoppingItem {
    private String ID;
    private String name;
    private String info;
    private String price;
    private float ratedInfo;
    private int imageResource;
    private int count;

    public ShoppingItem() {
    }

    public ShoppingItem(String name, String info, String price, float ratedInfo, int imageResource, int count) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.imageResource = imageResource;
        this.count=count;
    }

    public String getName() { return name; }
    public String getInfo() { return info; }
    public String getPrice() { return price; }
    public float getRatedInfo() { return ratedInfo; }
    public int getImageResource() { return imageResource; }

    public int getCount() {
        return count;
    }

    public String _getID() { return ID; }

    public void setID(String ID) {
        this.ID = ID;
    }


    public void setCount(int count) {
        this.count = count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setRatedInfo(float ratedInfo) {
        this.ratedInfo = ratedInfo;
    }
}
