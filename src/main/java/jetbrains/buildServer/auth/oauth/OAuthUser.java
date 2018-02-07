package jetbrains.buildServer.auth.oauth;

import java.util.*;

public class OAuthUser {
    private static final String[] IDS_LIST = {"login", "username", "id"};
    private static final String[] NAMES_LIST = {"name", "display_name"};
    private static final String[] EMAIL_LIST = {"email"};

    private final String id;
    private final String name;
    private final String email;
    private List<String> roles = new ArrayList<>();

    public OAuthUser(String id) {
        this(id, null, null);
    }

    public OAuthUser(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public OAuthUser(Map userData) {
        id = getValueByKeys(userData, IDS_LIST);
        name = getValueByKeys(userData, NAMES_LIST);
        email = getValueByKeys(userData, EMAIL_LIST);
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
        return Optional.ofNullable(id).orElse(email);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void addRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "OAuthUser{" + "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", roles='" + getRoles() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OAuthUser oAuthUser = (OAuthUser) o;
        return Objects.equals(id, oAuthUser.id) &&
                Objects.equals(name, oAuthUser.name) &&
                Objects.equals(roles, oAuthUser.roles) &&
                Objects.equals(email, oAuthUser.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, roles);
    }
}
