package com.example.android.customnotifications.event;

/**
 * Represents an event from the notification widget.
 */
public class NotificationEvent {
    private Type mType;

    public enum Type {
        UpCount,
        DownCount;

        public String getName() {
            switch (this) {
                case UpCount:
                    return "up";

                case DownCount:
                    return "down";
            }
            return super.toString();
        }
    }

    public NotificationEvent(Type type) {
        mType = type;
    }

    public Type getEventType() {
        return mType;
    }
}
