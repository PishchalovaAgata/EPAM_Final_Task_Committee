package test.pishchalova.committee.validation;

import com.pishchalova.committee.entity.Subject;
import com.pishchalova.committee.entity.User;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.validation.SubjectValidation;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SubjectValidationTest {

    @DataProvider
    public Object[][] testUserValidation() {
        return new Object[][]{
                {new Subject(0, "Subject", false), true},
                {new Subject(0, "subject", false), false},
                {new Subject(0, "Subject1", false), false},
                {new Subject(0, "1", false), false},

        };
    }

    @Test(dataProvider = "testUserValidation")
    public void test(Subject actual, boolean expected) throws ServiceException {
        Assert.assertEquals(SubjectValidation.validateSubjectParameters(actual.getName()),
                expected);
    }
}
