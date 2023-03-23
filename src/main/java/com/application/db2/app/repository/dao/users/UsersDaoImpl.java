package com.application.db2.app.repository.dao.users;

import com.application.db2.app.entities.user.UsersLoginEntity;
import com.application.db2.app.repository.query.users.UsersQueries;
import com.application.utils.UtilsRepository;
import com.application.utils.hibernate.CustomParameter;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional(timeout = 300)
public class UsersDaoImpl implements UsersDao {
    public final SessionFactory sessionFactory;

    public UsersDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public UsersLoginEntity getUserByUsernameAndPass(String username, String password) {
        List<CustomParameter> list = new ArrayList<>();
        list.add(new CustomParameter(UsersQueries.P_USERNAME, username, String.class));
        list.add(new CustomParameter(UsersQueries.P_PASSWORD, password, String.class));
        return Objects.isNull(UtilsRepository.getObjectByAliasBeanWithParams(sessionFactory, UsersQueries.GET_USER_BY_USERNAME_AND_PASS, UsersLoginEntity.class, list)) ? null :
                (UsersLoginEntity) UtilsRepository.getObjectByAliasBeanWithParams(sessionFactory, UsersQueries.GET_USER_BY_USERNAME_AND_PASS, UsersLoginEntity.class, list);
    }

    @Override
    public String getExistedUsername(String username){
        System.out.println("repository********   ");
        List<CustomParameter> list = new ArrayList<>();
        list.add(new CustomParameter(UsersQueries.P_USERNAME, username, String.class));
        UsersLoginEntity entity = (UsersLoginEntity) UtilsRepository.getObjectByAliasBeanWithParams(sessionFactory,UsersQueries.GET_EXISTING_USER,UsersLoginEntity.class,list);
        return Objects.isNull(entity)?null:
               entity.getUsername();
    }
}
