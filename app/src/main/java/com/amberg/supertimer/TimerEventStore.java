package com.amberg.supertimer;
import java.util.ArrayList;

/**
 * Class which will hold all TimerEvents to be processed for a given
 * sequence of work and rest events.
 */
public class TimerEventStore {
    private ArrayList<TimerEvent> mTimerEvents = new ArrayList<>();

    public ArrayList<TimerEvent> getStore() {
        return this.mTimerEvents;
    }

    public void add(TimerEvent t) {
        this.mTimerEvents.add(t);
    }

    public void remove(TimerEvent t) {
        this.mTimerEvents.remove(t);
    }

    public TimerEvent nextEvent(TimerEvent t) {
        if (this.mTimerEvents.contains(t)) {
            int i = this.mTimerEvents.indexOf(t) + 1;
            if (i < this.mTimerEvents.size()) {
                return this.mTimerEvents.get(i);
            }
        }
        return null;
    }
}
