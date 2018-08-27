package com.pishchalova.committee.tag;

import java.io.IOException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;


public class CustomFooter extends SimpleTagSupport {
    @Override
    public void doTag() throws IOException {
        JspWriter out = getJspContext().getOut();
        out.println("<footer name=\"page-footer\" >");
        out.println("Epam Project by <a href=\"mailto:pishalova.14@gmail.com\">Agatha Pishchalova<a/>");
        out.println("</footer>");
    }
}
