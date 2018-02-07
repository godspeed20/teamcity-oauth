package jetbrains.buildServer.auth.oauth;

import java.util.*;

public class OAuthUserRoles {
    private static final String[] IDS_LIST = new String[]{"login", "username", "id"};

    private final String id;
    private final List<String> roles;

    public OAuthUserRoles(String id) {
        this(id, new ArrayList());
    }

    public OAuthUserRoles(String id, List<String> roles) {
        this.id = id;
        this.roles = roles;
    }

    public OAuthUserRoles(Map userData) {
        this.id = getValueByKeys(userData, IDS_LIST);
        this.roles = (List<String>) userData.getOrDefault("roles", new ArrayList());
    }

    private String getValueByKeys(Map userData, String[] keys) {
        if (userData == null)
            return null;
        String value = null;
        for (String key : keys) {
            value = (String) userData.get(key);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    public String getId() {
        return Optional.of(id).get();
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "OAuthUserRoles{" +
                "id='" + id + '\'' +
                ", roles=" + roles +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OAuthUserRoles that = (OAuthUserRoles) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roles);
    }
}
