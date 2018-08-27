package com.pishchalova.committee.entity;

public class Mark implements Entity {
    private Integer subjectId;
    private int value;


    public Mark(Integer subjectId, int value) {
        this.subjectId = subjectId;
        this.value = value;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mark)) return false;

        Mark mark = (Mark) o;

        if (getValue() != mark.getValue()) return false;
        return getSubjectId() != null ? getSubjectId().equals(mark.getSubjectId()) : mark.getSubjectId() == null;
    }

    @Override
    public int hashCode() {
        int result = getSubjectId() != null ? getSubjectId().hashCode() : 0;
        result = 31 * result + getValue();
        return result;
    }

    @Override
    public String toString() {
        return "Mark{" +
                "subjectId=" + subjectId +
                ", value=" + value +
                '}';
    }
}
