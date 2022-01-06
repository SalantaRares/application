package ro.btrl.miswebappspringdemo.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;

import javax.naming.directory.SearchControls;
import java.text.MessageFormat;

/**
 * original Class is FilterBasedLdapUserSearch
 * method searchForUser needed changes
 */
public class CustomLdapUserSearch implements LdapUserSearch {

    private static final Log logger = LogFactory.getLog(FilterBasedLdapUserSearch.class);
    private final ContextSource contextSource;
    private final SearchControls searchControls = new SearchControls();
    private String searchBase = "";
    private final String searchFilter;

    public CustomLdapUserSearch(String searchBase, String searchFilter, BaseLdapPathContextSource contextSource) {
        this.searchFilter = searchFilter;
        this.contextSource = contextSource;
        this.searchBase = searchBase;
        this.setSearchSubtree(true);
    }


    /**
     * the username needed to be changed
     * the method parameter come with username and domain
     * ex: username= denisa.cremine@btrl.ro and  the search filter needed denisa.cremine
     *
     * @param username - context username (ex: denisa.cremine@btrl.ro)
     * @return - context user data
     */
    public DirContextOperations searchForUser(String username) {
        if (logger.isDebugEnabled()) {
            logger.debug("Searching for user '" + username + "', with user search " + this);
        }

        SpringSecurityLdapTemplate template = new SpringSecurityLdapTemplate(this.contextSource);
        template.setSearchControls(this.searchControls);

        try {
            return template.searchForSingleEntry(this.searchBase, MessageFormat.format(searchFilter, username.substring(0, username.indexOf('@'))), new String[]{username});

        } catch (IncorrectResultSizeDataAccessException var4) {
            if (var4.getActualSize() == 0) {
                throw new UsernameNotFoundException("User " + username + " not found in directory.");
            } else {
                throw var4;
            }
        }
    }

    public void setDerefLinkFlag(boolean deref) {
        this.searchControls.setDerefLinkFlag(deref);
    }

    public void setSearchSubtree(boolean searchSubtree) {
        this.searchControls.setSearchScope(searchSubtree ? 2 : 1);
    }

    public void setSearchTimeLimit(int searchTimeLimit) {
        this.searchControls.setTimeLimit(searchTimeLimit);
    }

    public void setReturningAttributes(String[] attrs) {
        this.searchControls.setReturningAttributes(attrs);
    }


}
