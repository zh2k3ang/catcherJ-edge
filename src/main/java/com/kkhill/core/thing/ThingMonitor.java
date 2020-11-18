package com.kkhill.core.thing;

import com.kkhill.utils.event.EventType;
import com.kkhill.core.event.Event;
import com.kkhill.core.event.EventBus;
import com.kkhill.core.exception.*;
import com.kkhill.utils.event.PropertyUpdatedEventData;
import com.kkhill.utils.event.ServiceCalledEventData;
import com.kkhill.utils.event.StateUpdatedEventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThingMonitor {

    private final Logger logger = LoggerFactory.getLogger(ThingMonitor.class);

    private Map<String, Thing> things;

    private ThingMonitor() {
        this.things = new ConcurrentHashMap<>();
    }

    private static class Holder {
        private static ThingMonitor instance = new ThingMonitor();
    }

    public static ThingMonitor getInstance() {
        return Holder.instance;
    }

    /**
     * parse state, properties, services in a thing and register
     *
     * @param thing
     * @return
     * @throws IllegalThingException
     */
    public String registerThing(Thing thing) throws IllegalThingException {

        String id = thing.build();
        things.put(id, thing);
        EventBus.getInstance().fire(new Event(EventType.THING, "registered", id));
        logger.info("thing has been registered, name: {}, id: {}", thing.getFriendlyName(), id);
        return id;
    }

    public void removeThing(String thingId) {

        things.remove(thingId);
        EventBus.getInstance().fire(new Event(EventType.THING, "removed", thingId));
        logger.info("thing has been removed, id: {}", thingId);
    }

    public Map<String, Thing> getThings() {
        return this.things;
    }

    public Thing getThing(String id) throws NotFoundException {
        Thing thing = this.things.get(id);
        if(thing == null) throw new NotFoundException(String.format("not found thing: %s", id));
        return thing;
    }

    /**
     * update and notify state of a thing
     * do not fire event bus if not changed
     * usually used by thing implementation
     * e.g. updateAndNotifyState(this, "on");
     *
     * @param id
     * @throws NotFoundException
     * @throws IllegalThingException
     */
    public void updateStateAndNotify(String id) throws NotFoundException, IllegalThingException {

        State state = getThing(id).getState();
        String oldState = state.getValue();
        String newState = state.updateValue();
        System.out.println(id+"!!!test!!!!!"+oldState+"---"+newState);
        // do not notify if state is not changed
        if(oldState.equals(newState)) return;

        Object data = new StateUpdatedEventData(id, oldState, newState);
        EventBus.getInstance().fire(new Event(EventType.STATE_UPDATED, id, data));
        logger.info("update thing state, id: {}, from {} to {}", id, oldState, newState);
    }

    /**
     * update property and notify
     * do not fire event bus if not changed
     * usually used by thing implementation
     * e.g. updateAndNotifyProperty(this, "brightness", 50)
     *
     * @param id
     * @param name
     * @throws NotFoundException
     * @throws IllegalAccessException
     */
    public void updatePropertyAndNotify(String id, String name) throws NotFoundException, IllegalThingException {

        Property property = getThing(id).getProperties().get(name);
        if(property == null) throw new NotFoundException(String.format("property not found: %s", name));

        Object oldValue = property.getValue();
        Object newValue = property.updateValue();
        // do not notify if value is not changed
        if(oldValue.equals(newValue)) return;

        Object data = new PropertyUpdatedEventData(id, name, oldValue, newValue);
        EventBus.getInstance().fire(new Event(EventType.PROPERTY_UPDATED, id, data));
        logger.info("update thing property, id: {}, from {} to {}", id, oldValue, newValue);
    }

    /**
     * call service of thing
     * usually used by other things rather than this.
     * e.g. callService("some_id_from_UI", "set_brightness", new Object[]{50})
     *
     *
     * @param thingId
     * @param service
     * @param args
     * @throws NotFoundException
     */
    public Object callServiceAndNotify(String thingId, String service, Object... args) throws NotFoundException {

        Service s = getThing(thingId).getServices().get(service);
        if (s == null) throw new NotFoundException(String.format("service not found: %s", service));
        Object data = new ServiceCalledEventData(thingId, service);
        Object res = null;
        try {
            res = s.invoke(args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("error with calling thing service");
            e.printStackTrace();
        }
        EventBus.getInstance().fire(new Event(EventType.SERVICE_CALLED, thingId, data));
        logger.info("call thing service, id: {}, service: {}", thingId, service);
        return res;
    }

    public void enableThing(String thingId) throws NotFoundException {

        Thing thing = getThing(thingId);
        if(!thing.isAvailable()) {
            thing.enable();
            EventBus.getInstance().fire(new Event(EventType.THING, thingId, "enabled"));
            logger.info("enable thing, id: {}", thingId);
        }
    }

    public void disableThing(String thingId) throws NotFoundException {

        Thing thing = getThing(thingId);
        if(thing.isAvailable()) {
            thing.disable();
            EventBus.getInstance().fire(new Event(EventType.THING, thingId, "disabled"));
            logger.info("disable thing, id: {}", thingId);
        }
    }
}
