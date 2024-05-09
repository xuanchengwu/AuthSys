package Service;

import Model.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HeaderEncoder {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String encodeUserDetailsToBase64(CustomUserDetails userDetails) {
        try {
            String json = objectMapper.writeValueAsString(userDetails);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode user details", e);
        }
    }
}
