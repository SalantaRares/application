package ro.btrl.miswebappspringdemo.db2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.btrl.miswebappspringdemo.db2.service.Db2Service;

import java.security.Principal;

/**
 * @author Marius Pop
 * @since 02/June/2021
 */

@RestController
@RequestMapping("/api/db2")
public class Db2Controller {

    @Autowired
    Db2Service db2Service;

    @GetMapping("/bar")
    private ResponseEntity getUser(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(db2Service.getDb2Models());
    }
}