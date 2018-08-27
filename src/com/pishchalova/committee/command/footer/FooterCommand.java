package com.pishchalova.committee.command.footer;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.exception.CommandException;
import com.pishchalova.committee.util.ParameterConst.CommandParameterConst;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FooterCommand implements Command {

    private static final String FOOTER = "/WEB-INF/jsp/footer.jsp";

    @Override
    public String doGet(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        try {
            request.getSession().getServletContext()
                    .getRequestDispatcher(request.getContextPath() + FOOTER)
                    .forward(request, response);
        } catch (ServletException | IOException e) {
            throw new CommandException("There is a problem while construction footer!", e);
        }
        return CommandParameterConst.EMPTY_STRING;
    }


    @Override
    public String doPost(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
