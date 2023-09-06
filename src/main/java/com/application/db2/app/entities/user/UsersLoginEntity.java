package com.application.db2.app.entities.user;

import com.application.db2.app.dtos.users.UsersLoginDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "useri_utilizatori_rares", schema = "DEV_ADF_APPL")
public class UsersLoginEntity {
    @Id
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "USER_TYPE")
    private String userType;
    @Column(name = "EMAIL_CONTACT")
    private String emailContact;
    @Column(name = "VALIDATION")
    private BigDecimal validation;
    @Column(name = "VALABILITY")
    private Date valability;
    @Column(name = "APPROVER_EMAIL")
    private String approverEmail;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "LOCATION")
    private String location;

    public UsersLoginEntity(UsersLoginDto dto) {
        this.username = dto.getUsername();
        this.password = dto.getPassword();
        this.emailContact = dto.getEmailContact();
        this.validation = new BigDecimal(0);
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.location = dto.getLocation();

    }
}
