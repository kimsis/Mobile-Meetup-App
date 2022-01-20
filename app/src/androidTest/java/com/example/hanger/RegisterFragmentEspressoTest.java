package com.example.hanger;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RegisterFragmentEspressoTest {

    @Rule
    public ActivityTestRule<LoginRegisterActivity> mActivityTestRule = new ActivityTestRule<>(LoginRegisterActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    // tests if register with an existing profile failed (still on register screen)
    @Test
    public void registerFragmentEspressoTest() {
        ViewInteraction materialTextView = onView(
                allOf(withId(R.id.tv_register_link), withText("Not yet registered?"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0),
                                2),
                        isDisplayed()));
        materialTextView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.et_email_register),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("test@tes.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.et_email_register), withText("test@tes.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.et_email_register), withText("test@tes.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("test@test.com"));

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.et_email_register), withText("test@test.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText4.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.et_email_register), withText("test@test.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText5.perform(click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.et_email_register), withText("test@test.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText6.perform(click());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.et_email_register), withText("test@test.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("yep@mope.com"));

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.et_email_register), withText("yep@mope.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText8.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.et_email_register), withText("yep@mope.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText9.perform(click());

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.et_email_register), withText("yep@mope.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText10.perform(replaceText("yep@nope.com"));

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.et_email_register), withText("yep@nope.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText11.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText12 = onView(
                allOf(withId(R.id.et_email_register), withText("yep@nope.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText12.perform(click());

        ViewInteraction appCompatEditText13 = onView(
                allOf(withId(R.id.et_email_register), withText("yep@nope.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText13.perform(click());

        ViewInteraction appCompatEditText14 = onView(
                allOf(withId(R.id.et_password_register),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText14.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction appCompatEditText15 = onView(
                allOf(withId(R.id.et_password_confirm),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText15.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.btn_register), withText("Register"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.cv_register),
                                        0),
                                3),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.btn_register), withText("REGISTER"),
                        withParent(withParent(withId(R.id.cv_register))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
