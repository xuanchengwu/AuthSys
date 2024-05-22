package Service;

import Controller.AdminController;
import Model.CustomUserDetails;
import Model.UserAccessRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminServices {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final Map<String, CustomUserDetails> userDetailsMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private static final String USER_DATA_FILE = "users.json";

    File file = new ClassPathResource(USER_DATA_FILE).getFile();

    //constructor
    public AdminServices(ObjectMapper objectMapper) throws IOException {
        this.objectMapper = objectMapper;
        String currentContent = Files.readString(file.toPath());
        logger.info("Current content of users.json before update in constructor:\n{}", currentContent);
        logger.info("AdminServices constructor is called");
    }



    @PostConstruct
    public void initializeUserDetailsMap() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(USER_DATA_FILE);
            if (inputStream != null) {
                List<CustomUserDetails> users = objectMapper.readValue(inputStream, new TypeReference<>() {});
                for (CustomUserDetails user : users) {
                    userDetailsMap.put(user.getUserId(), user);
                }
                logger.info("User data loaded successfully: {}", userDetailsMap);
            } else {
                logger.warn("User data file does not exist: {}", USER_DATA_FILE);
            }
        } catch (IOException e) {
            logger.error("Error loading user data", e);
        }
    }



    public void addUserAccess(@NotNull UserAccessRequest request) throws IOException {
        logger.info("AdminServices.addUserAccess is loaded");
        logger.info("Received UserAccessRequest: userId={}, endpoint={}", request.getUserId(), request.getEndpoint());

        CustomUserDetails userDetails = userDetailsMap.get(request.getUserId().toString());
        if (userDetails != null) {
            userDetails.setEndpoints(request.getEndpoint());
            saveUserData();
            logger.info("Access granted for user ID {} to resources: {}", request.getUserId(), request.getEndpoint());
        } else {
            // Handle the case where the user is not found


            logger.warn("User ID {} not found", request.getUserId());
            return;
        }
    }

    public void updateUserAccess(String userId, UserAccessRequest accessRequest) throws IOException {
        CustomUserDetails user = userDetailsMap.get(userId);
        if (user != null) {
            user.setEndpoints(accessRequest.getEndpoint());
            userDetailsMap.put(userId, user);
            saveUserData();
        } else {
            // Handle the case where the user is not found
            logger.warn("User not found: {}", userId);
        }
    }


    private void saveUserData() throws IOException {
        try {

            String currentContent1 = Files.readString(file.toPath());
            logger.info("Current content of users.json before update:\n{}", currentContent1);

            Path filePath = Paths.get(file.getAbsolutePath());
            Path tempFilePath = Paths.get(file.getParent(), "temp_users.json");


            //check if file is writable
            if (!file.canWrite()) {
                logger.error("File is not writable: {}", file.getAbsolutePath());
                return;
            }



            Collection<CustomUserDetails> users = userDetailsMap.values();
            ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
            String jsonString = writer.writeValueAsString(users);
            logger.info("Writing the following data to temp_users.json:\n{}", jsonString);

            // Write to a temporary file first
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(tempFilePath)) {
                bufferedWriter.write(jsonString);
                bufferedWriter.flush();
                logger.info("Successfully wrote to temp_users.json");
            } catch (IOException e) {
                logger.error("Error writing to temp_users.json", e);
                return;
            }

            // Replace the original file with the temporary file
            try {
                Files.move(tempFilePath, filePath, StandardCopyOption.REPLACE_EXISTING);
                logger.info("Successfully replaced the original users.json with the new data");
            } catch (IOException e) {
                logger.error("Error replacing the original users.json with the new data", e);
            }

            String currentContent = Files.readString(filePath);
            logger.info("Current content of users.json after update:\n{}", currentContent);

        } catch (IOException e) {
            // Handle the case where the file cannot be written
            logger.error("Error saving users.json", e);
            return;
        }

    }


}
