package com.pishchalova.committee.util.helper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import javax.mail.*;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.Context;
import javax.naming.InitialContext;


import com.pishchalova.committee.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MailHelper {
    private static final Logger LOGGER = LogManager.getLogger(MailHelper.class);
    private static final Session SESSION = init();
    private static final String confirmSubjectEng = "Confirm Registration";
    private final static String confirmationURL = "http://localhost:8888/mail?command=confirm-registration&EncryptedData=";

    private static Session init() {
        Session session = null;
        try {
            Context initialContext = new InitialContext();
            session = (Session) initialContext.lookup("java:comp/env/mail/Session");
        } catch (Exception ex) {
            LOGGER.error("mail session lookup error", ex);
        }
        return session;
    }

    public static void sendConfirmationEmail(User user) {
        try {
            Message msg = new MimeMessage(SESSION);
            msg.setFrom(new InternetAddress("pishalova.14@gmail.com"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
            setContentToConfirmationEmailEng(msg, user);
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (MessagingException | UnsupportedEncodingException e) {
            LOGGER.error(e);
        }
    }

    private static void setContentToConfirmationEmailEng(Message msg, User user)
            throws MessagingException, UnsupportedEncodingException {
        msg.setSubject(confirmSubjectEng);

        Multipart multipart = new MimeMultipart();

        String encodedEmail = new String(Base64.getEncoder().encode(
                user.getEmail().getBytes(StandardCharsets.UTF_8)));

        InternetHeaders emailAndPass = new InternetHeaders();
        emailAndPass.addHeader("Content-type", "text/plain; charset=UTF-8");
        String hello = "<p>Hello, " + user.getLogin() + " !\n"
                + " You successfully registered on our web-site!</p><br/>";

        String data = "\nLogin: " + user.getLogin() + "\r\n E-mail: " + user.getEmail() + "\r\n";

        InternetHeaders headers = new InternetHeaders();
        headers.addHeader("Content-type", "text/html; charset=UTF-8");
        String confirmLink = "<p>Complete your registration by clicking on following "
                + "<a href='" + confirmationURL + encodedEmail + "'>link</a></p>";
        MimeBodyPart greetingAndData = new MimeBodyPart(headers, (hello + data + confirmLink).getBytes("UTF-8"));
        multipart.addBodyPart(greetingAndData);
        msg.setContent(multipart);
    }
}