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
import org.hamcrest.Matcher;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Christian, Florian Zoubek
 */
public class LoginTest extends BaseSeleniumTest {

    private String username = "asd";
    private String password = "asd123";

    private void checkMessages(String message, Matcher<String> matcher) {
        
        WebElement loginMessagesElement = null;
        
        if(exists(By.id("login_messages"))){
            loginMessagesElement = driver.findElement(By.id("login_messages"));
        }else if(exists(By.id("form:login_messages"))){
            loginMessagesElement = driver.findElement(By.id("form:login_messages"));
        }else if(exists(By.id("login:form:login_messages"))){
            loginMessagesElement = driver.findElement(By.id("login:form:login_messages"));
        }
        String text = loginMessagesElement == null ? null : loginMessagesElement.getText();

        checkThat(message, text, matcher);
    }

    public void doLogin(WebDriver driver, String username, String password) {
        checkThat("Login form element 'name' with id 'login:form:name' does not exist", true, is(exists(driver, By.id("login:form:name"))));
        checkThat("Login form element 'password' with id 'login:form:password' does not exist", true, is(exists(driver, By.id("login:form:password"))));
        checkThat("Login form element 'submit' with id 'login:form:submit' does not exist", true, is(exists(driver, By.id("login:form:submit"))));
        
        driver.findElement(By.id("login:form:name")).sendKeys(username);
        driver.findElement(By.id("login:form:password")).sendKeys(password);
        driver.findElement(By.id("login:form:submit")).click();
        wait(200);
        waitForJQuery(driver);
    }

    @Test
    public void testLogin_emptyFieldsShouldFail() throws Exception {
        checkMessages("No login message expected but there is one given", isEmptyOrNullString());
        doLogin(driver, "", "");
        checkMessages("Expected login failed message but none found", not(isEmptyOrNullString()));
    }

    @Test
    public void testLogin_shouldFail() throws Exception {
        checkMessages("No login message expected but there is one given", isEmptyOrNullString());
        doLogin(driver, "blabla", "blabla");
        checkMessages("Expected login failed message but none found", not(isEmptyOrNullString()));
        exportCurrentHTML(driver, "login-failed.html");
    }

    @Test
    public void testLogin_shouldBeSuccessful() throws Exception {
        exportCurrentHTML(driver, "login-new.html");
        checkMessages("No login message expected but there is one given", isEmptyOrNullString());
        doLogin(driver, username, password);

        // Logout
        if(exists(By.id("logout"))){
            driver.findElement(By.id("logout")).click();
        } else if(exists(By.id("navForm:logout"))){
            driver.findElement(By.id("navForm:logout")).click();
        } else {
            assertFalse("The logout link could not be found, either a login error occurred or the logout link has a different id than expected", true);
        }
        
        wait(200);
        waitForJQuery(driver);
        checkMessages("No login message expected but there is one given", isEmptyOrNullString());
        exportCurrentHTML(driver, "login-logged-out.html");
    }

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
}