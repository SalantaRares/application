package com.application.auth;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import java.util.Collection;

public class CustomLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations dirContextOperations, String s) {
        String authorities = "";
        String[] groups = dirContextOperations.getStringAttributes("memberOf");
        for (String c : groups) {
            authorities = authorities + "," + getAuthorityFromLdapGroup(c);
        }
        authorities = authorities + "," + dirContextOperations.getStringAttribute("samaccountname");

        return AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
    }

    /**
     * Extracts the value of CN
     *
     * @param ldapGroup example of ldapGroup: CN=MIS Users,OU=MIS,OU=Centrala-Cluj,DC=BT,DC=WAN
     * @return - the CN value
     */
    private String getAuthorityFromLdapGroup(String ldapGroup) {
        return ldapGroup.substring(ldapGroup.indexOf("=") + 1, ldapGroup.indexOf(','));
    }


}
