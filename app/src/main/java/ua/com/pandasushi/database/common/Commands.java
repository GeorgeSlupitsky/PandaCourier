package ua.com.pandasushi.database.common;

import java.io.Serializable;

public enum Commands implements Serializable {
    CHECK,
    START_CHANGE,
    END_CHANGE,
    GET_ORDER_LIST,
    UPDATE_ORDER,
    SAVE_TRACK
}
