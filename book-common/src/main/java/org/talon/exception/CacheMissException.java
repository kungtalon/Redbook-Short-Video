package org.talon.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Created by Zelong
 * On 2022/12/10
 **/


public class CacheMissException extends RuntimeException {

    private String missedkey;
    private String cacheTable;

    private String message;

    public CacheMissException(String key, String tableName) {
        missedkey = key == null ? "" : key;
        cacheTable = tableName == null ? "" : tableName;
        message = "Cache missed for key : " + missedkey + " for table " + cacheTable;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "CacheMissException: " + message;
    }

}
