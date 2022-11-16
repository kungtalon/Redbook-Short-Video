package org.talon.service;

import org.springframework.stereotype.Service;
import org.talon.utils.pagination.PagedGridResult;
import org.talon.vo.IndexVlog;
import org.talon.vo.VlogInfo;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/11/13
 **/

@Service
public interface IVlogService {
    void createVlog(VlogInfo vi);

    PagedGridResult getIndexVlogList(String search, Integer page, Integer pageSize);

    IndexVlog getVlogDetail(String vlogId);

    boolean changeVisibility(String userId, String vlogId, Integer isPrivate);

    /* get the vlogs of current user */
    PagedGridResult queryMyVlogList(String userId,
                                    Integer page,
                                    Integer pageSize,
                                    Integer isPrivate);
}
