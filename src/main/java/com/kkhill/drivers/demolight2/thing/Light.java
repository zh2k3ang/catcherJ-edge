package com.kkhill.drivers.demolight2.thing;

import com.kkhill.core.Catcher;
import com.kkhill.core.annotation.Property;
import com.kkhill.core.annotation.Service;
import com.kkhill.core.annotation.ServiceParam;
import com.kkhill.core.annotation.State;
import com.kkhill.core.exception.IllegalThingException;
import com.kkhill.core.exception.NotFoundException;
import com.kkhill.core.thing.Thing;
import com.kkhill.drivers.demolight2.lib.Client;
import com.kkhill.common.thing.CProperty;
import com.kkhill.common.thing.CService;
import com.kkhill.common.thing.CState;

public class Light extends Thing {

    private Client client;

    @State(description = "state")
    public String state = CState.OFF;

    @Property(name="vendor", description = "vendor name")
    public String vendor = "otcaix";

    @Property(name= CProperty.BRIGHTNESS, description = "brightness")
    public int brightness;

    @Property(name= CProperty.TEMPERATURE, description = "temperature")
    public int temperature;

    @Service(name="open", description = "open the light")
    public void open() {
        if(this.client.open()) {
            this.state = "on";
            try {
                Catcher.getThingMonitor().updateStateAndNotify(this.getId());
            } catch (NotFoundException | IllegalThingException e) {
                e.printStackTrace();
            }
        }
    }

    @Service(name= CService.CLOSE, description = "close the light")
    public void close() {
        if(this.client.close()) {
            this.state = "off";
            try {
                Catcher.getThingMonitor().updateStateAndNotify(this.getId());
            } catch (NotFoundException | IllegalThingException e) {
                e.printStackTrace();
            }
        }
    }

    @Service(name= CService.TOGGLE, description = "toggle the light")
    public void toggle() {
        if(this.client.state()) this.close();
        else this.open();
    }

    @Service(name="set_brightness", description = "decease brightness")
    public void setBrightness(@ServiceParam(name="brightness", description="brightness") int brightness) {
        if(this.client.setBrightness(brightness)) {
            this.brightness = brightness;
            try {
                Catcher.getThingMonitor().updatePropertyAndNotify(this.getId(), "brightness");
            } catch (NotFoundException | IllegalThingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  polling for new state and property
     */
    @Service(name="update", description = "update data", poll = true, interval = 10)
    public void update() {
        this.state = this.client.state() ? CState.ON : CState.OFF;
        this.brightness = this.client.getBrightness();
        this.temperature = this.client.getTemperature();
        try {
            Catcher.getThingMonitor().updateStateAndNotify(this.getId());
            Catcher.getThingMonitor().updatePropertyAndNotify(this.getId(), "brightness");
            Catcher.getThingMonitor().updatePropertyAndNotify(this.getId(), "temperature");
        } catch (NotFoundException | IllegalThingException  e) {
            e.printStackTrace();
        }
    }

    public Light(String type, String name, String description, String ip, String port) {
        super(type, name, description);
        this.client = new Client(ip, port);
    }
}
