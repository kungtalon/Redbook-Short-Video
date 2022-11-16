package org.talon.utils.pagination;


import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/11/13
 **/

public class MysqlPageUtils {

    public static PagedGridResult setterPagedGrid(List<?> list,
                                           Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(list);
        gridResult.setPage(page);
        gridResult.setRecords(pageList.getTotal());
        gridResult.setTotal(pageList.getPages());
        return gridResult;
    }

}
