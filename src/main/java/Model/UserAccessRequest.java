package Model;




import java.util.List;

public class UserAccessRequest {

    private Long userId;

    private List<String> endpoint;

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getEndpoint() {
        return endpoint;
    }

    public void setEndpoints(List<String> endpoint) {
        this.endpoint = endpoint;
    }
}
