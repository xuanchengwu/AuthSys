package Controller;


import Model.CustomUserDetails;
import Service.UserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final UserServices userService;

    //Constructor
    @Autowired //Dependency Injection
    public UserController(@Qualifier("userServices") UserServices _userService) {
        this.userService = _userService;
        logger.info("UserController is loaded");
    }
    @GetMapping("/user/info")
    public ResponseEntity<String> userInfo() {
        return ResponseEntity.ok("User Accessing.");
    }

    @GetMapping("/user/{resource}")
    public ResponseEntity<String> checkUserAccess(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String resource) {
        boolean hasAccess = userService.checkUserAccess(userDetails, resource);
        logger.info("Checking user access for resource: " + resource);
        if (hasAccess) {
            return ResponseEntity.ok("Access granted to resource: " + resource);
        } else {
            return ResponseEntity.status(403).body("Access denied to resource: " + resource);
        }
    }


}
