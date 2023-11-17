package ru.netology.test;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.LoginPage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;
import static ru.netology.data.SQLHelper.cleanDatabase;

public class BankLoginTest {


    @BeforeEach
    void setup() {
        var loginPage = Selenide.open("http://localhost:9999", LoginPage.class);
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @AfterAll
    static void teardown() {
        cleanDatabase();
    }

    @Test
    void shouldSuccessfulLogin() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    @Test
    void shouldGetErrorNotificationIfLoginAndPasswordAreNotIncludeInDB() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);
        var authInfo = DataHelper.generateRandomUser();
        loginPage.login(authInfo);
        loginPage.verifyErrorNotificationVisibility();
    }

    @Test
    void shouldGetErrorIfIncorrectVerificationCode() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotificationVisibility();
    }

    @Test
    void shouldGetErrorIfLoginIsCorrectButPasswordIsIncorrect() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);
        var correctLogin = DataHelper.getAuthInfoWithTestData().getLogin();
        var incorrectPassword = DataHelper.generateRandomUser().getPassword();
        loginPage.login(new DataHelper.AuthInfo(correctLogin, incorrectPassword));
        loginPage.verifyErrorNotificationVisibility();
    }
}
