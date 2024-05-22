package Model;




import java.util.List;

public class UserAccessRequest {

    private Long userId;

    private List<String> endpoints;

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getEndpoint() {
        return endpoints;
    }

    public void setEndpoints(List<String> endpoint) {
        this.endpoints = endpoint;
    }
}
