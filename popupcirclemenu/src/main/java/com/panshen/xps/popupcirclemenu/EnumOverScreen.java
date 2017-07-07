package com.panshen.xps.popupcirclemenu;

/**
 * Created by XPS on 2017/6/18.
 */

public enum EnumOverScreen {
    LEFT("LEFT", 0), RIGHT("RIGHT", 0), TOP("TOP", 0);
    private String type;
    private int overScreenDistance;

    EnumOverScreen(String name, int index) {
        this.type = name;
        this.overScreenDistance = index;
    }

    public int getOverScreenDistance() {
        return overScreenDistance;
    }

    public void setOverScreenDistance(int overScreenDistance) {
        this.overScreenDistance = overScreenDistance;
    }
}
