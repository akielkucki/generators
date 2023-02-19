package com.gungenns.models;

public class GenLocation {
    private double x,y,z;
    private String world;
    private String GUID;
    private boolean dropped;

    public GenLocation(double x, double y, double z, String world, String GUID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.GUID = GUID;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }


    public void setDropped(boolean dropped) {
        this.dropped = dropped;
    }

    public boolean isDropped() {
        return dropped;
    }
}
