package ar.com.tzulberti.archerytraining.activities.timer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.common.BaseArcheryTrainingActivity;

/**
 * Created by tzulberti on 4/24/17.
 */
public class ConfigureTimerActivity extends BaseArcheryTrainingActivity {

    @Override
    protected boolean shouldShowHelp() {
        return true;
    }

    @Override
    protected String helpText() {
        return this.getString(R.string.timer_configure_help);
    }

    public static final String TIME = "ar.com.tzulberti.archerytraining.timer.time";
    public static final String CHANGE_AT_SECONDS = "ar.com.tzulberti.archerytraining.timer.change_at_seconds";
    public static final String START_IN = "ar.com.tzulberti.archerytraining.timer.startIn";
    public static final String VOICE_COUNT_DOWN = "ar.com.tzulberti.archerytraining.timer.voice_count_down";

    Map<String, EditText> inputMapping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_configure);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        EditText timeEditText = this.findViewById(R.id.timer_time);
        EditText changeAtSecondsEditText = this.findViewById(R.id.timer_change_at_seconds);
        EditText startInEditText = this.findViewById(R.id.timer_start_in);
        EditText startVoiceCountDownAt = this.findViewById(R.id.timer_voice_count_down);

        this.inputMapping = new HashMap<>();
        inputMapping.put(TIME, timeEditText);
        inputMapping.put(CHANGE_AT_SECONDS, changeAtSecondsEditText);
        inputMapping.put(START_IN, startInEditText);
        inputMapping.put(VOICE_COUNT_DOWN, startVoiceCountDownAt);

        for (Map.Entry<String, EditText> info : inputMapping.entrySet()) {
            int sharedPrefValue = sharedPref.getInt(info.getKey(), -1);
            if (sharedPrefValue >= 0) {
                info.getValue().setText(String.valueOf(sharedPrefValue));
            }
        }
    }

    public void startRetentions(View v) {

        Intent intent = new Intent(this, TimerActivity.class);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        boolean foundError = false;
        for (Map.Entry<String, EditText> info : this.inputMapping.entrySet()) {
            String inputValue = info.getValue().getText().toString();
            String errorMessagte = this.validateNumber(inputValue, 0, 300);
            if (! StringUtils.isBlank(errorMessagte)) {
                info.getValue().setError(errorMessagte);
                foundError = true;
                continue;
            }

            int value = Integer.parseInt(inputValue);
            editor.putInt(info.getKey(), value);
            intent.putExtra(info.getKey(), value);
        }

        if (foundError) {
            editor.clear();
            return;
        }

        editor.apply();
        startActivity(intent);
    }
}
