import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebFilter;

/**
 * Authentication filter to protect student pages
 */
@WebFilter("/students/*")
public class AuthenticationFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        // Check if user is logged in
        boolean isLoggedIn = (session != null && session.getAttribute("studentID") != null);
        
        // Check if it's a login request
        String loginURI = httpRequest.getContextPath() + "/login";
        boolean isLoginRequest = httpRequest.getRequestURI().equals(loginURI);
        
        // Check if it's a resource request (CSS, JS, images)
        String requestURI = httpRequest.getRequestURI();
        boolean isResourceRequest = requestURI.endsWith(".css") || 
                                   requestURI.endsWith(".js") || 
                                   requestURI.endsWith(".png") || 
                                   requestURI.endsWith(".jpg") || 
                                   requestURI.endsWith(".gif") || 
                                   requestURI.endsWith(".ico") ||
                                   requestURI.contains("/lib/");
        
        if (isLoggedIn || isLoginRequest || isResourceRequest) {
            // Allow the request to proceed
            chain.doFilter(request, response);
        } else {
            // Redirect to login page
            httpResponse.sendRedirect(loginURI);
        }
    }
    
    @Override
    public void destroy() {
        // Cleanup if needed
    }
}