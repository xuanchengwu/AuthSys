package com.example.authsys;

import Controller.AdminController;
import Model.CustomUserDetails;
import Model.Role;
import Service.AdminServices;
import config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static Service.HeaderEncoder.encodeUserDetailsToBase64;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AuthSysApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    CustomUserDetails userDetails = new CustomUserDetails("123456", "JohnDoe", Role.ADMIN);
    String encodedHeader = encodeUserDetailsToBase64(userDetails);



    @MockBean
    private AdminServices adminService;

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
        String jsonContent = "{\"userId\":123456,\"endpoint\":[\"resource A\",\"resource B\",\"resource C\"]}";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/addUser")
                        .contentType(APPLICATION_JSON)
                        .content(jsonContent)
                        .with(csrf()))
                .andExpect(status().isOk());
    }


}
