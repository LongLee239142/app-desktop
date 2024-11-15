package atlantafx.sampler.base.entity.admin;

public class Admin {
    private String username;
    private String email;

    public Admin(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}