package at.ac.tuwien.big.testsuite.impl.selenium;

/*
 * Copyright 2013 BIG TU Wien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;
import java.util.List;
import org.hamcrest.Matcher;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 *
 * @author Christian, Florian Zoubek
 */
public class RegisterTest extends BaseSeleniumTest {

    private String username = "asd";
    private String password = "asd123";
    private String lastname;
    private String firstname;
    private String birthdate;
    private int sex;
    private String cUsername;
    private String cPassword;
    private static String[] BIRTHDAY_IDS = new String[]{"dateofbirth", "birthdate", "birthday"};
    private static String[] ID_PREFIXES = new String[]{"form:", ""};

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Before
    @Override
    public void init() throws Exception {
        super.init();

        if (exists(By.id("register"))) {
            clickNonAjax(By.id("register"));
        } else if (exists(By.id("navForm:register"))) {
            clickNonAjax(By.id("navForm:register"));
        } else {
            assertFalse(TestsuiteConstants.KNOWN_ERROR_PREFIX + " The register link does not exist", true);
        }

        waitForJQuery();
    }

    @Test
    public void testValidRegistration_shouldNotFail() {
        exportCurrentHTML(driver, "register-new.html");

        lastname = "Asd";
        firstname = "user";
        birthdate = "01.01.2000";
        sex = 0;

        doRegistration(firstname, lastname, birthdate, sex, username, password);
        String paramString = paramsToString(firstname, lastname, birthdate, sex, username, password);
        checkMessages("No firstname error message expected, but an error is listed. " + paramString, "firstname", isEmptyOrNullString());
        checkMessages("No lastname error message expected, but an error is listed. " + paramString, "lastname", isEmptyOrNullString());
        checkMessages("No date of birth error message expected, but an error is listed. " + paramString, BIRTHDAY_IDS, isEmptyOrNullString());
        checkMessages("No sex error message expected, but an error is listed. " + paramString, "sex", isEmptyOrNullString());
        checkMessages("No username error message expected, but an error is listed. " + paramString, "username", isEmptyOrNullString());
        checkMessages("No password error message expected, but an error is listed. " + paramString, "password", isEmptyOrNullString());
        exportCurrentHTML(driver, "register-success.html");
    }

    @Test
    public void testRequiredElements_shouldAllFail() {
        lastname = "";
        firstname = "";
        birthdate = "";
        sex = 0;
        cUsername = "";
        cPassword = "";

        doRegistration(firstname, lastname, birthdate, sex, cUsername, cPassword);
        String paramString = paramsToString(firstname, lastname, birthdate, sex, cUsername, cPassword);
        checkMessages("Expected firstname error message, but no error is listed. " + paramString, "firstname", not(isEmptyOrNullString()));
        checkMessages("Expected lastname error message, but no error is listed. " + paramString, "lastname", not(isEmptyOrNullString()));
        checkMessages("Expected date of birth error message, but no error is listed. " + paramString, BIRTHDAY_IDS, not(isEmptyOrNullString()));
        checkMessages("No sex error message expected, but an error is listed. " + paramString, "sex", isEmptyOrNullString());
        checkMessages("Expected username error message, but no error is listed. " + paramString, "username", not(isEmptyOrNullString()));
        checkMessages("Expected password error message, but no error is listed. " + paramString, "password", not(isEmptyOrNullString()));
        exportCurrentHTML(driver, "register-fail-all.html");
    }

    @Test
    public void testInvalidFirstName_shouldFail() {
        lastname = "Asd";
        firstname = "user1";
        birthdate = "01.01.2000";
        sex = 1;
        cUsername = username + "invalidfirstname";
        cPassword = password + "invalidfirstname";

        doRegistration(firstname, lastname, birthdate, sex, cUsername, cPassword);
        String paramString = paramsToString(firstname, lastname, birthdate, sex, cUsername, cPassword);
        checkMessages("Expected firstname error message, but no error is listed. " + paramString, "firstname", not(isEmptyOrNullString()));
    }

    @Test
    public void testInvalidLastName_shouldFail() {
        lastname = "Asd";
        firstname = "user";
        lastname = "Asd1";
        birthdate = "01.01.2000";
        sex = 0;
        cUsername = username + "invalidLastname";
        cPassword = password + "invalidLastname";

        doRegistration(firstname, lastname, birthdate, sex, cUsername, cPassword);
        String paramString = paramsToString(firstname, lastname, birthdate, sex, cUsername, cPassword);
        checkMessages("Expected lastname error message, but no error is listed. " + paramString, "lastname", not(isEmptyOrNullString()));
    }

    @Test
    public void testInvalidBirthDateInvalidDayMonth_shouldFail() {
        lastname = "Asd";
        firstname = "user";
        birthdate = "00.00.2000";
        sex = 1;
        cUsername = username + "invalidBirthdayDayMonth";
        cPassword = password + "invalidBirthdayDayMonth";

        doRegistration(firstname, lastname, birthdate, sex, cUsername, cPassword);
        String paramString = paramsToString(firstname, lastname, birthdate, sex, cUsername, cPassword);
        checkMessages("Expected date of birth error message, but no error is listed. " + paramString, BIRTHDAY_IDS, not(isEmptyOrNullString()));
    }

    @Test
    public void testInvalidBirthDateInvalidDay_shouldFail() {
        lastname = "Asd";
        firstname = "user";
        birthdate = "70.01.2000";
        sex = 0;
        cUsername = username + "invalidBirthdayDay";
        cPassword = password + "invalidBirthdayDay";

        doRegistration(firstname, lastname, birthdate, sex, cUsername, cPassword);
        String paramString = paramsToString(firstname, lastname, birthdate, sex, cUsername, cPassword);
        checkMessages("Expected date of birth error message, but no error is listed. " + paramString, BIRTHDAY_IDS, not(isEmptyOrNullString()));
    }

    @Test
    public void testInvalidBirthDateInvalidMonth_shouldFail() {
        lastname = "Asd";
        firstname = "user";
        birthdate = "01.70.2000";
        sex = 1;
        cUsername = username + "invalidBirthdayMonth";
        cPassword = password + "invalidBirthdayMonth";

        doRegistration(firstname, lastname, birthdate, sex, cUsername, cPassword);
        String paramString = paramsToString(firstname, lastname, birthdate, sex, cUsername, cPassword);
        checkMessages("Expected date of birth error message, but no error is listed. " + paramString, BIRTHDAY_IDS, not(isEmptyOrNullString()));
    }

    @Test
    public void testInvalidBirthDateText_shouldFail() {
        lastname = "Asd";
        firstname = "user";
        birthdate = "some date";
        sex = 0;
        cUsername = username + "invalidBirthdayDayText";
        cPassword = password + "invalidBirthdayDayText";

        doRegistration(firstname, lastname, birthdate, sex, cUsername, cPassword);
        String paramString = paramsToString(firstname, lastname, birthdate, sex, cUsername, cPassword);
        checkMessages("Expected date of birth error message, but no error is listed. " + paramString, BIRTHDAY_IDS, not(isEmptyOrNullString()));
    }

    @Test
    public void testInvalidBirthDatePatternMismatch_shouldFail() {
        lastname = "Asd";
        firstname = "user";
        birthdate = "2000.01.01";
        sex = 1;
        cUsername = username + "invalidBirthdayPatternMismatch";
        cPassword = password + "invalidBirthdayPatternMismatch";

        doRegistration(firstname, lastname, birthdate, sex, cUsername, cPassword);
        String paramString = paramsToString(firstname, lastname, birthdate, sex, cUsername, cPassword);
        checkMessages("Expected date of birth error message, but no error is listed. " + paramString, BIRTHDAY_IDS, not(isEmptyOrNullString()));
    }

    @Test
    public void testInvalidUsername_shouldFail() {
        lastname = "Asd";
        firstname = "user";
        birthdate = "01.01.2000";
        sex = 0;
        cUsername = username + " 1234";
        cPassword = password + "invalidUsername";

        doRegistration(firstname, lastname, birthdate, sex, cUsername, cPassword);
        String paramString = paramsToString(firstname, lastname, birthdate, sex, cUsername, cPassword);
        checkMessages("Expected username error message, but no error is listed. " + paramString, "username", not(isEmptyOrNullString()));
    }

    @Test
    public void testInvalidPasswordOnlyChars_shouldFail() {
        lastname = "Asd";
        firstname = "user";
        birthdate = "01.01.2000";
        sex = 1;
        cUsername = username + "invalidPasswordOnlyChars";
        cPassword = "letmein";

        doRegistration(firstname, lastname, birthdate, sex, cUsername, cPassword);
        String paramString = paramsToString(firstname, lastname, birthdate, sex, cUsername, cPassword);
        checkMessages("Expected password error message, but no error is listed. " + paramString, "password", not(isEmptyOrNullString()));
    }

    @Test
    public void testInvalidPasswordOnlyNumbers_shouldFail() {
        lastname = "Asd";
        firstname = "user";
        birthdate = "01.01.2000";
        sex = 0;
        cUsername = username + "invalidPasswordOnlyNumbers";
        cPassword = "1234";

        doRegistration(firstname, lastname, birthdate, sex, cUsername, cPassword);
        String paramString = paramsToString(firstname, lastname, birthdate, sex, cUsername, cPassword);
        checkMessages("Expected password error message, but no error is listed. " + paramString, "password", not(isEmptyOrNullString()));
    }

    @Test
    public void testInvalidPasswordTooShort_shouldFail() {
        lastname = "Asd";
        firstname = "user";
        birthdate = "01.01.2000";
        sex = 1;
        cUsername = username + "invalidPasswordTooShort";
        cPassword = "1";

        doRegistration(firstname, lastname, birthdate, sex, cUsername, cPassword);
        String paramString = paramsToString(firstname, lastname, birthdate, sex, cUsername, cPassword);
        checkMessages("Expected password error message, but no error is listed. " + paramString, "password", not(isEmptyOrNullString()));
    }

    @Test
    public void testTermsAndConditions_shouldPass() {
        String termsId = getPrefixedId("terms");
        checkThat("The terms and conditions are visible before the checkbox has been clicked.", driver.findElement(By.id(termsId)).getText(), allOf(not(containsString("Play at least once a day!")), not(containsString("Spiele zumindest einmal am Tag!"))));
        String showtermsId = getPrefixedId("showterms");
        driver.findElement(By.id(showtermsId)).click();
        wait(200);
        waitForJQuery(driver);
        checkThat("The terms and conditions are not visible after the checkbox has been clicked.", driver.findElement(By.id(termsId)).getText(), anyOf(containsString("Play at least once a day!"), containsString("Spiele zumindest einmal am Tag!")));
        exportCurrentHTML(driver, "register-terms-and-conditions.html");
    }

    /**
     * Fills out the registration form, submit it and wait for the response
     *
     * @param firstname
     * @param lastName
     * @param birthdate
     * @param sex
     * @param username
     * @param password
     */
    private void doRegistration(String firstname, String lastName, String birthdate, int sex, String username, String password) {
        String firstnameId = getPrefixedId("firstname");
        driver.findElement(By.id(firstnameId)).sendKeys(firstname);
        String lastnameId = getPrefixedId("lastname");
        driver.findElement(By.id(lastnameId)).sendKeys(lastName);

        String birthdateId = ID_PREFIXES[0] + BIRTHDAY_IDS[0];
        for (String bId : BIRTHDAY_IDS) {
            for (String tmpPrefix : ID_PREFIXES) {
                if (exists(By.id(tmpPrefix + bId))) {
                    birthdateId = tmpPrefix + bId;
                    break;
                }
            }
        }
        driver.findElement(By.id(birthdateId)).sendKeys(birthdate);
        
        String sexId = getPrefixedId("sex");
        Select dropDown = new Select(driver.findElement(By.id(sexId)));
        dropDown.selectByIndex(sex);
        
        String usernameId = getPrefixedId("username");
        driver.findElement(By.id(usernameId)).sendKeys(username);
        String passwordId = getPrefixedId("password");
        driver.findElement(By.id(passwordId)).sendKeys(password);
        String submitId = getPrefixedId("submit");
        driver.findElement(By.id(submitId)).click();
        wait(200);
        waitForJQuery(driver);
    }

    /**
     * Creates a summary of the parameters
     *
     * @param firstname
     * @param lastName
     * @param birthdate
     * @param sex
     * @param username
     * @param password
     * @return a summary of the given parameters
     */
    private static String paramsToString(String firstname, String lastName, String birthdate, int sex, String username, String password) {
        return "(firstname: " + firstname + ", lastname: " + lastName + ", birthdate: " + birthdate + ", sex(index): " + sex + ", username: " + username + ", password: " + password + ")";
    }

    /**
     * Performs the given matcher check with the text of the WebElement with the
     * given id and stores the result in the error collector. If no element is
     * found with the given Id, the check is performed against null.
     *
     * @param message the message to be used if the check fails
     * @param elementId the id of the WebElement to check
     * @param matcher the matcher to be used in the check
     */
    private void checkMessages(String message, String elementId, Matcher<String> matcher) {
        checkMessages(message, new String[]{elementId}, matcher);
    }

    /**
     * Performs the given matcher check with the text of the WebElement with the
     * given id and stores the result in the error collector. If no element is
     * found with the given Id, the check is performed against null.
     *
     * @param message the message to be used if the check fails
     * @param elementIds the possible ids of the WebElement to check
     * @param matcher the matcher to be used in the check
     */
    private void checkMessages(String message, String[] elementIds, Matcher<String> matcher) {
        String elementId = elementIds[0];
        for (String tmp : elementIds) {
            for (String tmpPrefix : ID_PREFIXES) {
                if (exists(By.id(tmpPrefix + tmp + "_messages"))) {
                    elementId = tmpPrefix + tmp;
                    break;
                }
            }
        }
        WebElement messagesElement = null;
        List<WebElement> elements = driver.findElements(By.id(elementId + "_messages"));
        if (!elements.isEmpty()) {
            messagesElement = elements.get(0);
        }
        String text = messagesElement == null ? null : messagesElement.getText();
        checkThat(message, text, matcher);
    }

    /**
     * returns the id with the used prefix
     *
     * @param id
     */
    private String getPrefixedId(String id) {
        String elementId = ID_PREFIXES[0] + id;
        for (String tmpPrefix : ID_PREFIXES) {
            if (exists(By.id(tmpPrefix + id))) {
                elementId = tmpPrefix + id;
                return elementId;
            }
        }
        return elementId;
    }
}