package test.pishchalova.committee;

import com.pishchalova.committee.util.EncryptPassword;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EncodingPasswordTest {

    @Test
    public void testEncryptPasswordNegative() {
        String actual = EncryptPassword.encryptPass("Swedxzaq14");
        String expected = "f2a47c6809d88e175dade0ef7b16aa13";
        Assert.assertNotEquals(actual, expected);
    }

    @Test
    public void testEncryptPasswordPositive() {
        String actual = EncryptPassword.encryptPass("Swedxzaq14");
        String expected = "f73ffa4aa9ae34d38b1b7a6b87a7156b";
        Assert.assertEquals(actual, expected);
    }

}