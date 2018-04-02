package ar.com.tzulberti.archerytraining.activities.bow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import ar.com.tzulberti.archerytraining.R;
import ar.com.tzulberti.archerytraining.activities.common.BaseArcheryTrainingActivity;
import ar.com.tzulberti.archerytraining.model.bow.Bow;
import ar.com.tzulberti.archerytraining.model.bow.SightDistanceValue;


/**
 * Created by tzulberti on 6/12/17.
 */

public class ViewBowActivity extends BaseArcheryTrainingActivity implements View.OnClickListener{

    public static final String BOW_ARGUMENT_KEY = "bow";

    private EditText bowNameText;
    private TableLayout bowDistances;
    private Bow existingBow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bow_view_details);


        this.bowDistances = (TableLayout) findViewById(R.id.bow_distances);
        this.bowNameText = (EditText) findViewById(R.id.bow_name);

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(BOW_ARGUMENT_KEY)) {
            this.existingBow = (Bow) intent.getSerializableExtra(BOW_ARGUMENT_KEY);
            this.renderBowInformation();
        } else {
            this.existingBow = null;
        }
    }

    private void renderBowInformation() {
        this.bowNameText.setText(this.existingBow.name);
        for (SightDistanceValue sightDistanceValue : this.existingBow.sightDistanceValues) {
            this.addSightDistanceValue(sightDistanceValue.sightValue, sightDistanceValue.distance, this);
        }
    }

    private void addSightDistanceValue(float sightValue, int distance, Context context) {
        TableRow tr = new TableRow(context);
        EditText distanceEditText = new EditText(context);
        distanceEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (distance > -1) {
            distanceEditText.setText(String.valueOf(distance));
        }

        EditText sightValueEditText = new EditText(context);
        sightValueEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (sightValue > -1) {
            sightValueEditText.setText(String.valueOf(sightValue));
        }

        Button deleteDistanceInfo = new Button(context);
        deleteDistanceInfo.setText(this.getString(R.string.bow_distance_delete));
        deleteDistanceInfo.setOnClickListener(this);

        tr.addView(distanceEditText);
        tr.addView(sightValueEditText);
        tr.addView(deleteDistanceInfo);

        this.bowDistances.addView(tr);
    }

    /**
     * Updates or create a bow with all the information on the user
     */
    private void saveData() {
        // Make sure that everything is valid
        Bow newBow = new Bow();
        newBow.name = this.bowNameText.getText().toString();
        newBow.sightDistanceValues = new ArrayList<>();

        String requiredValue = this.getString(R.string.commonRequiredValidationError);
        if (StringUtils.isBlank(newBow.name)) {
            this.bowNameText.setError(requiredValue);
            return;
        }

        // ignore the first row because it is the table header
        for (int i = 1; i < this.bowDistances.getChildCount(); i++) {
            TableRow tr = (TableRow) this.bowDistances.getChildAt(i);

            EditText distanceEditText = (EditText) tr.getChildAt(0);
            EditText sightValueText = (EditText) tr.getChildAt(1);

            String distanceValue = distanceEditText.getText().toString();
            String sightValue = sightValueText.getText().toString();

            if (StringUtils.isBlank(distanceValue) && StringUtils.isBlank(sightValue)) {
                // an empty line on the table so ignore it
                continue;
            } else if (! StringUtils.isBlank(distanceValue) && StringUtils.isBlank(sightValue)) {
                sightValueText.setError(requiredValue);
                return ;
            } else if (StringUtils.isBlank(distanceValue) && ! StringUtils.isBlank(sightValue)) {
                distanceEditText.setError(requiredValue);
                return ;
            } else {
                SightDistanceValue sightDistanceValue = new SightDistanceValue();
                newBow.sightDistanceValues.add(sightDistanceValue);
                sightDistanceValue.bow = newBow;
                sightDistanceValue.sightValue = Float.valueOf(sightValue);
                sightDistanceValue.distance = Integer.valueOf(distanceValue);
            }
        }

        // delete the existing values and set the new ones
        if (this.existingBow != null) {
            this.bowDAO.deleteBow(this.existingBow.id);
        }

        this.bowDAO.createBow(newBow.name, newBow.sightDistanceValues);


        Intent intent = new Intent(this, ViewExistingBowsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_distance) {
            this.addSightDistanceValue(-1, -1, this);

        } else if (v.getId() == R.id.btn_bow_save) {
            this.saveData();
        } else {
            // the user selected to delete the row with the sight value for a given distance
            TableRow tr = (TableRow) v.getParent();
            this.bowDistances.removeView(tr);
        }
    }
}
