package org.talon.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.*;
import org.talon.datautils.RedisUtils;
import org.talon.exception.RedbookException;
import org.talon.rest.JsonResponse;
import org.talon.rest.ResponseStatusEnum;
import org.talon.service.ICommentService;
import org.talon.service.IUserService;
import org.talon.utils.RedisProperties;
import org.talon.vo.CommentInfo;
import springfox.documentation.spring.web.json.Json;

import javax.validation.Valid;

/**
 * Created by Zelong
 * On 2022/10/28
 **/

@Slf4j
@RestController
@RequestMapping("comment")
public class CommentController extends RedisProperties {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICommentService commentService;

    @PostMapping("create")
    public JsonResponse create(@RequestBody @Valid CommentInfo commentInfo)
            throws Exception
    {
        // check whether father comment id exists
        if (!"0".equalsIgnoreCase(commentInfo.getFatherCommentId())) {
            if (commentService.getCommentById(commentInfo.getFatherCommentId()) == null) {
                return JsonResponse.errorCustom(ResponseStatusEnum.COMMENT_NOT_EXISTS_ERROR);
            }
        }

        // check vlogerId and commentUserId valid?
        if (userService.userExistsById(commentInfo.getVlogerId()) == null
         || userService.userExistsById(commentInfo.getCommentUserId()) == null) {
            return JsonResponse.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        CommentInfo reply = commentService.createComment(commentInfo);
        return JsonResponse.ok(reply);
    }

    @GetMapping("counts")
    public JsonResponse counts(@RequestParam String vlogId) {
        String countsStr = redis.getOr(REDIS_VLOG_COMMENT_COUNT + vlogId, "0");

        return JsonResponse.ok(Integer.valueOf(countsStr));
    }

    @GetMapping("list")
    public JsonResponse list(@RequestParam String vlogId,
                             @RequestParam(defaultValue = "") String userId,
                             @RequestParam Integer page,
                             @RequestParam Integer pageSize) {

        return JsonResponse.ok(
                commentService.queryVlogComments(
                        vlogId, userId, page, pageSize
                )
        );
    }

    @DeleteMapping("delete")
    public JsonResponse delete(@RequestParam String commentUserId,
                               @RequestParam String commentId,
                               @RequestParam String vlogId) {
        commentService.deleteComment(commentUserId, commentId, vlogId);
        return JsonResponse.ok();
    }

    @PostMapping("like")
    public JsonResponse like(@RequestParam String commentId,
                             @RequestParam String userId) {
        redis.increment(REDIS_COMMENT_LIKED_COUNT + commentId, 1);
        redis.setHashValue(REDIS_USER_LIKE_COMMENT + commentId, userId, "1");
        return JsonResponse.ok();
    }

    @PostMapping("unlike")
    public JsonResponse unlike(@RequestParam String commentId,
                               @RequestParam String userId) {
        redis.decrement(REDIS_COMMENT_LIKED_COUNT + commentId, 1);
        redis.hdel(REDIS_USER_LIKE_COMMENT + commentId, userId);
        return JsonResponse.ok();
    }
}
