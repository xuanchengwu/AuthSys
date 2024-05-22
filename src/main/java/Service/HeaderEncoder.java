package Service;

import Controller.AdminController;
import Model.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HeaderEncoder {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);


    public static String encodeUserDetailsToBase64(CustomUserDetails userDetails) {
        try {
            logger.info("Encoding user details to base64");
            String json = objectMapper.writeValueAsString(userDetails);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode user details", e);
        }
    }
}
