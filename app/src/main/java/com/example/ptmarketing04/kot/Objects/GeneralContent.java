package com.example.ptmarketing04.kot.Objects;

/**
 * Created by ptmarketing04 on 10/05/2017.
 */

public class GeneralContent {

    protected int id,task,tipe;
    protected String detail;

    public GeneralContent(){}

    public GeneralContent(int id, int task, int tipe, String detail) {
        this.id = id;
        this.task = task;
        this.tipe = tipe;
        this.detail = detail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public int getTipe() {
        return tipe;
    }

    public void setTipe(int tipe) {
        this.tipe = tipe;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
