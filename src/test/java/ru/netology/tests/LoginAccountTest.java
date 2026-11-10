package ru.netology.tests;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.pages.AuthorizationPage;
import ru.netology.pages.DashboardPage;

public class LoginAccountTest {

    AuthorizationPage authorizationPage;
    DashboardPage dashboardPage;
    DataHelper.AuthInfo authInfo = DataHelper.getAuthInfoValid();

    @BeforeEach
    public void openSite() {
        this.authorizationPage = Selenide.open("http://localhost:9999", AuthorizationPage.class);
    }

    @AfterEach
    public void cleanAuthInfo() {
        SQLHelper.cleanAuthInfo();
    }

    @AfterAll
    public static void returnOriginalState() {
        SQLHelper.cleanDB();
    }

    @Test
    @DisplayName("Should successfully log in to the account")
    public void successfulLoginAccountTest() {
        var verificationField = authorizationPage.loginOfPersonalAccount(authInfo);
        verificationField.checkPageVisible();
        var verificationCode = DataHelper.getVerificationCodeValid(SQLHelper.getCode());
        dashboardPage = verificationField.verification(verificationCode);
        dashboardPage.checkPageVisible();
    }

    @Test
    @DisplayName("Should show error if incorrectly verification code")
    public void shouldShowErrorIfIncorrectlyVerificationCode() {
        var verificationCode = DataHelper.getVerificationCodeNoValid();
        var verificationField = authorizationPage.loginOfPersonalAccount(authInfo);
        dashboardPage = verificationField.verification(verificationCode);
        verificationField.checkPageVisible();
        verificationField.errorMessage("Ошибка! " + "Неверно указан код! Попробуйте ещё раз.");
    }

    @Test
    @DisplayName("Should blocked account if repeat 3 times password no valid ")
    public void shouldBlockedAccountIfRepeatPasswordNoValid() {
        authorizationPage.loginOfPersonalAccount(DataHelper.getAuthInfoNoValid());
        authorizationPage.errorMessage("Ошибка! " + "Неверно указан логин или пароль");
        authorizationPage.cleanField();
        authorizationPage.loginOfPersonalAccount(DataHelper.getAuthInfoNoValid());
        authorizationPage.errorMessage("Ошибка! " + "Неверно указан логин или пароль");
        authorizationPage.cleanField();
        authorizationPage.loginOfPersonalAccount(DataHelper.getAuthInfoNoValid());
        authorizationPage.errorMessage("Блокировка! " + "Неверно указан логин или пароль");
        authorizationPage.cleanField();
        var verificationField = authorizationPage.loginOfPersonalAccount(authInfo);
        verificationField.checkPageNotVisible();
    }

    @Test
    @DisplayName("Should show error if password no valid")
    public void shouldShowErrorIfPasswordNoValid() {
        authorizationPage.loginOfPersonalAccount(DataHelper.getAuthInfoNoValid());
        authorizationPage.checkPageVisible();
        authorizationPage.errorMessage("Ошибка! " + "Неверно указан логин или пароль");
    }
}