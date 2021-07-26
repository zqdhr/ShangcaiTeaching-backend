package org.tdf.sim.util;

import org.jasig.cas.client.authentication.AttributePrincipal;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ApplicationUtils {

	public static LoginUser getCurrentLoginUser(HttpServletRequest request) {
		LoginUser loginUser = (LoginUser) request.getSession().getAttribute(ConstantUtils.USER_SESSION_KEY);
		if(loginUser != null) {
			return loginUser;
		}
		String name = request.getRemoteUser();
		if ("".equals(name) || name == null) {
			return null;
		}
		AttributePrincipal attrPrincipal = (AttributePrincipal) request.getUserPrincipal();
		Map<String, Object> attMap = attrPrincipal.getAttributes();
		LoginUser userinfo = new LoginUser();
		userinfo.setId((String) attMap.get("id"));
		userinfo.setUid((String) attMap.get("user_uid"));
		userinfo.setUserName((String) attMap.get("userName"));
		userinfo.setNick((String) attMap.get("nick"));
		userinfo.setStaffNo((String) attMap.get("staffNo"));
		userinfo.setEmail((String) attMap.get("email"));
		userinfo.setTel((String) attMap.get("tel"));
		request.getSession().setAttribute(ConstantUtils.USER_SESSION_KEY, userinfo);
		return userinfo;
	}

}
