package com.kkhill.drivers.demolight2.lib;

public class Client {

    private String ip;
    private String port;
    private boolean state;
    private int brightness;
    private int temperature;

    public Client(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

    public boolean open() {
        state = true;
        return true;
    }

    public boolean close() {
        state = false;
        return true;
    }

    public boolean state() {
        return state;
    }

    public int getBrightness() {
        return brightness;
    }

    public boolean setBrightness(int brightness) {
        System.out.println("wow! brightness had been changed");
        this.brightness = brightness;
        return true;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getTemperature() {
        return temperature;
    }

    public boolean setTemperature(int temperature) {
        System.out.println("wow! temperature had been changed");
        this.temperature = temperature;
        return true;
    }
}
