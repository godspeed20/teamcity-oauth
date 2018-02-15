package jetbrains.buildServer.auth.oauth.helpers

import jetbrains.buildServer.groups.SUserGroup
import jetbrains.buildServer.groups.UserGroup
import jetbrains.buildServer.groups.UserGroupException
import jetbrains.buildServer.notification.DuplicateNotificationRuleException
import jetbrains.buildServer.notification.NotificationRule
import jetbrains.buildServer.notification.NotificationRulesHolder
import jetbrains.buildServer.notification.WatchedBuilds
import jetbrains.buildServer.serverSide.auth.*
import jetbrains.buildServer.users.PropertyKey
import jetbrains.buildServer.users.User

class StubUserGroup implements SUserGroup {
    private String description
    private String name
    private String key
    def removedUsers = []

    StubUserGroup(String key, String name, String description) {
        this.key = key
        this.name = name
        this.description = description
    }

    void setName(String name) throws UserGroupException {

    }

    void setDescription(String description) throws UserGroupException {

    }

    void addSubgroup(UserGroup group) throws UserGroupException {

    }

    void removeSubgroup(UserGroup group) {

    }

    void addUser(User user) {

    }

    void removeUser(User user) throws UserGroupException {
        removedUsers.add(user.username)

    }

    void setGroupProperties(Map<? extends PropertyKey, String> properties) throws UserGroupException {

    }

    void setGroupProperty(PropertyKey propertyKey, String value) throws UserGroupException {

    }

    void deleteGroupProperty(PropertyKey propertyKey) throws UserGroupException {

    }

    String getKey() {
        return key
    }

    String getName() {
        return name
    }

    String getDescription() {
        return description
    }

    List<UserGroup> getDirectSubgroups() {
        return null
    }

    List<UserGroup> getAllSubgroups() {
        return null
    }

    List<UserGroup> getParentGroups() {
        return null
    }

    List<User> getDirectUsers() {
        return null
    }

    List<User> getAllUsers() {
        return null
    }

    boolean containsUserDirectly(User user) {
        return false
    }

    String describe(boolean verbose) {
        return null
    }

    List<NotificationRule> getNotificationRules(String notifierType) {
        return null
    }

    void setNotificationRules(String notifierType, List<NotificationRule> rules) {

    }

    void removeRule(long ruleId) {

    }

    void applyOrder(String notifierType, long[] ruleIds) {

    }

    long addNewRule(
            String notifierType, NotificationRule rule) throws DuplicateNotificationRuleException {
        return 0
    }

    Collection<Long> findConflictingRules(String notifierType, WatchedBuilds watch) {
        return null
    }

    NotificationRule findRuleById(long ruleId) {
        return null
    }

    List<NotificationRulesHolder> getParentRulesHolders() {
        return null
    }

    List<NotificationRulesHolder> getAllParentRulesHolders() {
        return null
    }

    boolean isPermissionGrantedGlobally(Permission permission) {
        return false
    }

    Permissions getGlobalPermissions() {
        return null
    }

    Map<String, Permissions> getProjectsPermissions() {
        return null
    }

    boolean isPermissionGrantedForProject(String projectId, Permission permission) {
        return false
    }

    boolean isPermissionGrantedForAllProjects(Collection<String> projectIds, Permission permission) {
        return false
    }

    boolean isPermissionGrantedForAnyProject(Permission permission) {
        return false
    }

    boolean isPermissionGrantedForAnyOfProjects(
            Collection<String> projectIds, Permission permission) {
        return false
    }

    Permissions getPermissionsGrantedForProject(String projectId) {
        return null
    }

    Permissions getPermissionsGrantedForAllProjects(Collection<String> projectIds) {
        return null
    }

    Permissions getPermissionsGrantedForAnyOfProjects(Collection<String> projectIds) {
        return null
    }

    User getAssociatedUser() {
        return null
    }

    Collection<Role> getRolesWithScope(RoleScope scope) {
        return null
    }

    Collection<RoleScope> getScopes() {
        return null
    }

    Collection<RoleEntry> getRoles() {
        return null
    }

    void addRole(RoleScope scope, Role role) {

    }

    void removeRole(RoleScope scope, Role role) {

    }

    void removeRole(Role role) {

    }

    void removeRoles(RoleScope scope) {

    }

    boolean isSystemAdministratorRoleGranted() {
        return false
    }

    boolean isSystemAdministratorRoleGrantedDirectly() {
        return false
    }

    boolean isSystemAdministratorRoleInherited() {
        return false
    }

    Collection<RolesHolder> getParentHolders() {
        return null
    }

    Collection<RolesHolder> getAllParentHolders() {
        return null
    }

    String getPropertyValue(PropertyKey propertyKey) {
        return null
    }

    boolean getBooleanProperty(PropertyKey propertyKey) {
        return false
    }

    Map<PropertyKey, String> getProperties() {
        return null
    }
}
