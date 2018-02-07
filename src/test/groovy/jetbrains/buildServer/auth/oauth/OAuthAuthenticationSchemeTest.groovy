package jetbrains.buildServer.auth.oauth

import jetbrains.buildServer.auth.oauth.helpers.StubSUser
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationResult
import jetbrains.buildServer.groups.UserGroupManager
import jetbrains.buildServer.web.openapi.PluginDescriptor
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class OAuthAuthenticationSchemeTest extends Specification {

    OAuthClient client = Mock()
    HttpServletResponse res = Mock()
    OAuthAuthenticationScheme scheme

    def setup() {
        PluginDescriptor pluginDescriptor = Mock()
        UserGroupManager userGroupManager = Mock()
        UserFactory principalFactory = Mock() {
            getUser(_, _) >> { OAuthUser user, boolean allow ->
                if (allow)
                    Optional.of(new StubSUser(user))
                else Optional.empty()
            }
        }
        scheme = new OAuthAuthenticationScheme(pluginDescriptor, principalFactory, userGroupManager, client)
    }

    @Unroll
    def "handle as not applicable if state or code is missing"() {
        given:
        HttpServletRequest req = Mock() {
            getParameter(OAuthAuthenticationScheme.CODE) >> code
            getParameter(OAuthAuthenticationScheme.STATE) >> state
        }
        when:
        HttpAuthenticationResult result = scheme.processAuthenticationRequest(req, res, [:])
        then:
        result.type == type

        where:
        code   | state   || type
        null   | null    || HttpAuthenticationResult.Type.NOT_APPLICABLE
        "code" | null    || HttpAuthenticationResult.Type.NOT_APPLICABLE
        null   | "state" || HttpAuthenticationResult.Type.NOT_APPLICABLE
        ""     | ""      || HttpAuthenticationResult.Type.NOT_APPLICABLE
    }

    def "handle as unauthenticated if state does not match"() {
        given:
        HttpServletRequest req = Mock() {
            getParameter(OAuthAuthenticationScheme.CODE) >> "code"
            getParameter(OAuthAuthenticationScheme.STATE) >> "state"
            getRequestedSessionId() >> "id"
        }
        when:
        HttpAuthenticationResult result = scheme.processAuthenticationRequest(req, res, [:])
        then:
        result.type == HttpAuthenticationResult.Type.UNAUTHENTICATED
        1 * res.sendError(401, "Unauthenticated since retrieved 'state' doesn't correspond to current TeamCity session.")
    }

    def "handle as unauthenticated if token is not acquired"() {
        given:
        HttpServletRequest req = Mock() {
            getParameter(OAuthAuthenticationScheme.CODE) >> "code"
            getParameter(OAuthAuthenticationScheme.STATE) >> "state"
            getRequestedSessionId() >> "state"
        }
        when:
        HttpAuthenticationResult result = scheme.processAuthenticationRequest(req, res, [:])
        then:
        result.type == HttpAuthenticationResult.Type.UNAUTHENTICATED
        1 * res.sendError(401, "Unauthenticated since failed to fetch token for code 'code' and state 'state'.")
    }

    def "handle as unauthenticated if user data are not valid"() {
        given:
        HttpServletRequest req = Mock() {
            getParameter(OAuthAuthenticationScheme.CODE) >> "code"
            getParameter(OAuthAuthenticationScheme.STATE) >> "state"
            getRequestedSessionId() >> "state"
        }
        client.getAccessToken("code") >> "token"
        client.getUserData("token") >> new OAuthUser((String) null)
        when:
        HttpAuthenticationResult result = scheme.processAuthenticationRequest(req, res, [:])
        then:
        result.type == HttpAuthenticationResult.Type.UNAUTHENTICATED
        1 * res.sendError(401, "Unauthenticated since user endpoint does not return any login id")
    }

    def "authenticate user"() {
        given:
        def userName = "testUser"
        def user = new OAuthUser(userName)
        HttpServletRequest req = Mock() {
            getParameter(OAuthAuthenticationScheme.CODE) >> "code"
            getParameter(OAuthAuthenticationScheme.STATE) >> "state"
            getRequestedSessionId() >> "state"
        }
        client.getAccessToken("code") >> "token"
        client.getUserData("token") >> user
        client.getUserRoles(user) >> Optional.empty()
        when:
        HttpAuthenticationResult result = scheme.processAuthenticationRequest(req, res, [:])
        then:
        result.type == HttpAuthenticationResult.Type.AUTHENTICATED
        result.principal.name == userName
    }

    def "don't authenticate user if allow to create new account is disabled"() {
        given:
        def user = new OAuthUser("testUser")
        HttpServletRequest req = Mock() {
            getParameter(OAuthAuthenticationScheme.CODE) >> "code"
            getParameter(OAuthAuthenticationScheme.STATE) >> "state"
            getRequestedSessionId() >> "state"
        }
        client.getAccessToken("code") >> "token"
        client.getUserData("token") >> user
        client.getUserRoles(user) >> Optional.empty()
        when:
        HttpAuthenticationResult result = scheme.processAuthenticationRequest(req, res, [allowCreatingNewUsersByLogin: false])
        then:
        result.type == HttpAuthenticationResult.Type.UNAUTHENTICATED
        1 * res.sendError(401, 'Unauthenticated since user could not be found or created.')
    }
}
