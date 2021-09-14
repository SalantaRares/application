package ro.btrl.miswebappspringdemo.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * This filter was implemented to catch MaxUploadSizeExceededException and then to handle it
 * The code is exactly like MultipartFilter except the try - catch  added
 */
@Component
public class CustomMultipartFilter extends OncePerRequestFilter {

    public static final String DEFAULT_MULTIPART_RESOLVER_BEAN_NAME = "filterMultipartResolver";

    private final MultipartResolver defaultMultipartResolver = new StandardServletMultipartResolver();

    private String multipartResolverBeanName = DEFAULT_MULTIPART_RESOLVER_BEAN_NAME;


    /**
     * Set the bean name of the MultipartResolver to fetch from Spring's
     * root application context. Default is "filterMultipartResolver".
     */
    public void setMultipartResolverBeanName(String multipartResolverBeanName) {
        this.multipartResolverBeanName = multipartResolverBeanName;
    }

    /**
     * Return the bean name of the MultipartResolver to fetch from Spring's
     * root application context.
     */
    protected String getMultipartResolverBeanName() {
        return this.multipartResolverBeanName;
    }


    /**
     * Check for a multipart request via this filter's MultipartResolver,
     * and wrap the original request with a MultipartHttpServletRequest if appropriate.
     * <p>All later elements in the filter chain, most importantly servlets, benefit
     * from proper parameter extraction in the multipart case, and are able to cast to
     * MultipartHttpServletRequest if they need to.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            MultipartResolver multipartResolver = lookupMultipartResolver(request);

            HttpServletRequest processedRequest = request;
            if (multipartResolver.isMultipart(processedRequest)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Resolving multipart request [" + processedRequest.getRequestURI() +
                            "] with MultipartFilter");
                }
                processedRequest = multipartResolver.resolveMultipart(processedRequest);
            } else {
                // A regular request...
                if (logger.isDebugEnabled()) {
                    logger.debug("Request [" + processedRequest.getRequestURI() + "] is not a multipart request");
                }
            }

            try {
                filterChain.doFilter(processedRequest, response);
            } finally {
                if (processedRequest instanceof MultipartHttpServletRequest) {
                    multipartResolver.cleanupMultipart((MultipartHttpServletRequest) processedRequest);
                }
            }
        } catch (MaxUploadSizeExceededException ex) {
            ExceptionMessage exceptionMessage = new ExceptionMessage("Cantitatea incarcata este prea mare! Limita maxima pentru un fisier este de 10MB!");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write(convertObjectToJson(exceptionMessage));
            response.addHeader("Content-Type", "application/json");
        }


    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    /**
     * Look up the MultipartResolver that this filter should use,
     * taking the current HTTP request as argument.
     * <p>The default implementation delegates to the {@code lookupMultipartResolver}
     * without arguments.
     *
     * @return the MultipartResolver to use
     * @see #lookupMultipartResolver()
     */
    protected MultipartResolver lookupMultipartResolver(HttpServletRequest request) {
        return lookupMultipartResolver();
    }

    /**
     * Look for a MultipartResolver bean in the root web application context.
     * Supports a "multipartResolverBeanName" filter init param; the default
     * bean name is "filterMultipartResolver".
     * <p>This can be overridden to use a custom MultipartResolver instance,
     * for example if not using a Spring web application context.
     *
     * @return the MultipartResolver instance
     */
    protected MultipartResolver lookupMultipartResolver() {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        String beanName = getMultipartResolverBeanName();
        if (wac != null && wac.containsBean(beanName)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Using MultipartResolver '" + beanName + "' for MultipartFilter");
            }
            return wac.getBean(beanName, MultipartResolver.class);
        } else {
            return this.defaultMultipartResolver;
        }
    }

}
