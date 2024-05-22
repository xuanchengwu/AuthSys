package Controller;


import Model.CustomUserDetails;
import Model.UserAccessRequest;
import Service.AdminServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);


    private final AdminServices adminService;
    @Autowired
    public AdminController(@Qualifier("adminServices") AdminServices _adminService) {

        this.adminService = _adminService;
        logger.info("AdminController is loaded");
    }


    @GetMapping("/admin/info")
    public ResponseEntity<String> adminInfo() {
        logger.info("Accessing admin info endpoint");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return ResponseEntity.ok("Admin Accessing + " + userDetails.toString());
        } else {
            return ResponseEntity.ok("User Accessing." + userDetails.toString());
        }
    }

    private String extractUserDetails(Authentication authentication) {
        // Assuming the principal is an instance of UserDetails
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // Construct a string with user details
            return String.format("Username: %s, Authorities: %s",
                    userDetails.getUsername(),
                    userDetails.getAuthorities().toString());
        } else {
            return authentication.getPrincipal().toString(); // Fallback if custom user details are used
        }
    }

    @PostMapping("/admin/addUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserAccessRequest> addUser(@RequestBody UserAccessRequest request) throws IOException {
        // Logic to add access to resources for the user
        // For demonstration, assume the service call is successful
        logger.info("Adding User Access to resources: " + request.getEndpoint() + " for user ID " + request.getUserId());
        adminService.addUserAccess(request);
        return ResponseEntity.ok(request);
    }

}
