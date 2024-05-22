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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminServices {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final Map<String, CustomUserDetails> userDetailsMap = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();
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
                List<CustomUserDetails> users = objectMapper.readValue(inputStream, new TypeReference<List<CustomUserDetails>>() {});
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

    public String getAdminInfo() {
        logger.info("AdminServices.getAdminInfo is loaded");
        return "Access granted for admin.";
    }

    public String addUser() throws IOException {
        logger.info("AdminServices.addUser is loaded");

        return "User added.";
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
            logger.warn("User ID {} not found", request.getUserId());
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


            //check if file is writable
            if (!file.canWrite()) {
                logger.error("File is not writable: {}", file.getAbsolutePath());
                return;
            }



            String testString = "This is a test write operation to the file.";
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(testString);
                logger.info("Test write successful. Written data: {}", testString);
                String currentContent2 = Files.readString(file.toPath());
                logger.info("Current content of users.json after teststring:\n{}", currentContent2);
            }



            Collection<CustomUserDetails> users = userDetailsMap.values();

            // Log the values being written to the file
            ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
            String jsonString = writer.writeValueAsString(users);
            logger.info("Writing the following data to users.json:\n{}", jsonString);

            objectMapper.writeValue(file, users);
            logger.info("Successfully saved users.json");
            String currentContent3 = Files.readString(file.toPath());
            logger.info("Current content of users.json before update2:\n{}", currentContent3);
        } catch (IOException e) {
            logger.error("Error saving users.json", e);
        }

    }

//    private void printLoadedUserData() {
//        userDetailsMap.forEach((userId, userDetails) -> {
//            logger.info("Loaded User - ID: {}, AccountName: {}, Role: {}, Resources: {}",
//                    userId, userDetails.getAccountName(), userDetails.getRole(), userDetails.getEndpoints());
//        });
//    }
}
