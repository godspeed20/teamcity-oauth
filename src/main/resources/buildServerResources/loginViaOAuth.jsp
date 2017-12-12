<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="jetbrains.buildServer.auth.oauth.PluginConstants" %>
<%@ include file="/include-internal.jsp" %>
<c:set var="oauthLoginUrl"><%=PluginConstants.Web.LOGIN_PATH%>
</c:set>
<c:if test="${oauth2_settings.schemeConfigured}">
    <c:if test="${oauth2_settings.hideLoginForm}">
        <style>
            .loginForm {
                display: none;
            }

            .oauth-guest-section {
                text-align: right;
                padding-top: 0.5em;
            }
        </style>
    </c:if>
    <style>
        .google-form-wrapper {
            width: 262px !important;
            display: flex;
            height: 29px;
            background-color: #4285f4;
            border: 1px solid #4285f4;
            color: #fff;
            box-shadow: 0 1px 0 rgba(0, 0, 0, 0.10);
            border-radius: 3px;
            font-family: Roboto, arial, sans-serif;
            outline: none;
            overflow: hidden;
            text-decoration: none;
            white-space: nowrap;
        }

        .google-icon {
            background: no-repeat white center url(//www.gstatic.com/images/branding/googleg/2x/googleg_standard_color_18dp.png);
            background-size: 18px 18px;
            margin-top: -1px;
            height: 32px;
            width: 32px;
        }

        .google-button {
            border-radius: 0 3px 3px 0;
            display: inline-block;
            width: 100%;
            color: white;
            background: inherit;
            border: none;
            cursor: pointer;
        }
    </style>
    <div>
        <form class="google-form-wrapper" action="<c:url value='${oauthLoginUrl}'/>" method="GET">
            <div class="google-icon"></div>
            <input class="google-button" type="submit" name="submitLogin"
                   value="Sign in with Google">
            <c:if test="${oauth2_settings.hideLoginForm and oauth2_settings.guestLoginAllowed}">
                <div class="oauth-guest-section">
                    <span class="greyNote">
                        <a href="<c:url value='/guestLogin.html?guest=1'/>">Log in as guest</a>&nbsp;
                        <bs:help file="User+Account"/>
                    </span>
                </div>
            </c:if>
        </form>
    </div>
</c:if>
