package oxchains.invoice.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static oxchains.invoice.rest.domain.RestResp.fail;

/**
 * @author aiet
 */
@Component
public class AuthError implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        handleAccessDenied(request, response, authException);
    }

    private static void handleAccessDenied(HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException, ServletException {
        response.setStatus(SC_FORBIDDEN);
        response.setContentType(APPLICATION_JSON_VALUE);

        String message = "authentication error: ";
        if (exception.getCause() != null) {
            message += exception
              .getCause()
              .getMessage();
        } else {
            message += exception.getMessage();
        }
        byte[] body = new ObjectMapper().writeValueAsBytes(fail(message));
        response
          .getOutputStream()
          .write(body);

    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        handleAccessDenied(request, response, accessDeniedException);
    }

}
