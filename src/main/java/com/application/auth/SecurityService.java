package com.application.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Service("securityService")
public class SecurityService {

    @Autowired
    CustomLdapGroupSearch customLdapGroupSearch;

    @Transactional
    public boolean hasPermission(String applicationPart) {
        List<String> allowedGroups = customLdapGroupSearch.findGroupAndAllTreeMembers(SecurityMapping.getValues(applicationPart));
        String[] userAuthorities = getUserAuthorities();
        for (String userAuthority : userAuthorities) {
            if (allowedGroups.contains(userAuthority)) return true;
        }
        return false;
    }

    public String[] getUserAuthorities() {
        String authoritiesAsString = "";
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        while (iterator.hasNext()) {
            authoritiesAsString += "," + iterator.next();
        }
        return authoritiesAsString.split(",");
    }
}