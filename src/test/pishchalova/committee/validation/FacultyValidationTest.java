package test.pishchalova.committee.validation;

import com.pishchalova.committee.entity.Entrant;
import com.pishchalova.committee.entity.Faculty;
import com.pishchalova.committee.entity.Mark;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.validation.EntrantValidation;
import com.pishchalova.committee.validation.FacultyValidation;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FacultyValidationTest {
    private HashMap<Integer, String> subjects = new HashMap<>();

    @BeforeClass
    private void init() {
        subjects.put(1, "Math");
        subjects.put(2, "Math 123");
        subjects.put(3, "math");
        subjects.put(4, "math123");
        subjects.put(5, "123");
        subjects.put(6, "English");
        subjects.put(7, "Physics");
    }

    @DataProvider
    public Object[][] testFacultyValidation() {
        return new Object[][]{
                {
                        new Faculty(1, "Frukgnj", 1000L, 1, 0, new ArrayList<Integer>() {
                            {
                                this.add(1);
                                this.add(1);
                                this.add(1);
                            }
                        }), false
                },
                {
                        new Faculty(1, "Famcs", 1000L, 1000, 0, new ArrayList<Integer>() {
                            {
                                this.add(1);
                                this.add(6);
                                this.add(7);
                            }
                        }), true
                },
                {
                        new Faculty(1, "Famcs", 1000L, 10, 0, new ArrayList<Integer>() {
                            {
                                this.add(1);
                                this.add(6);
                                this.add(7);
                            }
                        }), false
                },
                {
                        new Faculty(1, "Famcs", 1000L, 1000, 0, new ArrayList<Integer>() {
                            {
                                this.add(1);
                                this.add(6);
                            }
                        }), false
                },

                {
                        new Faculty(1, "famcs", 1000L, 10, 0, new ArrayList<Integer>() {
                            {
                                this.add(1);
                                this.add(6);
                                this.add(7);
                            }
                        }), false
                },

                {
                        new Faculty(1, "Famcs", 1000L, 1000, 0, new ArrayList<Integer>() {
                            {
                                this.add(1);
                                this.add(2);
                                this.add(3);
                            }
                        }), false
                },
                {
                        new Faculty(1, "Famcs", 1000L, 1000, 0, new ArrayList<Integer>() {
                            {
                                this.add(3);
                                this.add(4);
                                this.add(5);
                            }
                        }), false
                },

        };
    }


    @Test(dataProvider = "testFacultyValidation")
    public void test(Faculty actual, boolean expected) throws ServiceException {
        ArrayList<String> strings = new ArrayList<>();
        for (Integer id : actual.getSubjectsId()) {
            for (Map.Entry entry : subjects.entrySet()) {
                Integer key = (Integer) entry.getKey();
                if (key.equals(id)) {
                    strings.add((String) entry.getValue());
                }
            }
        }
        Assert.assertEquals(FacultyValidation.validateFacultyParameters(actual.getFacultyName(), String.valueOf(actual.getEntryPlan()), strings),
                expected);
    }
}
