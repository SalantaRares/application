package ro.btrl.miswebappspringdemo.db1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.btrl.miswebappspringdemo.db1.dao.Db1Dao;

import java.security.Principal;

/**
 * @author Marius Pop
 * @since 02/June/2021
 */

@RestController
@RequestMapping("/api/db1")
public class Db1Controller {

    @Autowired
    Db1Dao db1Dao;

    @GetMapping("/abc")
    private ResponseEntity getUser(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(db1Dao.getDb1Models());
    }
}