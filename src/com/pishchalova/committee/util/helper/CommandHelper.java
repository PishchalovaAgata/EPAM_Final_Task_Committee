package com.pishchalova.committee.util.helper;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHelper {
    private static final String SLASH = "/";
    private static final String NUMBER = "[0-9]+";

    public static Integer getEntityIdForCommand(HttpServletRequest request) {
        int ignoreAmt = request.getContextPath().length() + request.getServletPath().length();
        String URI = request.getRequestURI().substring(ignoreAmt + 1);
        String[] components = URI.split(SLASH);
        Integer entityId = 0;
        for (String component : components) {
            Matcher matcher = Pattern.compile(NUMBER).matcher(component);
            if (matcher.matches()) {
                entityId = Integer.parseInt(component);
            }
        }
        return entityId;
    }
}
