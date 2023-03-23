package com.application.db2.app.dtos.users;

import com.application.utils.hibernate.Utils;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class UsersLoginDto {
    private String username;
    private String password;
    private String userType;
    private String emailContact;
    private BigDecimal validation;
    private String firstName;
    private String lastName;
    private String location;


    public String getPassword() {
        return Utils.encryptPassword(password);
    }

    public void setPassword(String password) {
        this.password = Utils.encryptPassword(password);
    }
}
