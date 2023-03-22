package com.application.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.application.auth.CustomLdapGroupSearch;

@RestController
@RequestMapping("/api/security")
public class SecurityTestController {

    @Autowired
    CustomLdapGroupSearch customLdapGroupSearch;

    @GetMapping("/groups")
    private ResponseEntity findGroupsAndSubgroups(@RequestParam(name = "groups") String... groups) {
        return ResponseEntity.status(HttpStatus.OK).body(customLdapGroupSearch.findGroupAndAllTreeMembers(groups));
    }

    @GetMapping("/user")
    private ResponseEntity isUser(@RequestParam(name = "username") String username) {
        return ResponseEntity.status(HttpStatus.OK).body(customLdapGroupSearch.isUser(username));
    }

}
