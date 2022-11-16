package org.talon.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.talon.exception.RedbookException;
import org.talon.rest.JsonResponse;
import org.talon.rest.ResponseStatusEnum;
import org.talon.utils.RedisProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Zelong
 * On 2022/11/13
 **/

@Slf4j
public class UserTokenInterceptor extends RedisProperties implements HandlerInterceptor {

    private static final String HEADER_USER_ID = "headerUserId";
    private static final String HEADER_USER_TOKEN = "headerUserToken";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String userId = request.getHeader(HEADER_USER_ID);
        String userToken = request.getHeader(HEADER_USER_TOKEN);

        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String redisUserToken = redis.get(REDIS_USER_TOKEN + userId);
            if (StringUtils.isBlank(redisUserToken)) {
                throw new RedbookException(ResponseStatusEnum.UN_LOGIN);
            } else {
                // check token consistency
                // if the tokens are not the same, it means user has already logged in
                // on another device!
                if (!redisUserToken.equalsIgnoreCase(userToken)) {
                    throw new RedbookException(ResponseStatusEnum.TICKET_INVALID);
                }
            }
        } else {
            throw new RedbookException(ResponseStatusEnum.UN_LOGIN);
        }

        return true;
    }

}
