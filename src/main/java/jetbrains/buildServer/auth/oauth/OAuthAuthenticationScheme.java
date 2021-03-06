package jetbrains.buildServer.auth.oauth;

import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationResult;
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationSchemeAdapter;
import jetbrains.buildServer.controllers.interceptors.auth.util.HttpAuthUtil;
import jetbrains.buildServer.groups.SUserGroup;
import jetbrains.buildServer.groups.UserGroupManager;
import jetbrains.buildServer.serverSide.auth.AuthModuleUtil;
import jetbrains.buildServer.serverSide.auth.ServerPrincipal;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class OAuthAuthenticationScheme extends HttpAuthenticationSchemeAdapter {

    private static final Logger LOG = Logger.getLogger(OAuthAuthenticationScheme.class);
    private static final boolean DEFAULT_ALLOW_CREATING_NEW_USERS_BY_LOGIN = true;
    public static final String CODE = "code";
    public static final String STATE = "state";

    private final PluginDescriptor pluginDescriptor;
    private final UserFactory principalFactory;
    private final UserGroupManager userGroupManager;
    private final OAuthClient authClient;

    public OAuthAuthenticationScheme(@NotNull final PluginDescriptor pluginDescriptor,
                                     @NotNull final UserFactory principalFactory,
                                     @NotNull final UserGroupManager userGroupManager,
                                     @NotNull final OAuthClient authClient) {
        this.pluginDescriptor = pluginDescriptor;
        this.principalFactory = principalFactory;
        this.userGroupManager = userGroupManager;
        this.authClient = authClient;
    }

    @NotNull
    @Override
    protected String doGetName() {
        return PluginConstants.OAUTH_AUTH_SCHEME_NAME;
    }

    @NotNull
    @Override
    public String getDescription() {
        return PluginConstants.OAUTH_AUTH_SCHEME_DESCRIPTION;
    }

    @Nullable
    @Override
    public String getEditPropertiesJspFilePath() {
        return pluginDescriptor.getPluginResourcesPath(PluginConstants.Web.EDIT_SCHEME_PAGE);
    }

    @Nullable
    @Override
    public Collection<String> validate(@NotNull Map<String, String> properties) {
        final Collection<String> errors = new ConfigurationValidator().validate(properties);
        return errors.isEmpty() ? super.validate(properties) : errors;
    }

    @NotNull
    @Override
    public HttpAuthenticationResult processAuthenticationRequest(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Map<String, String> schemeProperties) throws IOException {
        String code = request.getParameter(CODE);
        String state = request.getParameter(STATE);

        if (StringUtil.isEmpty(code) || StringUtil.isEmpty(state))
            return HttpAuthenticationResult.notApplicable();

        LOG.debug(String.format("oAuth response with code '%s' & state '%s'", code, state));

        if (!state.equals(SessionUtil.getSessionId(request)))
            return sendUnauthorizedRequest(request, response, "Unauthenticated since retrieved 'state' doesn't correspond to current TeamCity session.");

        String token = authClient.getAccessToken(code);
        if (token == null) {
            return sendUnauthorizedRequest(request, response, String.format("Unauthenticated since failed to fetch token for code '%s' and state '%s'.", code, state));
        }

        OAuthUser user = authClient.getUserData(token);
        if (user.getId() == null) {
            return sendUnauthorizedRequest(request, response, "Unauthenticated since user endpoint does not return any login id");
        }

        boolean allowCreatingNewUsersByLogin = AuthModuleUtil.allowCreatingNewUsersByLogin(schemeProperties, DEFAULT_ALLOW_CREATING_NEW_USERS_BY_LOGIN);
        final Optional<SUser> tcUser = principalFactory.getUser(user, allowCreatingNewUsersByLogin);

        if (tcUser.isPresent()) {
            LOG.debug("Request authenticated. Determined user " + tcUser.get().getName());

            Optional<OAuthUserRoles> userRoles = authClient.getUserRoles(user);
            userRoles.ifPresent(roles -> {
                        roles.getRoles().forEach(v -> {
                            SUserGroup userGroupByKey = userGroupManager.findUserGroupByKey(v);
                            if (userGroupByKey == null) userGroupByKey = userGroupManager.createUserGroup(v, v, v);
                            userGroupByKey.addUser(tcUser.get());
                        });
                        tcUser.get().getUserGroups().forEach(userGroup -> {
                            if (roles.getRoles().contains(userGroup.getKey())) return;
                            userGroupManager.findUserGroupByKey(userGroup.getKey()).removeUser(tcUser.get());
                        });
                    }
            );

            ServerPrincipal serverPrincipal = new ServerPrincipal(PluginConstants.OAUTH_AUTH_SCHEME_NAME, tcUser.get().getUsername());
            return HttpAuthenticationResult.authenticated(serverPrincipal, true)
                    .withRedirect("/");
        } else {
            return sendUnauthorizedRequest(request, response, "Unauthenticated since user could not be found or created.");
        }
    }

    private HttpAuthenticationResult sendUnauthorizedRequest(HttpServletRequest request, HttpServletResponse response, String reason) throws IOException {
        LOG.warn(reason);
        HttpAuthUtil.setUnauthenticatedReason(request, reason);
        response.sendError(HttpStatus.UNAUTHORIZED.value(), reason);
        return HttpAuthenticationResult.unauthenticated();
    }
}
