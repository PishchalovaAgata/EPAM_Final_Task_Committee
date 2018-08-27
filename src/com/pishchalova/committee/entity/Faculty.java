package com.pishchalova.committee.entity;

import com.pishchalova.committee.exception.WrongDateFormattingException;

import javax.xml.bind.DatatypeConverter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.pishchalova.committee.validation.FieldEntityValidation.isValidDate;


public class Faculty implements Entity {

    private static final String SPECIAL_DATE_FORMATTING = "yyyy-MM-dd";


    private Integer id;
    private String facultyName;
    private String endDateReceiving;
    private int entryPlan;
    private int amountEntrant;
    private boolean isUnavailable;
    private boolean enrollmentIsOver = false;
    private ArrayList<Integer> subjectsId;

    public Faculty() {
    }


    public Long getEndDateReceivingAsLong() throws WrongDateFormattingException {
        return getMillisecondsFromDate(endDateReceiving);
    }

    public void setEndDateReceiving(String endDateReceiving) {
        this.endDateReceiving = endDateReceiving;
    }

    public Faculty(String facultyName, Long dateMillis, int entryPlan, ArrayList<Integer> subjectsId) {
        this.facultyName = facultyName;
        this.endDateReceiving = getDateFromMilliSeconds(dateMillis);
        this.entryPlan = entryPlan;
        this.amountEntrant = 0;
        this.isUnavailable = false;
        this.subjectsId = subjectsId;
    }

    public Faculty(Integer id, String facultyName, Long dateMillis, int entryPlan, ArrayList<Integer> subjectsId) {
        this.id = id;
        this.facultyName = facultyName;
        this.endDateReceiving = getDateFromMilliSeconds(dateMillis);
        this.entryPlan = entryPlan;
        this.amountEntrant = 0;
        this.isUnavailable = false;
        this.subjectsId = subjectsId;
    }

    public Faculty(Integer id, String facultyName, Long dateMillis, int entryPlan, int amountEntrant, ArrayList<Integer> subjectsId) {
        this.id = id;
        this.facultyName = facultyName;
        this.endDateReceiving = getDateFromMilliSeconds(dateMillis);
        this.entryPlan = entryPlan;
        this.amountEntrant = amountEntrant;
        this.isUnavailable = false;
        this.subjectsId = subjectsId;
    }

    public Faculty(Integer id, String facultyName, Long dateMillis, int entryPlan) {
        this.id = id;
        this.facultyName = facultyName;
        this.endDateReceiving = getDateFromMilliSeconds(dateMillis);
        this.entryPlan = entryPlan;
        this.amountEntrant = 0;
        this.isUnavailable = false;
        this.subjectsId = new ArrayList<>();
    }


    public Faculty(Integer id, String facultyName, Long dateMillis, int entryPlan, int amountEntrant, boolean isUnavailable, boolean enrollmentIsOver, ArrayList<Integer> subjectsId) {
        this.id = id;
        this.facultyName = facultyName;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateMillis);
        SimpleDateFormat format = new SimpleDateFormat(SPECIAL_DATE_FORMATTING);
        System.out.println(format.format(cal.getTime()));
        this.endDateReceiving = format.format(cal.getTime());
        this.entryPlan = entryPlan;
        this.amountEntrant = amountEntrant;
        this.isUnavailable = isUnavailable;
        this.enrollmentIsOver = enrollmentIsOver;
        this.subjectsId = subjectsId;
    }

    private static String getDateFromMilliSeconds(long longDate) {
        return new SimpleDateFormat(SPECIAL_DATE_FORMATTING)
                .format(new Date(longDate));
    }

    private static Long getMillisecondsFromDate(String dateInString) throws WrongDateFormattingException {
        if (isValidDate(dateInString)) {
            return DatatypeConverter
                    .parseDateTime(dateInString)
                    .getTimeInMillis();
        } else {
            throw new WrongDateFormattingException("Wrong Date Formatting Exception! Be Careful!");
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public int getEntryPlan() {
        return entryPlan;
    }

    public void setEntryPlan(int entryPlan) {
        this.entryPlan = entryPlan;
    }

    public int getAmountEntrant() {
        return amountEntrant;
    }

    public void setAmountEntrant(int amountEntrant) {
        this.amountEntrant = amountEntrant;
    }

    public boolean isUnavailable() {
        return isUnavailable;
    }

    public void setUnavailable(boolean unavailable) {
        isUnavailable = unavailable;
    }

    public boolean isEnrollmentIsOver() {
        return enrollmentIsOver;
    }

    public void setEnrollmentIsOver(boolean enrollmentIsOver) {
        this.enrollmentIsOver = enrollmentIsOver;
    }

    public ArrayList<Integer> getSubjectsId() {
        return subjectsId;
    }

    public void setSubjectsId(ArrayList<Integer> subjectsId) {
        this.subjectsId = subjectsId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Faculty)) return false;

        Faculty faculty = (Faculty) o;

        if (getEntryPlan() != faculty.getEntryPlan()) return false;
        if (getAmountEntrant() != faculty.getAmountEntrant()) return false;
        if (isUnavailable() != faculty.isUnavailable()) return false;
        if (isEnrollmentIsOver() != faculty.isEnrollmentIsOver()) return false;
        if (getId() != null ? !getId().equals(faculty.getId()) : faculty.getId() != null) return false;
        if (getFacultyName() != null ? !getFacultyName().equals(faculty.getFacultyName()) : faculty.getFacultyName() != null)
            return false;
        if (endDateReceiving != null ? !endDateReceiving.equals(faculty.endDateReceiving) : faculty.endDateReceiving != null)
            return false;
        return getSubjectsId() != null ? getSubjectsId().equals(faculty.getSubjectsId()) : faculty.getSubjectsId() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getFacultyName() != null ? getFacultyName().hashCode() : 0);
        result = 31 * result + (endDateReceiving != null ? endDateReceiving.hashCode() : 0);
        result = 31 * result + getEntryPlan();
        result = 31 * result + getAmountEntrant();
        result = 31 * result + (isUnavailable() ? 1 : 0);
        result = 31 * result + (isEnrollmentIsOver() ? 1 : 0);
        result = 31 * result + (getSubjectsId() != null ? getSubjectsId().hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "Faculty{" +
                "id=" + id +
                ", facultyName='" + facultyName + '\'' +
                ", endDateReceiving='" + endDateReceiving + '\'' +
                ", entryPlan=" + entryPlan +
                ", amountEntrant=" + amountEntrant +
                ", isUnavailable=" + isUnavailable +
                ", enrollmentIsOver=" + enrollmentIsOver +
                ", subjectsId=" + subjectsId +
                '}';
    }
}
