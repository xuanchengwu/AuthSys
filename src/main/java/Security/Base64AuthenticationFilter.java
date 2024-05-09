package Security;

import Model.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

public class Base64AuthenticationFilter extends OncePerRequestFilter{
    private static final Logger logger = LoggerFactory.getLogger(Base64AuthenticationFilter.class);


    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("X-Auth-Role");
        if (header != null && !header.isEmpty()) {
            try {
                logger.warn("Entering X-Auth-Role header");
                // Decode the Base64 encoded JSON string
                logger.warn("Received X-Auth-Role header: {}", header);
                byte[] decodedBytes = Base64.getDecoder().decode(header);
                String decodedJson = new String(decodedBytes, StandardCharsets.UTF_8);
                logger.warn("Decoded JSON: {}", decodedJson);

                // Deserialize the JSON to CustomUserDetails object
                CustomUserDetails customUserDetails = objectMapper.readValue(decodedJson, CustomUserDetails.class);
                logger.warn("Decoded UserDetails: userId={}, accountName={}, role={}",
                        customUserDetails.getUserId(),
                        customUserDetails.getAccountName(),
                        customUserDetails.getRole());

                // Create authentication token
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        customUserDetails, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + customUserDetails.getRole().name()))
                );

                // Set the authentication in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (IllegalArgumentException | IOException e) {
                // Log the exception and set the response to unauthorized
                logger.error("Error processing X-Auth-Role header", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return; // Stop further filter execution because of the error
            }
        }else {
            // Log the missing header and set the response to unauthorized
            logger.warn("Missing X-Auth-Role header");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return; // Stop further filter execution because of the error
        }

        filterChain.doFilter(request, response); // Continue the filter chain

    }
}
