package Controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    //Constructor
    public UserController() {
        logger.info("UserController is loaded");
    }
    @GetMapping("/user/info")
    public ResponseEntity<String> userInfo() {
        return ResponseEntity.ok("User Accessing.");
    }
}
