package com.kkhill.drivers.coap.things;

import com.kkhill.common.thing.CService;
import com.kkhill.common.thing.CState;
import com.kkhill.core.Catcher;
import com.kkhill.core.annotation.Property;
import com.kkhill.core.annotation.Service;
import com.kkhill.core.annotation.State;
import com.kkhill.core.exception.IllegalThingException;
import com.kkhill.core.exception.NotFoundException;
import com.kkhill.core.thing.Thing;
import com.kkhill.drivers.coap.lib.Client;

public class Lock extends Thing {

    private Client client;

    @State(description = "state")
    public String state = CState.OFFLINE;

    @Property(name = "vendor", description = "设备厂商")
    public String vendor = "zh2k3ang";

    @Service(name = CService.OPEN, description = "开门")
    public void open() {
        String res = this.client.send("state", "open", "PUT");
        if(res == null) return;
        this.state = CState.OPENED;
        try {
            Catcher.getThingMonitor().updateStateAndNotify(this.getId());
        } catch (NotFoundException | IllegalThingException e) {
            e.printStackTrace();
        }
    }

    @Service(name = CService.CLOSE, description = "关门")
    public void close() {
        String res = this.client.send("state", "close", "PUT");
        if(res == null) return;
        this.state = CState.CLOSED;
        try {
            Catcher.getThingMonitor().updateStateAndNotify(this.getId());
        } catch (NotFoundException | IllegalThingException e) {
            e.printStackTrace();
        }
    }

    @Service(name = "update", description = "update data", poll = true)
    public void update() {
        try {
            String s = this.client.send("state", null, "GET");
            this.state = s == null ? CState.OFFLINE : s;
            Catcher.getThingMonitor().updateStateAndNotify(this.getId());
        } catch (NotFoundException | IllegalThingException e) {
            e.printStackTrace();
        }
    }

    public Lock(String type, String name, String description, String ip, String port) {
        super(type, name, description);
        this.client = new Client(ip, port);
    }
}
