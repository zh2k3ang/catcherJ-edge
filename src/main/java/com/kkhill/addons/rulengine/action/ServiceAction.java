package com.kkhill.addons.rulengine.action;

import com.kkhill.core.Catcher;
import com.kkhill.core.exception.NotFoundException;

public class ServiceAction extends Action {

    private String name;
    private String thing;

    public ServiceAction(String name, String thing) {
        this.name = name;
        this.thing = thing;
    }

    public String getName() {
        return name;
    }

    public String getThing() {
        return thing;
    }

    @Override
    public void execute() {
        try {
            Catcher.getThingMonitor().callServiceAndNotify(thing, name, null);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }
}