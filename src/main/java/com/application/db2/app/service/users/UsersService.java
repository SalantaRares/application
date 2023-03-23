package com.application.db2.app.service.users;

import com.application.db2.app.dtos.users.UsersLoginDto;
import com.application.db2.app.entities.user.UsersLoginEntity;

public interface UsersService {
    String getUsersLoginEntity(String username, String password);

    String requestUser(UsersLoginDto dto);
}
