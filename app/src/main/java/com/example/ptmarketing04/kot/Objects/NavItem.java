package com.example.ptmarketing04.kot.Objects;

import android.graphics.drawable.Drawable;

/**
 * Created by ptmarketing04 on 11/05/2017.
 */

public class NavItem {

    private int id;
    private String item;
    private Drawable pic;

    //constructor
    public NavItem(int id, Drawable pic, String item)
    {
        this.id 	=id;
        this.item	=item;
        this.pic	=pic;
    }

    //constructor
    public NavItem(Drawable pic, String item)
    {
        this.item=item;
        this.pic=pic;
    }

    //id
    public int getId()
    {
        return this.id;
    }

    //item
    public String getItem()
    {
        return item;
    }

    public void setItem(String item)
    {
        this.item=item;
    }

    //img
    public Drawable getPic() {
        return pic;
    }

    public void setPic(Drawable pic) {
        this.pic=pic;
    }
}
