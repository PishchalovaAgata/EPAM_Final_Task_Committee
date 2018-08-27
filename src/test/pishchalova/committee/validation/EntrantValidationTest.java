package test.pishchalova.committee.validation;

import com.pishchalova.committee.entity.Entrant;
import com.pishchalova.committee.entity.Mark;
import com.pishchalova.committee.entity.Subject;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.validation.EntrantValidation;
import com.pishchalova.committee.validation.SubjectValidation;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;

public class EntrantValidationTest {
    @DataProvider
    public Object[][] testEntrantValidation() {
        return new Object[][]{
                {
                        new Entrant("Ivan", "Ivanov", 100, 1, new ArrayList<Mark>() {
                            {
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                            }
                        }, Entrant.Status.FREE), true
                },

                {
                        new Entrant("ivan", "Ivanov", 100, 1, new ArrayList<Mark>() {
                            {
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                            }
                        }, Entrant.Status.FREE), false
                },

                {
                        new Entrant("Ivan1234", "Ivanov", 100, 1, new ArrayList<Mark>() {
                            {
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                            }
                        }, Entrant.Status.FREE), false
                },

                {
                        new Entrant("ivan123", "Ivanov", 100, 1, new ArrayList<Mark>() {
                            {
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                            }
                        }, Entrant.Status.FREE), false
                },

                {
                        new Entrant("1234", "Ivanov", 100, 1, new ArrayList<Mark>() {
                            {
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                            }
                        }, Entrant.Status.FREE), false
                },

                {
                        new Entrant("Ivan", "ivanov", 100, 1, new ArrayList<Mark>() {
                            {
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                            }
                        }, Entrant.Status.FREE), false
                },

                {
                        new Entrant("ivan", "ivanov1234", 100, 1, new ArrayList<Mark>() {
                            {
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                            }
                        }, Entrant.Status.FREE), false
                },

                {
                        new Entrant("Ivan", "Ivanov1234", 100, 1, new ArrayList<Mark>() {
                            {
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                            }
                        }, Entrant.Status.FREE), false
                },

                {
                        new Entrant("Ivan", "1234", 100, 1, new ArrayList<Mark>() {
                            {
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                            }
                        }, Entrant.Status.FREE), false
                },

                {
                        new Entrant("Ivan", "Ivanov", 20, 1, new ArrayList<Mark>() {
                            {
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                                this.add(new Mark(1, 100));
                            }
                        }, Entrant.Status.FREE), false
                },

                {
                        new Entrant("Ivan", "Ivanov", 40, 1, new ArrayList<Mark>() {
                            {
                                this.add(new Mark(1, 19));
                                this.add(new Mark(1, 30));
                                this.add(new Mark(1, 40));
                            }
                        }, Entrant.Status.FREE), false
                },
        };
    }


    @Test(dataProvider = "testEntrantValidation")
    public void test(Entrant actual, boolean expected) throws ServiceException {
        ArrayList<Integer> marks = new ArrayList<>();
        for (Mark mark : actual.getMarks()) {
            marks.add(mark.getValue());
        }
        Assert.assertEquals(EntrantValidation.validateEntrantParameters(actual.getEntrantFirstName(), actual.getEntrantSurName(), String.valueOf(actual.getCertificate()),marks ),
                expected);
    }
}
