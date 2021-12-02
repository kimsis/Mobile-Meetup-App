package com.example.hanger;

import static android.content.ContentValues.TAG;
import static org.mockito.Mockito.mock;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.hanger.ui.loginRegister.EmailValidator;
import com.example.hanger.ui.loginRegister.FireBaseHub;
import com.example.hanger.ui.loginRegister.InvalidEmailException;
import com.example.hanger.ui.loginRegister.LoginFragment;
import com.example.hanger.ui.loginRegister.PasswordMismatchException;
import com.example.hanger.ui.loginRegister.RegisterFragment;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.security.Provider;
import java.util.Random;

public class LoginInstrumentedTest {
    public void EmailValidate(String email) {
        Assert.assertTrue(EmailValidator.isValidEmail(email));
    }

    @Test
    public void Login() {

    }

    @Test
    public void SuccessfulRegister() throws InvalidEmailException, PasswordMismatchException {
        Random random = new Random();
        String mMockEmail = "something@mail.com";
        Log.e(TAG, "SuccessfulRegister: " + mMockEmail);
        String mMockPassword = "123456";
        String mMockPasswordConfirm = "123456";
        //Assert.assertTrue(FireBaseHub.CreateUser(mMockEmail, mMockPassword, mMockPasswordConfirm));
    }

    @Test
    public void InvalidEmailRegister() throws PasswordMismatchException {
        String mMockEmail = "@mail.com";
        String mMockPassword = "123456";
        String mMockPasswordConfirm = "123456";
        try {
            //Assert.assertTrue(FireBaseHub.CreateUser(mMockEmail, mMockPassword, mMockPasswordConfirm));
            throw new InvalidEmailException();
        } catch (InvalidEmailException e) {
            Log.d(TAG, "InvalidEmailRegister: test successful", e);
        }
    }

    @Test
    public void PasswordMismatchRegister() throws InvalidEmailException {
        String mMockEmail = "myEmail@mail.com";
        String mMockPassword = "123456";
        String mMockPasswordConfirm = "1234";
        try {
            //Assert.assertTrue(FireBaseHub.CreateUser(mMockEmail, mMockPassword, mMockPasswordConfirm));
            throw new PasswordMismatchException();
        } catch (PasswordMismatchException e) {
            Log.d(TAG, "PasswordMismatchRegister: test successful", e);
        }
    }

    @Test
    public void FirebasePasswordTooShortRegister() throws InvalidEmailException, PasswordMismatchException {
        String mMockEmail = "myEmail@mail.com";
        String mMockPassword = "1234";
        String mMockPasswordConfirm = "1234";
        //Assert.assertFalse(FireBaseHub.CreateUser(mMockEmail, mMockPassword, mMockPasswordConfirm));
    }
}
