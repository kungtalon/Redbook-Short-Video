package org.talon.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.talon.datautils.RedisConsts;
import org.talon.datautils.RedisUtils;

/**
 * Created by Zelong
 * On 2022/10/30
 **/

public class RedisProperties extends RedisConsts {

    @Autowired
    public RedisUtils redis;
}
