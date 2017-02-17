package c4stor.com.feheroes;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by Nicolas on 15/02/2017.
 */

public class HeroTableRow extends TableRow {

    public int selectedPos = 1;
    public FEHAnalyserActivity parentActivity;
    public String attribute;
    int[] selectedSpinners;
    int spinnerPos;

    public HeroTableRow(FEHAnalyserActivity parentActivity, int[] selectedSpinners, int spinnerPos,  String attribute, int[] attributeValues){
        super(parentActivity);
        this.parentActivity = parentActivity;
        this.attribute = attribute;
        this.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        this.setPadding(3, 0, 0, 9);
        this.selectedSpinners=selectedSpinners;
        this.spinnerPos=spinnerPos;


        TextView b = new TextView(parentActivity);
        b.setText(attribute);
        b.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        this.addView(b);

        Spinner lvl1Spinner = new Spinner(parentActivity);
        lvl1Spinner.setPadding(5, 0, 0, 0);

        lvl1Spinner.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1));
        lvl1Spinner.setAdapter(new Level1StatSpinnerAdapter(attributeValues));
        lvl1Spinner.setSelection(selectedSpinners[spinnerPos]);

        final TextView lvl40Value = new TextView(parentActivity);
        lvl40Value.setPadding(5, 0, 0, 0);
        lvl40Value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        lvl40Value.setGravity(Gravity.CENTER_HORIZONTAL);
        lvl40Value.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1));

        lvl40Value.setText(renderLvl40(attributeValues[4]) + "");

        lvl1Spinner.setOnItemSelectedListener(new SpinnerTextViewChanger(lvl40Value, attributeValues, this));
        this.addView(lvl1Spinner);
        this.addView(lvl40Value);
    }

    private String renderLvl40(int value) {
        if (value < 0 ||
                parentActivity.refMap == parentActivity.fourStarsMap)
            return "?";
        else
            return value + "";
    }

    public class SpinnerTextViewChanger implements AdapterView.OnItemSelectedListener {

        private TextView tv;
        private int[] attributeValues;
        private HeroTableRow parentRow;

        public SpinnerTextViewChanger(TextView tv, int attributeValues[], HeroTableRow parentRow) {
            this.tv = tv;
            this.attributeValues = attributeValues;
            this.parentRow = parentRow;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            tv.setText(renderLvl40(attributeValues[5 - position]));
            int color = 0;
            switch (position) {
                case 0:
                    color = parent.getContext().getResources().getColor(R.color.high_green);
                    break;
                case 1:
                    color = parent.getContext().getResources().getColor(R.color.colorPrimary);
                    break;
                case 2:
                    color = parent.getContext().getResources().getColor(R.color.low_red);
                    break;
            }
            if (tv.getText() == "?")
                color = parent.getContext().getResources().getColor(R.color.divider);
            parentRow.selectedPos=position;
            selectedSpinners[spinnerPos] = position;

            tv.setTextColor(color);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


}
