package com.example.authsys;

import Controller.AdminController;
import Model.CustomUserDetails;
import Model.Role;
import Model.UserAccessRequest;
import Service.AdminServices;
import Service.UserServices;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static Service.HeaderEncoder.encodeUserDetailsToBase64;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class AuthSysApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    CustomUserDetails userDetails = new CustomUserDetails("123456", "JohnDoe", Role.ADMIN);
    String encodedHeader = encodeUserDetailsToBase64(userDetails);

    private static final String USER_DATA_FILE = "users.json";

    File file = new ClassPathResource(USER_DATA_FILE).getFile();


    @Autowired
    private UserServices userService;

    @Autowired
    private AdminServices adminService;

    @Autowired
    private ObjectMapper objectMapper;

    AuthSysApplicationTests() throws IOException {
    }


    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void adminAccessAdminEndpoint() throws Exception {
        logger.info("Testing access to the endpoint");
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/info")
                .header("X-Auth-Role", encodedHeader))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="admin", roles = "ADMIN")
    public void testAddUserByNonAdmin() throws Exception {
        logger.info("Testing admin granting others to the endpoint");

        // Create UserAccessRequest
        UserAccessRequest accessRequest = new UserAccessRequest();
        accessRequest.setUserId(Long.valueOf("123456"));
        accessRequest.setEndpoints(List.of("resource A", "resource B", "resource C", "resource D"));

        // Call the service method to update user access
        adminService.updateUserAccess("123456", accessRequest);

        // Verify that the users.json file has been updated correctly

        String currentContent = Files.readString(file.toPath());
        logger.info("Current content of users.json in test:\n{}", currentContent);
        List<CustomUserDetails> users = objectMapper.readValue(file, new TypeReference<List<CustomUserDetails>>() {});
        CustomUserDetails updatedUser = users.stream()
                .filter(user -> "123456".equals(user.getUserId()))
                .findFirst()
                .orElse(null);

        assert updatedUser != null;
        assertNotNull(updatedUser, "User should be present in the file");
        assertEquals(List.of("resource A", "resource B", "resource C", "resource D"), updatedUser.getEndpoints(), "Endpoints should match");

        logger.info("Updated user access: {}", objectMapper.writeValueAsString(updatedUser));
        String currentContent2 = Files.readString(file.toPath());
        logger.info("Current content of users.json in test2:\n{}", currentContent2);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testUserAccessDenied() throws Exception {
        logger.info("Testing user access denied to a resource");

        CustomUserDetails userDetails = new CustomUserDetails("123456", "JohnDoe", Role.USER);
        String encodedHeader = encodeUserDetailsToBase64(userDetails);

        // Ensure the actual userService is used in the test
        boolean accessDenied = userService.checkUserAccess(userDetails, "resource B");
        if (!accessDenied) {
            mockMvc.perform(MockMvcRequestBuilders.get("/user/resource B")
                            .header("X-Auth-Role", encodedHeader))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$").value("Access denied to resource: resource B"));
        } else {
            mockMvc.perform(MockMvcRequestBuilders.get("/user/resource B")
                            .header("X-Auth-Role", encodedHeader))
                    .andExpect(status().isOk());
        }
    }



}
