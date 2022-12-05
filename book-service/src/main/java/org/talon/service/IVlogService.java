package org.talon.service;

import org.springframework.stereotype.Service;
import org.talon.pojo.Vlog;
import org.talon.utils.pagination.PagedGridResult;
import org.talon.index.IndexVlog;
import org.talon.vo.VlogInfo;

/**
 * Created by Zelong
 * On 2022/11/13
 **/

@Service
public interface IVlogService {
    void createVlog(VlogInfo vi);

    Vlog vlogExistsById(String vlogId);

    PagedGridResult getIndexVlogList(String userId, String search, Integer page, Integer pageSize);

    IndexVlog getVlogDetail(String vlogId, String userId);

    boolean changeVisibility(String userId, String vlogId, Integer isPrivate);

    /* get the vlogs of current user */
    PagedGridResult queryMyVlogList(String userId,
                                    Integer page,
                                    Integer pageSize,
                                    Integer isPrivate);

    Integer getVlogLikedCounts(String vlogId);

    boolean userLikeVlog(String userId, String vlogId, String vloggerId);

    boolean userUndoLikeVlog(String userId, String vlogId, String vloggerId);

    PagedGridResult getMyLikedVlogList(String userId,
                                       Integer page,
                                       Integer pageSize);

    PagedGridResult getMyFollowVlogList(String userId,
                                        Integer page,
                                        Integer pageSize,
                                        Boolean onlyFriend);

}
