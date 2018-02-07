package jetbrains.buildServer.auth.oauth.helpers

import jetbrains.buildServer.auth.oauth.OAuthUser
import jetbrains.buildServer.groups.UserGroup
import jetbrains.buildServer.notification.DuplicateNotificationRuleException
import jetbrains.buildServer.notification.NotificationRule
import jetbrains.buildServer.notification.NotificationRulesHolder
import jetbrains.buildServer.notification.WatchedBuilds
import jetbrains.buildServer.serverSide.SBuildType
import jetbrains.buildServer.serverSide.SProject
import jetbrains.buildServer.serverSide.auth.*
import jetbrains.buildServer.users.*
import jetbrains.buildServer.vcs.SVcsModification

class StubSUser implements SUser {
    private OAuthUser user

    StubSUser(OAuthUser user) {
        this.user = user
    }

    List<SVcsModification> getVcsModifications(int numberOfActiveDays) {
        return null
    }

    List<SVcsModification> getAllModifications() {
        return null
    }

    void updateUserAccount(
            String username, String name, String email) throws UserNotFoundException, DuplicateUserAccountException, EmptyUsernameException {

    }

    void setUserProperties(Map<? extends PropertyKey, String> properties) throws UserNotFoundException {

    }

    void setUserProperty(PropertyKey propertyKey, String value) throws UserNotFoundException {

    }

    void deleteUserProperty(PropertyKey propertyKey) throws UserNotFoundException {

    }

    void setPassword(String password) throws UserNotFoundException {

    }

    List<String> getProjectsOrder() throws UserNotFoundException {
        return null
    }

    void setProjectsOrder(List<String> projectsOrder) throws UserNotFoundException {

    }

    void setVisibleProjects(Collection<String> visibleProjects) throws UserNotFoundException {

    }

    void hideProject(String projectId) throws UserNotFoundException {

    }

    void setLastLoginTimestamp(Date timestamp) throws UserNotFoundException {

    }

    void setBlockState(String blockType, String blockState) {

    }

    String getBlockState(String blockType) {
        return null
    }

    List<UserGroup> getUserGroups() {
        return null
    }

    List<UserGroup> getAllUserGroups() {
        return null
    }

    List<VcsUsernamePropertyKey> getVcsUsernameProperties() {
        return null
    }

    List<SBuildType> getOrderedBuildTypes(SProject project) {
        return null
    }

    Collection<SBuildType> getBuildTypesOrder(SProject project) {
        return null
    }

    void setBuildTypesOrder(
            SProject project, List<SBuildType> visible, List<SBuildType> invisible) {

    }

    boolean isHighlightRelatedDataInUI() {
        return false
    }

    boolean isGuest() {
        return false
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

    long getId() {
        return 0
    }

    String getRealm() {
        return null
    }

    String getUsername() {
        return user.id
    }

    String getName() {
        return null
    }

    String getEmail() {
        return null
    }

    String getDescriptiveName() {
        return null
    }

    String getExtendedName() {
        return null
    }

    Date getLastLoginTimestamp() {
        return null
    }

    List<String> getVisibleProjects() {
        return null
    }

    List<String> getAllProjects() {
        return null
    }

    String describe(boolean verbose) {
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
