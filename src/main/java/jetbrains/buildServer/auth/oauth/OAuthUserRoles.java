package jetbrains.buildServer.auth.oauth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OAuthUserRoles {
    private final List<String> roles;

    public OAuthUserRoles(Map userData) {
        this.roles = (List<String>) userData.getOrDefault("roles", new ArrayList());
    }

    public List<String> getRoles() {
        return roles;
    }
}
