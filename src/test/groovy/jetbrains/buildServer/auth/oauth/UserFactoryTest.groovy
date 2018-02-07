package jetbrains.buildServer.auth.oauth

import jetbrains.buildServer.auth.oauth.helpers.StubSUser
import jetbrains.buildServer.users.InvalidUsernameException
import jetbrains.buildServer.users.SUser
import jetbrains.buildServer.users.UserModel
import spock.lang.Specification

class UserFactoryTest extends Specification {

    UserModel userModel = Mock()
    SUser teamcityUser = Mock()
    UserFactory userFactory

    def setup() {
        userFactory = new UserFactory(userModel)
    }

    def "read user from model"() {
        given:
        def user = new OAuthUser("testUser", "Test Name", "test@test.com")
        this.teamcityUser.getUsername() >> user.id
        userModel.findUserByUsername(_, _) >> new StubSUser(user)
        when:
        SUser principal = userFactory.getUser(user, true).get()
        then:
        principal != null
        principal.username == user.id
    }

    def "create user if model reports null"() {
        given:
        def user = new OAuthUser("testUser", "Test Name", "test@test.com")
        userModel.findUserByUsername(_,_) >> null
        userModel.createUserAccount(PluginConstants.OAUTH_AUTH_SCHEME_NAME, user.id) >> new StubSUser(user)
        when:
        SUser principal = userFactory.getUser(user, true).get()
        then:
        principal != null
        principal.username == user.id
    }

    def "return empty user if model reports null"() {
        given:
        def user = new OAuthUser("testUser", "Test Name", "test@test.com")
        userModel.findUserByUsername(_,_) >> null
        when:
        Optional<SUser> principal = userFactory.getUser(user, false)
        then:
        !principal.isPresent()
    }

    def "create user if model reports exception"() {
        given:
        def user = new OAuthUser("testUser", "Test Name", "test@test.com")
        userModel.findUserByUsername(_,_) >> { throw new InvalidUsernameException("mocked reason") }
        userModel.createUserAccount(PluginConstants.OAUTH_AUTH_SCHEME_NAME, user.id) >> new StubSUser(user)
        when:
        SUser principal = userFactory.getUser(user, true).get()
        then:
        principal != null
        principal.username == user.id
    }
}
