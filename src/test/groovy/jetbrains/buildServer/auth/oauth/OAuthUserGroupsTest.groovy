package jetbrains.buildServer.auth.oauth

import jetbrains.buildServer.auth.oauth.helpers.StubSUser
import jetbrains.buildServer.auth.oauth.helpers.StubUserGroup
import jetbrains.buildServer.groups.UserGroupManager
import jetbrains.buildServer.web.openapi.PluginDescriptor
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class OAuthUserGroupsTest extends Specification {

    private PluginDescriptor pluginDescriptor = Mock()
    private OAuthClient client = Mock()
    private HttpServletResponse res = Mock()

    private existingGroups = []
    private existingUserGroups = []
    private createdGroups = []

    def "create missing role"() {
        given:
        def existingGroup = new StubUserGroup("bob", "bob", "bob")
        UserGroupManager userGroupManager = Mock() {
            findUserGroupByKey(_) >> { String key ->
                if (!existingGroups.contains(key)) return null
                return existingGroup
            }
            createUserGroup(_, _, _) >> { String key, String name, String description ->
                createdGroups.add(key)
                return new StubUserGroup(key, name, description)
            }
        }
        UserFactory principalFactory = Mock() {
            getUser(_, _) >> { OAuthUser user, boolean allow ->
                if (allow) Optional.of(new StubSUser(user, existingUserGroups))
                else Optional.empty()
            }
        }
        def scheme = new OAuthAuthenticationScheme(pluginDescriptor, principalFactory, userGroupManager, client)

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
        existingGroups = ['role1']
        existingUserGroups = ['role1']
        when:
        scheme.processAuthenticationRequest(req, res, [:])
        then:
        createdGroups == ['role2']
        existingGroup.removedUsers == []
    }

    def "removes groups user is no longer part of"() {
        given:
        def existingGroup = new StubUserGroup("bob", "bob", "bob")
        UserGroupManager userGroupManager = Mock() {
            findUserGroupByKey(_) >> { String key ->
                return existingGroup
            }
            createUserGroup(_, _, _) >> { String key, String name, String description ->
                throw new UnsupportedOperationException("we do not expect this to be called")
            }
        }
        UserFactory principalFactory = Mock() {
            getUser(_, _) >> { OAuthUser user, boolean allow ->
                if (allow) Optional.of(new StubSUser(user, existingUserGroups))
                else Optional.empty()
            }
        }
        def scheme = new OAuthAuthenticationScheme(pluginDescriptor, principalFactory, userGroupManager, client)

        def userName = "testUser"
        def user = new OAuthUser(userName)
        HttpServletRequest req = Mock() {
            getParameter(OAuthAuthenticationScheme.CODE) >> "code"
            getParameter(OAuthAuthenticationScheme.STATE) >> "state"
            getRequestedSessionId() >> "state"
        }
        client.getAccessToken("code") >> "token"
        client.getUserData("token") >> user
        client.getUserRoles(user) >> Optional.of(new OAuthUserRoles([roles: []]))
        existingGroups = ['role1']
        existingUserGroups = ['role1']
        when:
        scheme.processAuthenticationRequest(req, res, [:])
        then:
        createdGroups == []
        existingGroup.removedUsers == [userName]
    }

    def "does nothing when no change"() {
        given:
        def existingGroup = new StubUserGroup("bob", "bob", "bob")
        UserGroupManager userGroupManager = Mock() {
            findUserGroupByKey(_) >> { String key ->
                return existingGroup
            }
            createUserGroup(_, _, _) >> { String key, String name, String description ->
                throw new UnsupportedOperationException("we do not expect this to be called")
            }
        }
        UserFactory principalFactory = Mock() {
            getUser(_, _) >> { OAuthUser user, boolean allow ->
                if (allow) Optional.of(new StubSUser(user, existingUserGroups))
                else Optional.empty()
            }
        }
        def scheme = new OAuthAuthenticationScheme(pluginDescriptor, principalFactory, userGroupManager, client)

        def userName = "testUser"
        def user = new OAuthUser(userName)
        HttpServletRequest req = Mock() {
            getParameter(OAuthAuthenticationScheme.CODE) >> "code"
            getParameter(OAuthAuthenticationScheme.STATE) >> "state"
            getRequestedSessionId() >> "state"
        }
        client.getAccessToken("code") >> "token"
        client.getUserData("token") >> user
        client.getUserRoles(user) >> Optional.of(new OAuthUserRoles([roles: ['role1']]))
        existingGroups = ['role1']
        existingUserGroups = ['role1']
        when:
        scheme.processAuthenticationRequest(req, res, [:])
        then:
        createdGroups == []
        existingGroup.removedUsers == []
    }
}
