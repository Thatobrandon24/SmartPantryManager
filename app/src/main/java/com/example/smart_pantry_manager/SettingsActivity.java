package com.example.smart_pantry_manager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private EditText editProfileName;
    private EditText editProfileEmail;
    private Spinner spinnerMeasurement;
    private Spinner spinnerExpiryDays;
    private Spinner spinnerSortOrder;
    private Switch switchExpiryReminders;
    private SharedPreferences preferences;

    private static final String PREFS_NAME =
            "smart_pantry_settings";
    private static final String KEY_PROFILE_NAME =
            "profile_name";
    private static final String KEY_PROFILE_EMAIL =
            "profile_email";
    private static final String KEY_MEASUREMENT =
            "measurement";
    private static final String KEY_REMINDERS =
            "expiry_reminders";
    private static final String KEY_EXPIRY_DAYS =
            "expiry_days";
    private static final String KEY_SORT_ORDER =
            "sort_order";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Profile fields to enter details
        editProfileName = findViewById(R.id.editProfileName);
        editProfileEmail = findViewById(R.id.editProfileEmail);

        // the preference controls
        spinnerMeasurement =
                findViewById(R.id.spinnerMeasurement);
        spinnerExpiryDays =
                findViewById(R.id.spinnerExpiryDays);
        spinnerSortOrder =
                findViewById(R.id.spinnerSortOrder);
        switchExpiryReminders =
                findViewById(R.id.switchExpiryReminders);


        Button btnSaveSettings =
                findViewById(R.id.btnSaveSettings);

        Button btnBackHome =
                findViewById(R.id.btnBackHome);

        // Persistent preferences
        preferences = getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        );

        loadSettings();

        btnSaveSettings.setOnClickListener(
                view -> saveSettings()
        );

        btnBackHome.setOnClickListener(
                view -> finish()
        );
    }

    private void loadSettings() {

        // to load profile
        editProfileName.setText(
                preferences.getString(
                        KEY_PROFILE_NAME, ""
                )
        );

        editProfileEmail.setText(
                preferences.getString(
                        KEY_PROFILE_EMAIL, ""
                )
        );

        // to load measurement preference
        int measurement = preferences.getInt(
                KEY_MEASUREMENT, 0
        );

        spinnerMeasurement.setSelection(
                validPosition(measurement, 2)
        );

        // to load expiry reminder preference
        boolean reminders = preferences.getBoolean(
                KEY_REMINDERS, true
        );

        switchExpiryReminders.setChecked(reminders);

        // to load warning days
        int expiryDays = preferences.getInt(
                KEY_EXPIRY_DAYS, 3
        );

        int expiryPosition;

        switch (expiryDays) {
            case 1:
                expiryPosition = 0;
                break;

            case 5:
                expiryPosition = 2;
                break;

            case 7:
                expiryPosition = 3;
                break;

            case 3:
            default:
                expiryPosition = 1;
                break;
        }

        spinnerExpiryDays.setSelection(
                expiryPosition
        );

        // to load pantry sort order
        int sortOrder = preferences.getInt(
                KEY_SORT_ORDER, 0
        );

        spinnerSortOrder.setSelection(
                validPosition(sortOrder, 3)
        );
    }

    private int validPosition(
            int position,
            int optionCount
    ) {
        if (position < 0 || position >= optionCount) {
            return 0;
        }

        return position;
    }

    private void saveSettings() {

        // Read profile
        String profileName =
                editProfileName.getText()
                        .toString()
                        .trim();

        String profileEmail =
                editProfileEmail.getText()
                        .toString()
                        .trim();

        // to read measurement preference
        int measurement =
                spinnerMeasurement.getSelectedItemPosition();

        // to read expiry reminder preference
        boolean reminders =
                switchExpiryReminders.isChecked();

        int[] warningDays = {1, 3, 5, 7};

        int expiryPosition =
                spinnerExpiryDays.getSelectedItemPosition();

        int expiryDays = warningDays[
                validPosition(expiryPosition, warningDays.length)
                ];

        // to read sort order
        int sortOrder =
                spinnerSortOrder.getSelectedItemPosition();

        // to save all settings together
        boolean saved = preferences.edit()
                .putString(
                        KEY_PROFILE_NAME,
                        profileName
                )
                .putString(
                        KEY_PROFILE_EMAIL,
                        profileEmail
                )
                .putInt(
                        KEY_MEASUREMENT,
                        measurement
                )
                .putBoolean(
                        KEY_REMINDERS,
                        reminders
                )
                .putInt(
                        KEY_EXPIRY_DAYS,
                        expiryDays
                )
                .putInt(
                        KEY_SORT_ORDER,
                        sortOrder
                )
                .commit();

        if (saved) {
            Toast.makeText(
                    this,
                    "Settings saved successfully.",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    this,
                    "Unable to save settings.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
