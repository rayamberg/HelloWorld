package com.amberg.supertimer;
import java.util.ArrayList;

/**
 * Class which will hold all TimerEvents to be processed for a given
 * sequence of work and rest events.
 */
public class TimerEventStore {
    private ArrayList<TimerEvent> mTimerEvents = new ArrayList<>();

    public TimerEventStore() {

    }

    public ArrayList<TimerEvent> getStore() {
        return this.mTimerEvents;
    }

    public int size() { return this.mTimerEvents.size(); }

    public void add(TimerEvent t) {
        this.mTimerEvents.add(t);
    }

    public void remove(TimerEvent t) {
        this.mTimerEvents.remove(t);
    }

    public void remove(int i) { this.mTimerEvents.remove(i); }

    public TimerEvent currentEvent() {
        return this.mTimerEvents.get(0);
    }
    public TimerEvent nextEvent() {
        int i = this.mTimerEvents.indexOf(this.currentEvent()) + 1;
        if (i < this.mTimerEvents.size()) {
            return this.mTimerEvents.get(i);
        }
        return null;
    }
}
