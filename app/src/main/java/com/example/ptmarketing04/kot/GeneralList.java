package com.example.ptmarketing04.kot;

/**
 * Created by ptmarketing04 on 27/04/2017.
 */

public class GeneralList {
    protected int id,id_user;
    protected String title;

    public GeneralList(){}

    public GeneralList(int id, int id_user, String title) {
        this.id = id;
        this.id_user = id_user;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
