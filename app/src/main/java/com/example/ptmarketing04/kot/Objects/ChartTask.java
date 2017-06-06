package com.example.ptmarketing04.kot.Objects;

/**
 * Created by Natalia on 06/06/2017.
 */

public class ChartTask {
    private int number;
    private String endDate;

    public ChartTask() {}

    public ChartTask(int number, String fecha) {
        this.number = number;
        this.endDate = fecha;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String fecha) {
        this.endDate = fecha;
    }
}
