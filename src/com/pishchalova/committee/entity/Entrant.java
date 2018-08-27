package com.pishchalova.committee.entity;

import java.util.ArrayList;

public class Entrant extends User {
    private Integer entrantId;
    private String entrantFirstName;
    private String entrantSurName;
    private int certificate;
    private Integer facultyId;
    private Integer userId;
    private ArrayList<Mark> marks;
    private Status status;
    private boolean isUnavailable;

    public Entrant(String entrantFirstName, String entrantSurName, int certificate, Integer userId, ArrayList<Mark> marks, Status status) {
        this.entrantFirstName = entrantFirstName;
        this.entrantSurName = entrantSurName;
        this.certificate = certificate;
        this.facultyId = 0;
        this.userId = userId;
        this.marks = marks;
        this.status = status;
        this.isUnavailable = false;
    }

    public Entrant(Integer entrantId, String entrantFirstName, String entrantSurName, int certificate, Integer facultyId, Integer userId,
                   ArrayList<Mark> marks, Status status) {
        this.entrantId = entrantId;
        this.entrantFirstName = entrantFirstName;
        this.entrantSurName = entrantSurName;
        this.certificate = certificate;
        this.facultyId = facultyId;
        this.userId = userId;
        this.marks = marks;
        this.status = status;
        this.isUnavailable = false;
    }

    public Entrant(Integer entrantId, String entrantFirstName, String entrantSurName, int certificate, Integer facultyId, Integer userId,
                   ArrayList<Mark> marks, Status status, boolean isUnavailable) {
        this.entrantId = entrantId;
        this.entrantFirstName = entrantFirstName;
        this.entrantSurName = entrantSurName;
        this.certificate = certificate;
        this.facultyId = facultyId;
        this.userId = userId;
        this.marks = marks;
        this.status = status;
        this.isUnavailable = isUnavailable;
    }

    public Entrant(Integer entrantId, String entrantFirstName, String entrantSurName, int certificate, Integer facultyId, Integer userId, ArrayList<Mark> marks, String status, boolean isUnavailable) {
        this.entrantId = entrantId;
        this.entrantFirstName = entrantFirstName;
        this.entrantSurName = entrantSurName;
        this.certificate = certificate;
        this.facultyId = facultyId;
        this.userId = userId;
        this.marks = marks;
        this.status = Status.valueOf(status);
        this.isUnavailable = isUnavailable;
    }

    public Entrant(Integer entrantId, String entrantFirstName, String entrantSurName, int certificate, Integer facultyId, Integer userId, Status status, boolean isUnavailable) {
        this.entrantId = entrantId;
        this.entrantFirstName = entrantFirstName;
        this.entrantSurName = entrantSurName;
        this.certificate = certificate;
        this.facultyId = facultyId;
        this.userId = userId;
        this.marks = new ArrayList<>();
        this.status = status;
        this.isUnavailable = isUnavailable;
    }

    public enum Status {
        FREE, ENLISTED, CANCELED, SUBMITTED;

        private String columnValue;

        Status() {
        }

        public String getColumnValue() {
            return columnValue;
        }

        public void setColumnValue(String columnValue) {
            this.columnValue = columnValue;
        }

        public static Status hasColumnInEntrantColumn(String value) {
            for (Status entrantColumn : Status.values()) {
                if (entrantColumn.getColumnValue().equals(value)) {
                    return entrantColumn;
                }
            }
            return null;
        }
    }


    public Integer getEntrantId() {
        return entrantId;
    }

    public void setEntrantId(Integer entrantId) {
        this.entrantId = entrantId;
    }

    public String getEntrantFirstName() {
        return entrantFirstName;
    }

    public void setEntrantFirstName(String entrantFirstName) {
        this.entrantFirstName = entrantFirstName;
    }

    public String getEntrantSurName() {
        return entrantSurName;
    }

    public void setEntrantSurName(String entrantSurName) {
        this.entrantSurName = entrantSurName;
    }

    public int getCertificate() {
        return certificate;
    }

    public void setCertificate(int certificate) {
        this.certificate = certificate;
    }

    public Integer getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Integer facultyId) {
        this.facultyId = facultyId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public ArrayList<Mark> getMarks() {
        return marks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entrant)) return false;
        if (!super.equals(o)) return false;

        Entrant entrant = (Entrant) o;

        if (getCertificate() != entrant.getCertificate()) return false;
        if (isUnavailable() != entrant.isUnavailable()) return false;
        if (getEntrantId() != null ? !getEntrantId().equals(entrant.getEntrantId()) : entrant.getEntrantId() != null)
            return false;
        if (getEntrantFirstName() != null ? !getEntrantFirstName().equals(entrant.getEntrantFirstName()) : entrant.getEntrantFirstName() != null)
            return false;
        if (getEntrantSurName() != null ? !getEntrantSurName().equals(entrant.getEntrantSurName()) : entrant.getEntrantSurName() != null)
            return false;
        if (getFacultyId() != null ? !getFacultyId().equals(entrant.getFacultyId()) : entrant.getFacultyId() != null)
            return false;
        if (getUserId() != null ? !getUserId().equals(entrant.getUserId()) : entrant.getUserId() != null) return false;
        if (getMarks() != null ? !getMarks().equals(entrant.getMarks()) : entrant.getMarks() != null) return false;
        return getStatus() == entrant.getStatus();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getEntrantId() != null ? getEntrantId().hashCode() : 0);
        result = 31 * result + (getEntrantFirstName() != null ? getEntrantFirstName().hashCode() : 0);
        result = 31 * result + (getEntrantSurName() != null ? getEntrantSurName().hashCode() : 0);
        result = 31 * result + getCertificate();
        result = 31 * result + (getFacultyId() != null ? getFacultyId().hashCode() : 0);
        result = 31 * result + (getUserId() != null ? getUserId().hashCode() : 0);
        result = 31 * result + (getMarks() != null ? getMarks().hashCode() : 0);
        result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
        result = 31 * result + (isUnavailable() ? 1 : 0);
        return result;
    }

    public void setMarks(ArrayList<Mark> marks) {
        this.marks = marks;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isUnavailable() {
        return isUnavailable;
    }

    public void setUnavailable(boolean unavailable) {
        isUnavailable = unavailable;
    }

    @Override
    public String toString() {
        return "Entrant{" +
                "entrantId=" + entrantId +
                ", entrantFirstName='" + entrantFirstName + '\'' +
                ", entrantSurName='" + entrantSurName + '\'' +
                ", certificate=" + certificate +
                ", facultyId=" + facultyId +
                ", userId=" + userId +
                ", marks=" + marks +
                ", status=" + status +
                ", isUnavailable=" + isUnavailable +
                '}';
    }
}
