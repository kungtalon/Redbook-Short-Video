package org.talon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.talon.mapper.UsersMapper;
import org.talon.pojo.Users;

import java.util.List;

/**
 * Created by Zelong
 * On 2022/10/30
 **/

@Service
public interface IUserService {

    Users userExists(String email);

    Users userExistsById(String userId);

    Users addUser(String email);

    Users addUser(Users user);

    boolean deleteByEmail(String email);

    boolean deleteByUserId(String userId);

    Users updateUserInfo(Users user);

    Users updateUserInfo(Users user, Integer type);
}
