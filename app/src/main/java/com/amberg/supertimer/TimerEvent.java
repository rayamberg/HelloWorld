package com.amberg.supertimer;

/**
 * This class represents a singular event in the sequence of events that occur when the timer is
 * running. The event has a duration in milliseconds, however may also be zero, indicating that
 * the event is untimed. This will be useful if the user does not need a particular rest or work
 * set to be timed. The Activity should handle duration appropriately.
 */

public class TimerEvent {
    public enum Type { WORK, REST }
    Type type;
    private long duration;

    /* default constructor: duration should be in milliseconds
     * and type should be one of the Types defined above.
      * NOTE: duration of 0 means untimed */
    TimerEvent(long duration, Type type) {
        this.duration = duration;
        this.type = type;
    }

    public long getDuration() {
        return this.duration;
    }

    public Type getType() {
        return this.type;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isUntimed() {
        return (this.duration == 0);
    }
}
