package com.application.db2.app.repository.dao.users;

import com.application.db2.app.entities.user.UsersLoginEntity;

public interface UsersDao {
    UsersLoginEntity getUserByUsernameAndPass(String username, String password);

    String getExistedUsername(String username);
}
