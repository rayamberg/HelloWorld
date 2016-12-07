package com.amberg.supertimer;

import java.util.UUID;

/**
 * This class represents a singular event in the sequence of events that occur when the timer is
 * running. The event has a duration in milliseconds, however may also be zero, indicating that
 * the event is untimed. This will be useful if the user does not need a particular rest or work
 * set to be timed. The Activity should handle duration appropriately.
 */

public class TimerEvent {
    private UUID mId;
    public enum Type { WORK, REST }
    Type mType;
    private long mDuration;
    private Long mEventEnd;

    /* default constructor: duration should be in milliseconds
     * and type should be one of the Types defined above.
      * NOTE: duration of 0 means untimed */
    TimerEvent(long duration, Type type) {
        mId = UUID.randomUUID();
        mDuration = duration;
        mType = type;
    }

    public UUID getId() {
        return mId;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public Type getType() {
        return this.mType;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public void setType(Type type) {
        this.mType = type;
    }

    public boolean isUntimed() {
        return (this.mDuration == 0);
    }

    public Long getEventEnd() {
        return this.mEventEnd;
    }

    public void setEventEnd(Long eventEnd) {
        this.mEventEnd = eventEnd;
    }
}
