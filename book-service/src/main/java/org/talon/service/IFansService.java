package org.talon.service;

import org.springframework.stereotype.Service;
import org.talon.pojo.Fans;
import org.talon.utils.pagination.PagedGridResult;

@Service
public interface IFansService {

    boolean doFollow(String myUserId, String followUserId);

    boolean doCancel(String myUserId, String followUserId);

    Fans queryFansRelation(String fansId, String vlogerId);

    PagedGridResult queryMyFollows(String myUserId, Integer page, Integer pageSize);

    PagedGridResult queryMyFans(String myUserId, Integer page, Integer pageSize);
}
