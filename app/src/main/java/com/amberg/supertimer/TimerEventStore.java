package com.amberg.supertimer;
import java.util.ArrayList;

/**
 * Class which will hold all TimerEvents to be processed for a given
 * sequence of work and rest events.
 */
public class TimerEventStore {
    private ArrayList<TimerEvent> timerEvents = new ArrayList<>();

    public ArrayList<TimerEvent> getStore() {
        return this.timerEvents;
    }

    public void add(TimerEvent t) {
        this.timerEvents.add(t);
    }

    public void remove(TimerEvent t) {
        this.timerEvents.remove(t);
    }

    public TimerEvent nextEvent(TimerEvent t) {
        if (this.timerEvents.contains(t)) {
            int i = this.timerEvents.indexOf(t) + 1;
            if (i < this.timerEvents.size()) {
                return this.timerEvents.get(i);
            }
        }
        return null;
    }
}
