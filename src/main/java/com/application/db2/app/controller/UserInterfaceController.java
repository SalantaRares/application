package com.application.db2.app.controller;

import com.application.db2.app.dtos.users.UsersLoginDto;
import com.application.db2.app.service.users.UsersService;
import com.application.utils.hibernate.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserInterfaceController {

    public final UsersService usersService;

    public UserInterfaceController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/get-user-acces-by-username-and-password")
    public ResponseEntity getUserLoginEntity(@RequestParam(name = "username") String username,
                                             @RequestParam(name = "password") String password) {
        return ResponseEntity.status(HttpStatus.OK).body(usersService.getUsersLoginEntity(username, password));
    }

    @PostMapping("/request-user")
    public ResponseEntity requestUser(@RequestBody UsersLoginDto dto){
        return ResponseEntity.status(HttpStatus.OK).body(usersService.requestUser(dto));
    }
}
