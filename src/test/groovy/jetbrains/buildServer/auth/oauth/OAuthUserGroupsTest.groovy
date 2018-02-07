package jetbrains.buildServer.auth.oauth

import jetbrains.buildServer.auth.oauth.helpers.StubSUser
import jetbrains.buildServer.auth.oauth.helpers.StubUserGroup
import jetbrains.buildServer.groups.UserGroupManager
import jetbrains.buildServer.web.openapi.PluginDescriptor
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class OAuthUserGroupsTest extends Specification {

    private OAuthClient client = Mock()
    private HttpServletResponse res = Mock()
    private OAuthAuthenticationScheme scheme

    private existingGroups = []
    private createdGroups = []

    def setup() {
        PluginDescriptor pluginDescriptor = Mock()
        UserGroupManager userGroupManager = Mock() {
            findUserGroupByKey(_) >> { String key ->
                if (!existingGroups.contains(key)) return null
                return new StubUserGroup(key, key, key)
            }
            createUserGroup(_, _, _) >> { String key, String name, String description ->
                createdGroups.add(key)
                return new StubUserGroup(key, name, description)
            }
        }
        UserFactory principalFactory = Mock() {
            getUser(_, _) >> { OAuthUser user, boolean allow ->
                if (allow) Optional.of(new StubSUser(user))
                else Optional.empty()
            }
        }
        scheme = new OAuthAuthenticationScheme(pluginDescriptor, principalFactory, userGroupManager, client)
    }

    def "create non existing roles"() {
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
        client.getUserRoles(user) >> Optional.of(new OAuthUserRoles([roles: ['role1', 'role2']]))
        existingGroups = []
        when:
        scheme.processAuthenticationRequest(req, res, [:])
        then:
        createdGroups == ['role1', 'role2']
    }

    def "does not create roles when they already exist"() {
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
        client.getUserRoles(user) >> Optional.of(new OAuthUserRoles([roles: ['role1', 'role2']]))
        existingGroups = ['role1', 'role2']
        when:
        scheme.processAuthenticationRequest(req, res, [:])
        then:
        createdGroups == []
    }
}
