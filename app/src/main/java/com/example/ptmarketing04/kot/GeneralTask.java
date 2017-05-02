package com.example.ptmarketing04.kot;

/**
 * Created by ptmarketing04 on 02/05/2017.
 */

public class GeneralTask {
    private int id_task;
    private String title;
    private int tipe;
    private String start_date;
    private String end_date;
    private int finished;
    private int urgent;
    private int id_list;

    public GeneralTask() {}

    public GeneralTask(int id_task, String title, int tipe, String start_date, String end_date, int finished, int urgent, int id_list) {
        this.id_task = id_task;
        this.title = title;
        this.tipe = tipe;
        this.start_date = start_date;
        this.end_date = end_date;
        this.finished = finished;
        this.urgent = urgent;
        this.id_list = id_list;
    }

    public int getId_task() {
        return id_task;
    }

    public void setId_task(int id_task) {
        this.id_task = id_task;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTipe() {
        return tipe;
    }

    public void setTipe(int tipe) {
        this.tipe = tipe;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getUrgent() {
        return urgent;
    }

    public void setUrgent(int urgent) {
        this.urgent = urgent;
    }

    public int getId_list() {
        return id_list;
    }

    public void setId_list(int id_list) {
        this.id_list = id_list;
    }
}
