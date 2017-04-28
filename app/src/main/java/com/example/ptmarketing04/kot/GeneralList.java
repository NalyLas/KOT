package com.example.ptmarketing04.kot;

/**
 * Created by ptmarketing04 on 27/04/2017.
 */

public class GeneralList {
    protected int id,id_user;
    protected String title, date;

    public GeneralList(){}

    public GeneralList(int id, int id_user, String title, String date) {
        this.id = id;
        this.id_user = id_user;
        this.title = title;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
