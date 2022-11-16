package org.talon.controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.talon.config.MinIOConfig;
import org.talon.datautils.MinIOUtils;
import org.talon.enums.YesOrNo;
import org.talon.rest.JsonResponse;
import org.talon.rest.ResponseStatusEnum;
import org.talon.service.IFansService;
import org.talon.service.IUserService;
import org.talon.service.impl.FansService;
import org.talon.utils.RedisProperties;

import javax.validation.constraints.NotBlank;

/**
 * Created by Zelong
 * On 2022/10/28
 **/

@Slf4j
@RequestMapping("fans")
@RestController
public class FansController extends RedisProperties {

    @Autowired
    private IFansService fansService;
    @Autowired
    private IUserService userService;

    @ApiOperation(value = "follow - me follow another vlogger")
    @PostMapping("follow")
    public JsonResponse follow(@RequestParam @NotBlank String myId,
                               @RequestParam @NotBlank String vlogerId) throws Exception {
        // you cannot follow yourself
        if (myId.equalsIgnoreCase(vlogerId)) {
            return JsonResponse.errorCustom(ResponseStatusEnum.USER_FOLLOW_THEMSELVES_ERROR);
        }

        // check whether users exist
        if (userService.userExistsById(myId) == null
                || userService.userExistsById(vlogerId) == null) {
            return JsonResponse.errorCustom(ResponseStatusEnum.USER_FOLLOW_STATS_ERROR);
        }

        // check whether me already follows vloger
        if (redis.exists(REDIS_ME_FOLLOWS_VLOGER + myId + ":" + vlogerId)) {
            return JsonResponse.errorCustom(ResponseStatusEnum.USER_FOLLOW_STATS_ERROR);
        }

        if (fansService.queryFansRelation(myId, vlogerId) != null) {
            redis.set(REDIS_ME_FOLLOWS_VLOGER + myId + ":" + vlogerId, "1");
            return JsonResponse.errorCustom(ResponseStatusEnum.USER_FOLLOW_STATS_ERROR);
        }

        fansService.doFollow(myId, vlogerId);

        return JsonResponse.ok();
    }

    @ApiOperation(value = "cancel - cancel following a vlogger")
    @PostMapping("cancel")
    public JsonResponse cancel(@RequestParam @NotBlank String myId,
                               @RequestParam @NotBlank String vlogerId) throws Exception {
        // you cannot follow yourself
        if (myId.equalsIgnoreCase(vlogerId)) {
            return JsonResponse.errorCustom(ResponseStatusEnum.USER_FOLLOW_THEMSELVES_ERROR);
        }

        // check whether users exist
        if (userService.userExistsById(myId) == null
                || userService.userExistsById(vlogerId) == null) {
            return JsonResponse.errorCustom(ResponseStatusEnum.USER_FOLLOW_STATS_ERROR);
        }

        if (fansService.doCancel(myId, vlogerId)) {
            return JsonResponse.ok();
        }

        return JsonResponse.errorCustom(ResponseStatusEnum.USER_FOLLOW_STATS_ERROR);
    }

    @ApiOperation(value = "queryDoIFollowVloger - am I following vlogger?")
    @GetMapping("queryDoIFollowVloger")
    public JsonResponse queryDoIFollowVloger(@RequestParam @NotBlank String myId,
                                             @RequestParam @NotBlank String vlogerId)
                                                throws Exception {
        // you cannot follow yourself
        if (redis.exists(REDIS_ME_FOLLOWS_VLOGER + myId + ":" + vlogerId)) {
            return JsonResponse.ok(YesOrNo.YES.type);
        }

        return JsonResponse.ok(YesOrNo.NO.type);
    }

    @ApiOperation(value = "queryMyFollows - get the list of vloggers I follow")
    @GetMapping("queryMyFollows")
    public JsonResponse queryMyFollows(@RequestParam @NotBlank String myId,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize)
                                        throws Exception {
        return JsonResponse.ok(fansService.queryMyFollows(myId, page, pageSize));
    }

    @ApiOperation(value = "queryMyFollows - get the list of my fans")
    @GetMapping("queryMyFans")
    public JsonResponse queryMyFans(@RequestParam @NotBlank String myId,
                                   @RequestParam Integer page,
                                   @RequestParam Integer pageSize)
            throws Exception {
        return JsonResponse.ok(fansService.queryMyFans(myId, page, pageSize));
    }
}
