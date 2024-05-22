package Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CustomUserDetails {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("accountName")
    private String accountName;
    @JsonProperty("role")
    private Role role;

    @JsonProperty("endpoints")
    private List<String> endpoints;

    public CustomUserDetails() {
    }

    public CustomUserDetails(String userId, String accountName, Role role) {
        this.userId = userId;
        this.accountName = accountName;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // set resource access for user


    public List<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }

    @Override
    public String toString() {
        return "  CustomUserDetails { " +
                "userId='" + userId + '\'' +
                ", accountName='" + accountName + '\'' +
                ", role=" + role +
                '}';
    }


}

