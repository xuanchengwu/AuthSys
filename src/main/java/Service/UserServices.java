package Service;


import Controller.UserController;
import Model.CustomUserDetails;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServices {

    // Simulate the resource access list for users
    private final Map<String, List<String>> userResourceAccessMap = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    //Constructor
    public UserServices() {
        logger.info("UserServices constructor is called");
    }



    public void addUserAccess(String userId, List<String> resources) {
        userResourceAccessMap.put(userId, resources);
    }

    public boolean checkUserAccess(@NotNull CustomUserDetails userDetails, String resource) {
        logger.info("Checking user access for resource in UserServices: " + resource);
        List<String> accessibleResources = userResourceAccessMap.get(userDetails.getUserId());
        return accessibleResources != null && accessibleResources.contains(resource);
    }

    // Method to add access to resources (called by AdminService)

}
