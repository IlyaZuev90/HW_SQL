package ru.netology.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.LoginPage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;
import static ru.netology.data.SQLHelper.cleanDatabase;

public class BankLoginTest {

//    @AfterAll
//    static void tearDown() {
//        cleanDatabase();
//    }

    @Test
    @DisplayName("Should successfully login to dashboard with exist login and password from sut test data")
    void shouldSuccessfulLogin() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    @Test
    @DisplayName("Should get Error notification if login valid and password invalid")
    void shouldGetErrorNotificationIfLoginValidButPasswordInvalid() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);
        var validLogin = DataHelper.getAuthInfoWithTestData().getLogin();
        var invalidPassword = DataHelper.generateRandomUser().getPassword();
        loginPage.login(new DataHelper.AuthInfo(validLogin, invalidPassword));
        loginPage.verifyErrorNotificationVisibility();
    }

    @Test
    @DisplayName("Should get Error notification if user doesn't exist in the base")
    void shouldGetErrorNotificationIfLoginAndPasswordNotExistInDB() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);
        var authInfo = DataHelper.generateRandomUser();
        loginPage.login(authInfo);
        loginPage.verifyErrorNotificationVisibility();
    }

    @Test
    @DisplayName("Should get Error notification if verification code invalid")
    void shouldGetErrorNotificationIfVerificationCodeInvalid() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotificationVisibility();
    }

    @Test
    @DisplayName("Should get Error and User blocked if log in attempts with invalid password failed three times in a row")
    void shouldGetErrorAndUserBlockedIfUsedInvalidPasswordThreeTimes() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);
        var validLogin = DataHelper.getAuthInfoWithTestData().getLogin();
        for (int i = 0; i < 3; i++) {
            var invalidPassword = DataHelper.generateRandomUser().getPassword();
            loginPage.login(new DataHelper.AuthInfo(validLogin, invalidPassword));
            loginPage.verifyErrorNotificationVisibility();
        }
        var authInfo = DataHelper.getAuthInfoWithTestData();
//        var verifyPage = loginPage.validLogin(authInfo);
//        verifyPage.verifyErrorNotificationVisibility();
        loginPage.login(authInfo);

        var userStatus = SQLHelper.getUserStatus();
        Assertions.assertEquals("blocked", userStatus);
    }

    @Test
    @DisplayName("Should get user's log-in options blocked if log-in attempts with invalid password failed three times in a row")
    void shouldDisablePageFunctionsIfUsedInvalidPasswordThreeTimes() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);

        var validLogin = DataHelper.getAuthInfoWithTestData().getLogin();

        if (DataHelper.getAuthInfoWithTestData().getLogin().equals("vasya")) {
            if (SQLHelper.getUserStatus().equals("active")) {
                for (int i = 0; i < 3; i++) {
                    var invalidPassword = DataHelper.generateRandomUser().getPassword();
                    loginPage.login(new DataHelper.AuthInfo(validLogin, invalidPassword));
                    loginPage.verifyErrorNotificationVisibility();
                }
            }
            loginPage.entryError();
        }
    }
}
