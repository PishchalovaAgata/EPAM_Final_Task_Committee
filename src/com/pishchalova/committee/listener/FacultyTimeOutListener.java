package com.pishchalova.committee.listener;

import com.pishchalova.committee.command.Command;
import com.pishchalova.committee.command.faculty.FacultyTimeOutCommand;
import com.pishchalova.committee.exception.CommandException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.util.Timer;
import java.util.TimerTask;

@WebListener()
public class FacultyTimeOutListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimeTask(), 0, 10 * 1000);
    }

    private static class MyTimeTask extends TimerTask {
        public void run() {
            try {
                new FacultyTimeOutCommand().execute(null, null, Command.ActionType.GET);
            } catch (CommandException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
