package org.talon.service.impl;

import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.talon.datautils.RedisConsts;
import org.talon.datautils.RedisUtils;
import org.talon.enums.MessageEnum;
import org.talon.exception.RedbookException;
import org.talon.mongo.Message;
import org.talon.pojo.Users;
import org.talon.repository.IMessageRepository;
import org.talon.rest.ResponseStatusEnum;
import org.talon.service.IMsgService;
import org.talon.service.IUserService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zelong
 * On 2022/12/4
 **/

@Service
public class MsgService implements IMsgService {

    @Autowired
    private IMessageRepository messageRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisUtils redis;

    @Override
    public void createMsg(String fromUserId, String toUserId, Integer msgType, Map msgContent) {
        Message msg = new Message();

        Users fromUser = userService.userExistsById(fromUserId);
        Users toUser = userService.userExistsById(toUserId);

        if(fromUser == null || toUser == null) {
            throw new RedbookException(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        msg.setFromUserId(fromUserId);
        msg.setFromNickname(fromUser.getNickname());
        msg.setFromFace(fromUser.getFace());

        msg.setToUserId(toUserId);
        msg.setToNickname(toUser.getNickname());
        msg.setToFace(toUser.getFace());

        msg.setMsgType(msgType);
        if (msgContent != null){
            msg.setMsgContent(msgContent);
        }

        msg.setCreateTime(new Date());

        messageRepository.save(msg);
    }

    @Override
    public List<Message> queryComments(String toUserId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page,
                pageSize,
                Sort.Direction.DESC,
                "createTime");

        List<Message> results = messageRepository.findAllByToUserIdOrderByCreateTimeDesc(toUserId, pageable);

        results.forEach(msg -> {
            // if type is follow, check whether we become friend
            if (msg.getMsgType() != null && msg.getMsgType() == MessageEnum.FOLLOW_YOU.type) {
                Map<String, Object> map = msg.getMsgContent();
                if (map == null) {
                    map = new HashMap();
                }

                String relation = redis.get(
                        RedisConsts.REDIS_ME_FOLLOWS_VLOGER + msg.getToUserId() + ":" + msg.getFromUserId());
                if (StringUtils.isNotBlank(relation) && "1".equalsIgnoreCase(relation)) {
                    map.put("isFriend", true);
                } else {
                    map.put("isFriend", false);
                }

                msg.setMsgContent(map);
            }
        });

        return results;
    }


}
