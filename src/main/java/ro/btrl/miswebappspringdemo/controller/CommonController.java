package ro.btrl.miswebappspringdemo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author Marius Pop
 * @since 02/June/2021
 */

@RestController
@RequestMapping("/api/common")
public class CommonController {

    @GetMapping("/user")
    private ResponseEntity getUser(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body("test succesful");
    }
}