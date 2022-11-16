package org.talon.controller;

import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.talon.config.MinIOConfig;
import org.talon.datautils.MinIOUtils;
import org.talon.enums.FileTypeEnum;
import org.talon.enums.UserInfoModifyType;
import org.talon.exception.RedbookException;
import org.talon.pojo.Users;
import org.talon.rest.JsonResponse;
import org.talon.rest.ResponseStatusEnum;
import org.talon.service.IUserService;
import org.talon.utils.RedisProperties;
import org.talon.vo.UpdateUserRequest;
import org.talon.vo.UserInfo;

/**
 * Created by Zelong
 * On 2022/10/30
 **/

@Slf4j
@Api(tags = "UserInfoController - Getting all info about users")
@RequestMapping("userInfo")
@RestController
public class UserInfoController extends RedisProperties {
    @Autowired
    private IUserService userService;
    @Autowired
    private MinIOConfig minIOConfig;

    @GetMapping("query")
    public Object query(@RequestParam String userId) throws RedbookException {
        Users user = userService.userExistsById(userId);

        if (user == null) {
            throw new RedbookException(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }
        UserInfo uf = new UserInfo();
        BeanUtils.copyProperties(user, uf);

        // my following
        String myFollowingCntStr = redis.get(REDIS_MY_FOLLOWING_COUNT + userId);

        // my fans
        String myFansCntStr = redis.get(REDIS_MY_FANS_COUNT + userId);

        // my liked
        String myVlogLikedCntStr = redis.get(REDIS_MY_VLOG_BE_LIKED_COUNT + userId);
        String myCommentLikedCntStr = redis.get(REDIS_MY_COMMENT_BE_LIKED_COUNT + userId);

        Long myFollowingCnt = 0L;
        Long myFansCnt = 0L;
        Long myVlogLikedCnt = 0L;
        Long myCommentLikedCnt = 0L;

        try {
            if (!StringUtils.isBlank(myFollowingCntStr))
                myFollowingCnt = Long.valueOf(myFollowingCntStr);
            if (!StringUtils.isBlank(myFansCntStr))
                myFansCnt = Long.valueOf(myFansCntStr);
            if (!StringUtils.isBlank(myVlogLikedCntStr))
                myVlogLikedCnt = Long.valueOf(myVlogLikedCntStr);
            if (!StringUtils.isBlank(myCommentLikedCntStr))
                myCommentLikedCnt = Long.valueOf(myCommentLikedCntStr);
        } catch (Exception e) {
            log.debug(e.toString());
        }

        uf.setMyFollowsCounts(myFollowingCnt);
        uf.setMyLikedVlogCounts(myVlogLikedCnt);
        uf.setMyFansCounts(myFansCnt);
        uf.setTotalLikeMeCounts(myVlogLikedCnt + myCommentLikedCnt);

        return JsonResponse.ok(uf);
    }

    @PostMapping("modifyUserInfo")
    public Object modifyUserInfo(@RequestBody UpdateUserRequest updateUserRequest,
                                 @RequestParam Integer type) throws Exception {

        UserInfoModifyType.checkUserInfoTypeIsRight(type);

        Users updatedUser = userService.updateUserInfo(updateUserRequest.toUserPojo());
        UserInfo newUserInfo = new UserInfo();
        BeanUtils.copyProperties(updatedUser, newUserInfo);

        return JsonResponse.ok(newUserInfo);
    }

    @ApiOperation(value = "userinfo - modify the image in user profile")
    @PostMapping("modifyImage")
    public JsonResponse modifyImage(@RequestParam String userId,
                                    @RequestParam Integer type,
                                    MultipartFile file) throws Exception {


        if (type != FileTypeEnum.BGIMG.type && type != FileTypeEnum.FACE.type) {
            return JsonResponse.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String fileName = file.getOriginalFilename();

        MinIOUtils.uploadFile(minIOConfig.getBucketName(), fileName, file.getInputStream());

        String imgUrl = String.join("/",
                minIOConfig.getFileHost(),
                minIOConfig.getBucketName(),
                fileName);

        // write the img url to database
        UpdateUserRequest updatedUserInfo = new UpdateUserRequest();
        updatedUserInfo.setId(userId);

        if (type == FileTypeEnum.BGIMG.type) {
            updatedUserInfo.setBgImg(imgUrl);
        } else {
            updatedUserInfo.setFace(imgUrl);
        }

        Users user = userService.updateUserInfo(updatedUserInfo.toUserPojo());

        return JsonResponse.ok(user);
    }

}
