package com.application.auth;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.SearchScope;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.*;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class CustomLdapGroupSearch {

    private static final Integer THREE_SECONDS = 3000;
    private String ldapSearchBase = "DC=BT,DC=WAN";

    private Set<String> groups;
    private Set<String> alreadySearchedGroups;

    private SpringSecurityLdapTemplate template;

    private Map<String[], List<String>> securityMap;

    public CustomLdapGroupSearch(SpringSecurityLdapTemplate template) {
        this.template = template;
        securityMap = new HashMap<>();
    }


    public List<String> findGroupAndAllTreeMembers(String... groups) {
        if (!this.securityMap.containsKey(groups)) {
            this.groups = new HashSet<>();
            this.alreadySearchedGroups = new HashSet<>();
            for (String group : groups) {
                if (isUser(group)) {
                    this.groups.add(group);
                } else {
                    findGroupAndMembers(group);
                }
            }
            this.securityMap.put(groups, new ArrayList(Arrays.asList(this.groups.toArray())));
        }
        return this.securityMap.get(groups);

    }

    private void findGroupAndMembers(String groupName) {
        if (!this.alreadySearchedGroups.contains(groupName)) {
            final String distinguishedName = getDistinguishedName(groupName);
            LdapQuery query = query()
                    .searchScope(SearchScope.SUBTREE)
                    .timeLimit(THREE_SECONDS)
                    .base(ldapSearchBase)
                    .filter("(&(objectCategory=group)(memberOf=" + distinguishedName + "))");

            List<String> searchResultList = template.search(query, new SimpleAttributesMapper("samaccountname")); //return samAccountName for AD entries where searched groupName is set on memberOf attribute

            if (distinguishedName != null) {
                this.groups.add(getContentFromEntry(distinguishedName)); //add to list the searched group if it is valid
                this.alreadySearchedGroups.add(groupName);
            }

            this.groups.addAll(searchResultList); //add to list the subgroups
            searchResultList.stream().forEach(item -> findGroupAndMembers(item)); //find subgroups
        }
    }

    private String getDistinguishedName(String name) {
        LdapQuery query = query()
                .searchScope(SearchScope.SUBTREE)
                .timeLimit(THREE_SECONDS)
                .base(ldapSearchBase)
                .filter("(&(objectCategory=group)(samaccountname=" + name + "))");
        final List<String> distinguishedname = template.search(query, new SimpleAttributesMapper("distinguishedname"));
        if (distinguishedname != null && !distinguishedname.isEmpty()) {
            return distinguishedname.get(0);
        }
        return null;
    }

    public boolean isUser(String name) {
        LdapQuery query = query()
                .searchScope(SearchScope.SUBTREE)
                .timeLimit(THREE_SECONDS)
                .base(ldapSearchBase)
                .filter("(&(objectClass=user)(objectCategory=person)(samaccountname=" + name + "))");
        final List<String> samaccountnameList = template.search(query, new SimpleAttributesMapper("samaccountname"));
        if (samaccountnameList != null && !samaccountnameList.isEmpty()) {
            return true;
        }
        return false;
    }

    private class SimpleAttributesMapper implements AttributesMapper<String> {

        String attribute;

        SimpleAttributesMapper(String attribute) {
            this.attribute = attribute;
        }

        public String mapFromAttributes(Attributes attrs) throws NamingException {
            Attribute _attribute = attrs.get(this.attribute);
            if (_attribute != null) {
                return (String) _attribute.get();
            }
            return null;
        }
    }

    private String getContentFromEntry(String entry) {
        return entry.substring(entry.indexOf("=") + 1, entry.indexOf(','));
    }
}
