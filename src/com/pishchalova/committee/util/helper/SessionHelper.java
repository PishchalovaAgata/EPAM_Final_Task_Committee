package com.pishchalova.committee.util.helper;

import com.pishchalova.committee.entity.User;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class SessionHelper {
    private static final String JSESSIONID = "JSESSIONID";

    public static boolean isAdmin(HttpServletRequest request) throws ServiceException {
        UserService userService = new UserService();
        String sessionID = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(JSESSIONID)) {
                    sessionID = cookie.getValue();
                }
            }
        }
        Integer userId = (Integer) request.getServletContext().getAttribute(sessionID);
        User user = userService.findUserById(userId);
        return user != null && user.getRole() == User.Role.ADMIN;
    }
}
