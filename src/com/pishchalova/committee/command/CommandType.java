package com.pishchalova.committee.command;

import com.pishchalova.committee.command.entrant.*;
import com.pishchalova.committee.command.faculty.*;
import com.pishchalova.committee.command.footer.FooterCommand;
import com.pishchalova.committee.command.subject.ChangeListOfSubjectsCommand;
import com.pishchalova.committee.command.subject.ViewAllSubjectsCommand;
import com.pishchalova.committee.command.user.ConfirmRegistrationCommand;
import com.pishchalova.committee.command.user.LoginUserCommand;
import com.pishchalova.committee.command.user.LogoutUserCommand;
import com.pishchalova.committee.command.user.RegisterUserCommand;

public enum  CommandType {
    COMMITTEE_ENTRANT_EDIT(new RegisterEntrantInformationCommand()),
    ENTRANT(new GetEntrantInformationCommand()),
    ENTRANT_FACULTY(new getFacultyForEntrantCommand()),
    ENTRANT_UNSUBSCRIBE(new DeregisterEntrantForFacultyCommand()),
    FACULTY_ADD(new AddNewFacultyCommand()),
    FACULTIES(new GetFacultiesCommand()),
    FACULTY(new GetFacultyInformationCommand() ),
    FACULTY_EDIT(new EditFacultyCommand()),
    SUBJECTS(new ViewAllSubjectsCommand()),
    REGISTER(new RegisterUserCommand()),
    FACULTY_DELETE(new DeleteFacultyCommand()),
    LOGIN(new LoginUserCommand()),
    SUBJECTS_EDIT(new ChangeListOfSubjectsCommand()),
    FACULTY_REGISTER(new RegisterEntrantForFacultyCommand()),
    FACULTY_SHEET(new GetReportSheetCommand()),
    FOOTER(new FooterCommand()),
    LOGOUT(new LogoutUserCommand()),
    ENTRANT_STATUS(new GetEntrantStatusCommand()),
//    FACULTY_TIME_OUT(new FacultyTimeOutCommand()),
    CONFIRM_REGISTRATION(new ConfirmRegistrationCommand());

    private Command command;

    CommandType(){}

    CommandType(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public static Command of(String value) {
        return valueOf(value.toUpperCase()).getCommand();
    }
}
