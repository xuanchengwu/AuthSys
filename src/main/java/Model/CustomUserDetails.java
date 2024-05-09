package Model;

public class CustomUserDetails {
    private String userId;
    private String accountName;
    private Role role;

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

    @Override
    public String toString() {
        return "  CustomUserDetails { " +
                "userId='" + userId + '\'' +
                ", accountName='" + accountName + '\'' +
                ", role=" + role +
                '}';
    }
}

