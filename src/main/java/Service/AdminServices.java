package Service;

import Controller.AdminController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AdminServices {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);


    //constructor
    public AdminServices() {

        System.out.println("AdminServices is loaded");
    }

    public String getAdminInfo() {
        logger.info("AdminServices.getAdminInfo is loaded");
        return "Access granted for admin.";
    }
}
