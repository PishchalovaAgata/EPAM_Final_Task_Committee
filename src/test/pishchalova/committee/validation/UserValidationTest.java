package test.pishchalova.committee.validation;

import com.pishchalova.committee.entity.User;
import com.pishchalova.committee.exception.ServiceException;
import com.pishchalova.committee.validation.UserValidation;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.HashSet;

public class UserValidationTest {

    @DataProvider
    public Object[][] testUserValidation() {
        return new Object[][]{
                {new User("user", "User1234", "wsdwr@mail.ru"), true},
                {new User("NewUser", "1111", "wsdwr@mail.ru"), false},
                {new User("NewUser", "User1234", "wsdwrmail.ru"), false},
                {new User("NEWUser", "User1234", "wsdwr.dsfghd.asfgh@mail.ru"), true},
                {new User("NewUser", "User1234", "wsdwr.dsfghd.asfgh@mail"), false},
                {new User("NewUser", "User1234", "wsdwr.dsfghd.asfgh@mail"), false},
                {new User("NewUser", "User1234", "wsdwr.dsfghd.asfgh@mail.comad"), false},
                {new User("11111111", "User1234", "wsdwr.dsfghd.asfgh@mail.com"), false},
                {new User("S111111", "User1234", "wsdwr.dsfghd.asfgh@mail.com"), true},
                {new User("SSS", "User1234", "wsdwr.dsfghd.asfgh@mail.com"), false},
                {new User("SSS", "User1234", "wsdwr.dsfghd.asfgh@mail.com"), false},
        };
    }

    @Test(dataProvider = "testUserValidation")
    public void test(User actual, boolean expected) throws ServiceException {
        Assert.assertEquals(UserValidation.validateUserParameters(actual.getLogin(), actual.getPassword(), actual.getEmail()),
                expected);
    }
}
