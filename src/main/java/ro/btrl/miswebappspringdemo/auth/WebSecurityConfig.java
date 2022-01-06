package ro.btrl.miswebappspringdemo.auth;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.client.config.SunJaasKrb5LoginConfig;
import org.springframework.security.kerberos.client.ldap.KerberosLdapContextSource;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import ro.btrl.miswebappspringdemo.config.AppConfig;
import ro.btrl.miswebappspringdemo.exceptions.CustomDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    String AD_SERVER = "ldap://ads01cj001.bt.wan:3268";
    String LDAP_SEARCH_BASE = "DC=BT,DC=WAN";
    String ldapSearchFilter = "(&(objectCategory=person)(objectClass=user)(sAMAccountName={0}))";

    String KEY_TAB_NAME;
    String KEY_TAB_LOCATION;

    WebSecurityConfig() {
        AppConfig.Parameters parameters = AppConfig.getInstance().getParameters();
        KEY_TAB_NAME = parameters.getKeyTabName();
        KEY_TAB_LOCATION = parameters.getKeyTabLocation();
    }


    /**
     * API access configurations
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .requireCsrfProtectionMatcher(new RequestMatcher() {
                    private Pattern allowedMethods = Pattern.compile("^(GET|POST|PUT|DELETE)$");
                    private RegexRequestMatcher apiMatcher = new RegexRequestMatcher("/.*[A-z]*/.*", null);

                    @Override
                    public boolean matches(HttpServletRequest request) {
                        // No CSRF due to allowedMethod
                        if (allowedMethods.matcher(request.getMethod()).matches()) {
                            return false;
                        }

                        // No CSRF due to api call
                        if (apiMatcher.matches(request)) {
                            return false;
                        }

                        // CSRF for everything else that is not an API call or an allowedMethod
                        return true;
                    }
                })
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(spnegoEntryPoint()) //initiate negotiations - sends the negotiating header to browser
                .and().exceptionHandling().accessDeniedHandler(new CustomDeniedHandler()) // if access to a resource is denied
                .and()
                .authorizeRequests()
                .antMatchers("/**").hasAnyAuthority(String.join(",", customLdapGroupSearch().findGroupAndAllTreeMembers(SecurityMapping.getAllValues())))
                .and()
                .addFilterBefore(spnegoAuthenticationProcessingFilter(authenticationManagerBean()), BasicAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(kerberosServiceAuthenticationProvider());
    }

    /**
     * @return SpnegoEntryPoint -Sends the negotiation request to the browser
     */
    @Bean
    public SpnegoEntryPoint spnegoEntryPoint() {
        return new SpnegoEntryPoint();
    }


    /**
     * @param authenticationManager - Attempts to authenticate the passed
     *                              Authentication object, returning a fully
     *                              populated Authentication object
     *                              (including granted authorities) if successful.
     * @return SpnegoAuthenticationProcessingFilter
     * - parses the Header sent by browser (the token)
     * - creates KerberosServiceRequestToken - Holds the
     * Kerberos/SPNEGO token for requesting a kerberized service
     */
    @Bean
    public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter(
            AuthenticationManager authenticationManager) {
        SpnegoAuthenticationProcessingFilter filter =
                new SpnegoAuthenticationProcessingFilter();
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    /**
     * @return KerberosServiceAuthenticationProvider - validates Kerberos Service Tickets or SPNEGO Tokens
     */
    @Bean
    public KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider() {
        KerberosServiceAuthenticationProvider provider =
                new KerberosServiceAuthenticationProvider();
        provider.setTicketValidator(sunJaasKerberosTicketValidator());
        provider.setUserDetailsService(ldapUserDetailsService());
        return provider;
    }

    @Bean
    public SunJaasKerberosTicketValidator sunJaasKerberosTicketValidator() {
        SunJaasKerberosTicketValidator ticketValidator = new SunJaasKerberosTicketValidator();
        ticketValidator.setServicePrincipal(KEY_TAB_NAME);
        FileSystemResource fs = new FileSystemResource(KEY_TAB_LOCATION); //Path to file .keytab
        Assert.notNull(fs.exists(), "*.keytab key must exist. Without that security is useless.");
        ticketValidator.setKeyTabLocation(new FileSystemResource(KEY_TAB_LOCATION));
        ticketValidator.setDebug(false);
        //ticketValidator.setDebug(true);
        return ticketValidator;
    }

    @Bean
    public KerberosLdapContextSource kerberosLdapContextSource() {
        KerberosLdapContextSource contextSource = new KerberosLdapContextSource(AD_SERVER);
        SunJaasKrb5LoginConfig loginConfig = new SunJaasKrb5LoginConfig();
        FileSystemResource f = new FileSystemResource(KEY_TAB_LOCATION);
        loginConfig.setKeyTabLocation(f);
        loginConfig.setServicePrincipal(KEY_TAB_NAME);
        //loginConfig.setDebug(true);
        loginConfig.setDebug(false);
        loginConfig.setIsInitiator(true);

        try {

            loginConfig.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        contextSource.setLoginConfig(loginConfig);
        return contextSource;
    }

    @Bean
    public LdapUserDetailsService ldapUserDetailsService() {
        CustomLdapUserSearch userSearch =
                new CustomLdapUserSearch(LDAP_SEARCH_BASE, ldapSearchFilter, kerberosLdapContextSource());

        LdapUserDetailsService service = new LdapUserDetailsService(userSearch, new CustomLdapAuthoritiesPopulator());
        service.setUserDetailsMapper(new LdapUserDetailsMapper());
        return service;
    }

    @Bean
    public SpringSecurityLdapTemplate springSecurityLdapTemplate() {
        return new SpringSecurityLdapTemplate(kerberosLdapContextSource());
    }

    @Bean
    public CustomLdapGroupSearch customLdapGroupSearch() {
        return new CustomLdapGroupSearch(springSecurityLdapTemplate());
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }
}