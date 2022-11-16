package org.talon.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.talon.exception.RedbookException;
import org.talon.rest.ResponseStatusEnum;
import org.talon.userinfo.IpUtils;
import org.talon.utils.RedisProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Zelong
 * On 2022/10/30
 **/

@Slf4j
public class PassportInterceptor extends RedisProperties implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String requestIp = IpUtils.getClientIp(request);
        if (requestIp == null || StringUtils.isBlank(requestIp)) {
            return false;
        }

        boolean keyExists = redis.exists(EMAIL_VERICODE + requestIp);
        if (keyExists) {
            throw new RedbookException(ResponseStatusEnum.VERICODE_NEED_WAIT_ERROR);
        }

//        redis.setnx60s(EMAIL_VERICODE + requestIp, requestIp);

//        redis.set(EMAIL_VERICODE + email, code, 5 * 60);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}
