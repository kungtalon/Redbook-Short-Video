package org.talon.mapper;

import org.springframework.stereotype.Repository;
import org.talon.my.MyMapper;
import org.talon.pojo.Users;

@Repository
public interface UsersMapper extends MyMapper<Users> {
}