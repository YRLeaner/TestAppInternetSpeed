package com.example.tyr.showyourspeed.list;

/**
 * Created by tyr on 2017/2/11.
 */
public class ShowMessage {
    private String title;
    private long speed;

    public ShowMessage() {
    }

    public ShowMessage(String title, long speed) {
        this.speed = speed;
        this.title = title;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
