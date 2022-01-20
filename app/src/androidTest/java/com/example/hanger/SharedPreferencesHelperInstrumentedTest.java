package com.example.hanger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import android.content.SharedPreferences;
import java.util.Calendar;

import com.example.hanger.ui.settings.SharedPreferenceEntry;
import com.example.hanger.ui.settings.SharedPreferencesHelper;


/**
 * Unit tests for the {@link SharedPreferencesHelper} that mocks {@link SharedPreferences}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SharedPreferencesHelperInstrumentedTest {

    private static final String TEST_DISTANCE_TYPE = "Km.";

    private static final float TEST_DISTANCE_AMOUNT = 3.0f;

    private static final float TEST_THEME_THRESHOLD = 50.5f;

    private SharedPreferenceEntry mSharedPreferenceEntry;

    private SharedPreferencesHelper mMockSharedPreferencesHelper;

    private SharedPreferencesHelper mMockBrokenSharedPreferencesHelper;

    @Mock
    SharedPreferences mMockSharedPreferences;

    @Mock
    SharedPreferences mMockBrokenSharedPreferences;

    @Mock
    SharedPreferences.Editor mMockEditor;

    @Mock
    SharedPreferences.Editor mMockBrokenEditor;

    @Before
    public void initMocks() {
        // Create SharedPreferenceEntry to persist.
        mSharedPreferenceEntry = new SharedPreferenceEntry(TEST_DISTANCE_TYPE, TEST_DISTANCE_AMOUNT, TEST_THEME_THRESHOLD);

        // Create a mocked SharedPreferences.
        mMockSharedPreferencesHelper = createMockSharedPreference();

        // Create a mocked SharedPreferences that fails at saving data.
        mMockBrokenSharedPreferencesHelper = createBrokenMockSharedPreference();
    }

    @Test
    public void sharedPreferencesHelper_SaveAndReadPersonalInformation() {
        // Save the personal information to SharedPreferences
        boolean success = mMockSharedPreferencesHelper.savePersonalInfo(mSharedPreferenceEntry);

        assertThat("Checking that SharedPreferenceEntry.save... returns true",
                success, is(true));

        // Read personal information from SharedPreferences
        SharedPreferenceEntry savedSharedPreferenceEntry =
                mMockSharedPreferencesHelper.getPersonalInfo();

        assertThat("Checking that SharedPreferenceEntry.name has been persisted and read correctly",
                mSharedPreferenceEntry.getDistanceType(),
                is(equalTo(savedSharedPreferenceEntry.getDistanceType())));
        assertThat("Checking that SharedPreferenceEntry.dateOfBirth has been persisted and read "
                        + "correctly",
                mSharedPreferenceEntry.getDistanceAmount(),
                is(equalTo(savedSharedPreferenceEntry.getDistanceAmount())));
        assertThat("Checking that SharedPreferenceEntry.email has been persisted and read "
                        + "correctly",
                mSharedPreferenceEntry.getThemeThreshold(),
                is(equalTo(savedSharedPreferenceEntry.getThemeThreshold())));
    }

    @Test
    public void sharedPreferencesHelper_SavePersonalInformationFailed_ReturnsFalse() {
        // Read personal information from a broken SharedPreferencesHelper
        boolean success =
                mMockBrokenSharedPreferencesHelper.savePersonalInfo(mSharedPreferenceEntry);
        assertThat("Makes sure writing to a broken SharedPreferencesHelper returns false", success,
                is(false));
    }

    /**
     * Creates a mocked SharedPreferences.
     */
    private SharedPreferencesHelper createMockSharedPreference() {
        // Mocking reading the SharedPreferences as if mMockSharedPreferences was previously written
        // correctly.
        when(mMockSharedPreferences.getString(eq(SharedPreferencesHelper.KEY_DISTANCE_TYPE), anyString()))
                .thenReturn(mSharedPreferenceEntry.getDistanceType());
        when(mMockSharedPreferences.getFloat(eq(SharedPreferencesHelper.KEY_DISTANCE_AMOUNT), anyFloat()))
                .thenReturn(mSharedPreferenceEntry.getDistanceAmount());
        when(mMockSharedPreferences.getFloat(eq(SharedPreferencesHelper.KEY_THEME_THRESHOLD), anyFloat()))
                .thenReturn(mSharedPreferenceEntry.getThemeThreshold());

        // Mocking a successful commit.
        when(mMockEditor.commit()).thenReturn(true);

        // Return the MockEditor when requesting it.
        when(mMockSharedPreferences.edit()).thenReturn(mMockEditor);
        return new SharedPreferencesHelper(mMockSharedPreferences);
    }

    /**
     * Creates a mocked SharedPreferences that fails when writing.
     */
    private SharedPreferencesHelper createBrokenMockSharedPreference() {
        // Mocking a commit that fails.
        when(mMockBrokenEditor.commit()).thenReturn(false);

        // Return the broken MockEditor when requesting it.
        when(mMockBrokenSharedPreferences.edit()).thenReturn(mMockBrokenEditor);
        return new SharedPreferencesHelper(mMockBrokenSharedPreferences);
    }
}
