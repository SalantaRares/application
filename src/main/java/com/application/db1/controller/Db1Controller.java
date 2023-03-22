package com.application.db1.controller;

import com.application.db1.service.Db1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@RequestMapping("/api/db1")
public class Db1Controller {

    @Autowired
    Db1Service db1Service;

    @GetMapping("/abc")
    private ResponseEntity getUser(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(db1Service.getDb1Models());
    }
}