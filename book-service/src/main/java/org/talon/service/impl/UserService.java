package org.talon.service.impl;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.talon.enums.Sex;
import org.talon.enums.UserInfoModifyType;
import org.talon.enums.YesOrNo;
import org.talon.exception.RedbookException;
import org.talon.mapper.UsersMapper;
import org.talon.pojo.Users;
import org.talon.rest.ResponseStatusEnum;
import org.talon.service.IUserService;
import org.talon.utils.Constants;
import org.talon.utils.DateUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


/**
 * Created by Zelong
 * On 2022/10/30
 **/

@Service
public class UserService implements IUserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Override
    public Users userExists(String email) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("mobile", email);
        return usersMapper.selectOneByExample(userExample);
    }

    @Override
    public Users userExistsById(String userId) {
        return usersMapper.selectByPrimaryKey(userId);
    }

    @Transactional
    @Override
    public Users addUser(String email) {
        String userId = sid.nextShort();

        Users user = new Users();
        user.setId(userId);
        user.setMobile(email);
        user.setNickname("U" + user.genHash());
        user.setImoocNum("U" + user.genHash());
        user.setFace(Constants.USER_FACE1);

        user.setBirthday(DateUtils.stringToDate("1998-05-02"));
        user.setSex(Sex.secret.type);

        user.setCountry("China");
        user.setProvince("Hunan");
        user.setCity("");

        user.setDistrict("");
        user.setDescription("This guy is mysterious... Nothing we know about him...");
        user.setCanImoocNumBeUpdated(YesOrNo.YES.type);

        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        usersMapper.insert(user);

        return user;
    }

    @Transactional
    @Override
    public Users addUser(Users user) {
        usersMapper.insert(user);
        return user;
    }

    @Transactional
    @Override
    public boolean deleteByEmail(String email) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("mobile", email);
        return usersMapper.deleteByExample(userExample) > 0;
    }

    @Transactional
    @Override
    public boolean deleteByUserId(String userId) {
        return usersMapper.deleteByPrimaryKey(userId) > 0;
    }

    @Transactional
    @Override
    public Users updateUserInfo(Users user) {
        if (userExistsById(user.getId()) == null){
            throw new RedbookException(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }
        if (usersMapper.updateByPrimaryKeySelective(user) > 0) {
            return userExistsById(user.getId());
        }
        throw new RedbookException(ResponseStatusEnum.USER_UPDATE_ERROR);
    }

    @Transactional
    @Override
    public Users updateUserInfo(Users user, Integer type) {
        if (type == UserInfoModifyType.NICKNAME.type) {
            Example example = new Example(Users.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("nickname", user.getNickname());
            Users prevUser = usersMapper.selectOneByExample(example);
            if (prevUser != null) {
                throw new RedbookException(
                        ResponseStatusEnum.USER_INFO_UPDATED_NICKNAME_EXIST_ERROR);
            }
        }
        if (type == UserInfoModifyType.IMOOCNUM.type) {
            Example example = new Example(Users.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("imoocNum", user.getNickname());
            Users prevUser = usersMapper.selectOneByExample(example);
            if (prevUser != null) {
                throw new RedbookException(
                        ResponseStatusEnum.USER_INFO_UPDATED_IMOOCNUM_EXIST_ERROR);
            }

            Users tempUser = userExistsById(user.getId());
            if (tempUser.getCanImoocNumBeUpdated() == YesOrNo.NO.type){
                throw new RedbookException(
                        ResponseStatusEnum.USER_INFO_CANT_UPDATED_IMOOCNUM_ERROR);
            }

            user.setCanImoocNumBeUpdated(YesOrNo.NO.type);
        }

        return updateUserInfo(user);
    }
}
