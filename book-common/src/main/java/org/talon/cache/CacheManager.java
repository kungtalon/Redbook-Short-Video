package org.talon.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zelong
 * On 2022/12/6
 **/

public class CacheManager {

    private static int maxSize = 100;
    private static Map<TableNames, Cache<String, Object>> allTables = new HashMap<>();

    public enum TableNames {
        VLOG_CACHE,
        COMMENT_CACHE,
        USERINFO_CACHE,
        FANS_CACHE,
    }

    private static void createTable(TableNames name) {
        Cache<String, Object> cache = CacheBuilder
                .newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(10 * 60, TimeUnit.SECONDS)
                .build();
        allTables.put(name, cache);
    }

    public static Cache<String, Object> ofTable(TableNames name) {
        if (!allTables.containsKey(name)) {
            createTable(name);
        }
        return allTables.get(name);
    }
}
