package org.talon.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.talon.pojo.Users;
import org.talon.rest.JsonResponse;
import org.talon.rest.ResponseStatusEnum;
import org.talon.service.IUserService;
import org.talon.userinfo.IpUtils;
import org.talon.userverification.VerificationService;
import org.talon.utils.RedisProperties;
import org.talon.vo.RegistLoginRequest;
import org.talon.vo.UserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

/**
 * Created by Zelong
 * On 2022/10/28
 **/

@Slf4j
@RestController
@RequestMapping("/passport")
public class PassportController extends RedisProperties {

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private IUserService userService;

    @ApiOperation(value = "passport - get verification code")
    @PostMapping("getVeriCode")
    public JsonResponse getVeriCode(@RequestParam String email,
                                    HttpServletRequest request) {
        log.info("Receiving new registration, email:" + email);

        if (StringUtils.isBlank(email)) {
            return JsonResponse.ok();
        }

        String requestIp = IpUtils.getClientIp(request);
        if (requestIp == null || StringUtils.isBlank(requestIp)) {
            return JsonResponse.ok();
        }
        redis.setnx60s(EMAIL_VERICODE + requestIp, requestIp);
        String code = (int)((Math.random() * 9 + 1) * 100000) + "";

//        verificationService.sendVerificationInfo(email, code);
        log.info("Getting verification code:" + code);

        redis.set(EMAIL_VERICODE + email, code, 5 * 60);

        return JsonResponse.ok();
    }

    @ApiOperation(value = "passport - login or register")
    @PostMapping("login")
    public JsonResponse login(@Valid @RequestBody RegistLoginRequest loginRequest,
                                    HttpServletRequest request) {

        String email = loginRequest.getEmail();
        String code = loginRequest.getVerifyCode();

        // check the information in redis
        String redisCode = redis.get(EMAIL_VERICODE + email);
        if (StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(code)) {
            return JsonResponse.errorCustom(ResponseStatusEnum.VERICODE_ERROR);
        }

        // does user exists?
        Users user = userService.userExists(email);
        if (user == null) {
            user = userService.addUser(email);
        }

        // create a session for user, keep session and token in redis
        String userToken = UUID.randomUUID().toString();
        redis.set(REDIS_USER_TOKEN + user.getId(), userToken, 10 * 60);

        // delete the verification code!!!
        redis.del(EMAIL_VERICODE + email);

        // return the user info and the token
        UserInfo uf = new UserInfo();
        BeanUtils.copyProperties(user, uf);
        uf.setUserToken(userToken);

        return JsonResponse.ok(uf);
    }

    @ApiOperation(value = "passport - logout")
    @PostMapping("logout")
    public JsonResponse logout(@RequestParam String userId,
                               HttpServletRequest request) {
        redis.del(REDIS_USER_TOKEN + userId);

        return JsonResponse.ok();
    }
}
