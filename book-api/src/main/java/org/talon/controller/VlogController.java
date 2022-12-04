package org.talon.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.talon.enums.YesOrNo;
import org.talon.rest.JsonResponse;
import org.talon.rest.ResponseStatusEnum;
import org.talon.service.IUserService;
import org.talon.service.IVlogService;
import org.talon.utils.RedisProperties;
import org.talon.utils.pagination.PagedGridResult;
import org.talon.vo.IndexVlog;
import org.talon.vo.VlogInfo;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/10/30
 **/

@Slf4j
@Api(tags = "VlogController - Create and update vlogs")
@RequestMapping("vlog")
@RestController
public class VlogController extends RedisProperties {
    @Autowired
    private IVlogService vlogService;

    @Autowired
    private IUserService userService;

    @PostMapping("publish")
    public JsonResponse publish(@RequestBody VlogInfo vlogInfo) throws Exception {
        vlogService.createVlog(vlogInfo);
        return JsonResponse.ok();
    }

    @GetMapping("indexList")
    public JsonResponse indexList(@RequestParam(defaultValue="") String userId,
                                  @RequestParam(defaultValue="") String search,
                                  @RequestParam Integer page,
                                  @RequestParam Integer pageSize) throws Exception {
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        PagedGridResult result = vlogService.getIndexVlogList(userId, search, page, pageSize);
        return JsonResponse.ok(result);
    }

    @GetMapping("detail")
    public JsonResponse detail(@RequestParam(defaultValue = "") String userId,
                               @RequestParam String vlogId) throws Exception {
        IndexVlog vlog = vlogService.getVlogDetail(vlogId, userId);
        return JsonResponse.ok(vlog);
    }

    @PostMapping("changeToPrivate")
    public JsonResponse changeToPrivate(@RequestParam String userId,
                                        @RequestParam String vlogId) {
        boolean success = vlogService.changeVisibility(userId, vlogId, YesOrNo.YES.type);
        if (success) {
            return JsonResponse.ok(vlogService.getVlogDetail(vlogId, userId));
        }
        return JsonResponse.errorCustom(ResponseStatusEnum.VLOG_UPDATE_ERROR);
    }

    @PostMapping("changeToPublic")
    public JsonResponse changeToPublic(@RequestParam String userId,
                                       @RequestParam String vlogId) {
        boolean success = vlogService.changeVisibility(userId, vlogId, YesOrNo.NO.type);
        if (success) {
            return JsonResponse.ok(vlogService.getVlogDetail(vlogId, userId));
        }
        return JsonResponse.errorCustom(ResponseStatusEnum.VLOG_UPDATE_ERROR);
    }

    @GetMapping("myPublicList")
    public JsonResponse myPublicList(@RequestParam String userId,
                                     @RequestParam Integer page,
                                     @RequestParam Integer pageSize) {
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        PagedGridResult result = vlogService.queryMyVlogList(userId,
                                                             page,
                                                             pageSize,
                                                             YesOrNo.NO.type);
        return JsonResponse.ok(result);
    }

    @GetMapping("myPrivateList")
    public JsonResponse myPrivateList(@RequestParam String userId,
                                     @RequestParam Integer page,
                                     @RequestParam Integer pageSize) {
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        PagedGridResult result = vlogService.queryMyVlogList(userId,
                page,
                pageSize,
                YesOrNo.YES.type);
        return JsonResponse.ok(result);
    }

    @PostMapping("like")
    public JsonResponse like(@RequestParam String userId,
                             @RequestParam String vlogId,
                             @RequestParam String vlogerId) {
        if (userService.userExistsById(userId) == null
                || vlogService.vlogExistsById(vlogId) == null
                || userService.userExistsById(vlogerId) == null) {
            return JsonResponse.errorCustom(ResponseStatusEnum.FAILED);
        }

        if (redis.exists(REDIS_USER_LIKE_VLOG + userId + ":" + vlogId)) {
            return JsonResponse.errorCustom(ResponseStatusEnum.USER_LIKE_VLOG_ERROR);
        }

        vlogService.userLikeVlog(userId, vlogId, vlogerId);
        return JsonResponse.ok();
    }

    @PostMapping("unlike")
    public JsonResponse unlike(@RequestParam String userId,
                               @RequestParam String vlogId,
                               @RequestParam String vlogerId) {
        if (userService.userExistsById(userId) == null
                || vlogService.vlogExistsById(vlogId) == null
                || userService.userExistsById(vlogerId) == null) {
            return JsonResponse.errorCustom(ResponseStatusEnum.FAILED);
        }

        vlogService.userUndoLikeVlog(userId, vlogId, vlogerId);
        return JsonResponse.ok();
    }

    @PostMapping("totalLikedCounts")
    public JsonResponse totalLikedCounts(@RequestParam String vlogId) {
        if (vlogService.vlogExistsById(vlogId) == null) {
            return JsonResponse.errorCustom(ResponseStatusEnum.VLOG_NOT_EXISTS_ERROR);
        }

        return JsonResponse.ok(vlogService.getVlogLikedCounts(vlogId));
    }

    @GetMapping("myLikedList")
    public JsonResponse myLikedList(@RequestParam String userId,
                                      @RequestParam Integer page,
                                      @RequestParam Integer pageSize) {
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        PagedGridResult result = vlogService.getMyLikedVlogList(
                    userId,
                    page,
                    pageSize);
        return JsonResponse.ok(result);
    }

    @GetMapping("followList")
    public JsonResponse followList(@RequestParam String myId,
                                    @RequestParam Integer page,
                                    @RequestParam Integer pageSize) {
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        PagedGridResult result = vlogService.getMyFollowVlogList(
                myId,
                page,
                pageSize,
                Boolean.FALSE);
//        log.info(result.getRows().toString());
        return JsonResponse.ok(result);
    }

    @GetMapping("friendList")
    public JsonResponse friendList(@RequestParam String myId,
                                   @RequestParam Integer page,
                                   @RequestParam Integer pageSize) {
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        PagedGridResult result = vlogService.getMyFollowVlogList(
                myId,
                page,
                pageSize,
                Boolean.TRUE);
//        log.info(result.getRows().toString());
        return JsonResponse.ok(result);
    }

}
