package com.pishchalova.committee.entity;

import java.util.ArrayList;

public class Subject implements Entity {
    private Integer id;
    private String name;
    private boolean isUnavailable;
    private ArrayList<Integer> facultiesId;

    public Subject(String name) {
        this.name = name;
        this.isUnavailable = false;
        this.facultiesId = new ArrayList<>();
    }

    public Subject(Integer id, String name, boolean isUnavailable) {
        this.id = id;
        this.name = name;
        this.isUnavailable = isUnavailable;
        this.facultiesId = new ArrayList<>();
    }

    public Subject(Integer id, String name, boolean isUnavailable, ArrayList<Integer> facultiesId) {
        this.id = id;
        this.name = name;
        this.isUnavailable = isUnavailable;
        this.facultiesId = facultiesId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUnavailable() {
        return isUnavailable;
    }

    public void setUnavailable(boolean unavailable) {
        isUnavailable = unavailable;
    }

    public ArrayList<Integer> getFacultiesId() {
        return facultiesId;
    }

    public void setFacultiesId(ArrayList<Integer> facultiesId) {
        this.facultiesId = facultiesId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;

        Subject subject = (Subject) o;

        if (isUnavailable() != subject.isUnavailable()) return false;
        if (getId() != null ? !getId().equals(subject.getId()) : subject.getId() != null) return false;
        if (getName() != null ? !getName().equals(subject.getName()) : subject.getName() != null) return false;
        return getFacultiesId() != null ? getFacultiesId().equals(subject.getFacultiesId()) : subject.getFacultiesId() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (isUnavailable() ? 1 : 0);
        result = 31 * result + (getFacultiesId() != null ? getFacultiesId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isUnavailable=" + isUnavailable +
                ", facultiesId=" + facultiesId +
                '}';
    }
}
