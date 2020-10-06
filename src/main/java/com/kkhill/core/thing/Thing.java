package com.kkhill.core.thing;

import java.util.HashMap;
import java.util.Map;

public abstract class Thing {

    private String id;
    protected String friendlyName;
    protected boolean available;
    protected State state;
    protected Map<String, Property> properties;
    protected Map<String, Service> services;

    public Thing(String friendlyName, boolean available) {

        this.friendlyName = friendlyName;
        this.available = available;
        this.properties = new HashMap<>();
        this.services = new HashMap<>();
    }

    public String getID() {
        return this.id;
    }

    void setID(String id) {
        this.id = id;
    }


    public String getFriendlyName() {
        return this.friendlyName;
    }

    public State getState() {
        return this.state;
    }

    void setState(State state) {
        this.state = state;
    }

    public boolean isAvailable() {
        return this.available;
    }
    void disable() {
        this.available = false;
    }

    void enable() {
        this.available = true;
    }

    public Map<String, Property> getProperties() {
        return this.properties;
    }

    void setProperty(Property property) {
        this.properties.put(property.getName(), property);
    }

    public Property getProperty(String name) {
        return this.properties.get(name);
    }

    public Map<String, Service> getServices() {
        return this.services;
    }

    public void setService(Service service) {
        this.services.put(service.getName(), service);
    }

    public Service getService(String name) {
        return this.services.get(name);
    }

//    public abstract void defineProperties();
//
//    public abstract void defineServices();
}
