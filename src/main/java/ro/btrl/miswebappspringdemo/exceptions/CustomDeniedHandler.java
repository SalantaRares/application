package ro.btrl.miswebappspringdemo.exceptions;



import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * this class is used for handle response for access denied to a resource
 */
public class CustomDeniedHandler implements AccessDeniedHandler {

    private String errorPage;

    public CustomDeniedHandler() {
    }

    public CustomDeniedHandler(String errorPage) {
        this.errorPage = errorPage;
    }

    public String getErrorPage() {
        return errorPage;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException{
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().print("Accesul la aceasta resursa este interzis!");
    }


}