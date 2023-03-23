package com.application.db2.app.service.users;

import com.application.db2.app.dtos.users.UsersLoginDto;
import com.application.db2.app.entities.user.UsersLoginEntity;
import com.application.db2.app.repository.dao.basic.BasicDao;
import com.application.db2.app.repository.dao.users.UsersDao;
import com.application.exceptions.CustomException;
import com.application.utils.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UsersServiceImpl implements UsersService {
    public final UsersDao usersDao;
    public final BasicDao basicDao;

    public UsersServiceImpl(UsersDao usersDao, BasicDao basicDao) {
        this.usersDao = usersDao;
        this.basicDao = basicDao;
    }

    @Override
    public String getUsersLoginEntity(String username, String password) {
        if (Objects.nonNull(usersDao.getUserByUsernameAndPass(username, password)))
            return "Access Granted";
        else return "Access not Allowed";
    }

    @Override
    public String requestUser(UsersLoginDto dto) {

        System.out.println("service***********   ");
        if (usersDao.getExistedUsername(dto.getUsername()).equals(dto.getUsername())) {
            throw new CustomException(Messages.USER_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        } else {
            UsersLoginEntity entity = new UsersLoginEntity(dto);
            basicDao.createObject(entity);
            return "inserted";
        }
    }
}
