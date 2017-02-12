package c4stor.com.feheroes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

public class FEHAnalyserActivity extends AppCompatActivity {

    private Map<String, JsonHero> fiveStarsMap = new TreeMap<String, JsonHero>();
    private Map<String, JsonHero> fourStarsMap = new TreeMap<String, JsonHero>();

    private Map<String, JsonHero> refMap = fiveStarsMap;

    private void initHeroesMap(int resource, Map<String, JsonHero> heroMap) throws IOException {
        InputStream inputStream = getBaseContext().getResources().openRawResource(resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        while (line != null) {
            JsonHero jH = gson.fromJson(line, JsonHero.class);

            heroMap.put(jH.name, jH);
            line = reader.readLine();
        }
    }

    private Gson gson = new Gson();

    private void initFiveStarsMapFromJson() throws IOException {
        initHeroesMap(R.raw.fivestarjson, fiveStarsMap);
    }

    private void initFourStarsMap() throws IOException {
        initHeroesMap(R.raw.fourstarjson, fourStarsMap);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initFiveStarsMapFromJson();
            initFourStarsMap();
        } catch (IOException e) {
        }
        setContentView(R.layout.activity_roller);
        final Spinner spinnerHeroes = (Spinner) findViewById(R.id.spinner_heroes);
        spinnerHeroes.setOnItemSelectedListener(new HeroSpinnerListener());

        Spinner spinnerStars = (Spinner) findViewById(R.id.spinner_stars);
        spinnerStars.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        String heroSelected = (String)spinnerHeroes.getSelectedItem();
                        String[] newValues = fiveStarsMap.keySet().toArray(new String[]{});
                        int newPosition = getNewPosition(heroSelected, newValues);
                        ArrayAdapter a = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_item, newValues);
                        a.notifyDataSetChanged();
                        spinnerHeroes.setAdapter(a);
                        spinnerHeroes.setSelection(newPosition);
                        refMap = fiveStarsMap;
                        break;
                    case 1:
                        String heroSelectedb = (String)spinnerHeroes.getSelectedItem();
                        String[] newValuesb = fourStarsMap.keySet().toArray(new String[]{});
                        int newPositionb = getNewPosition(heroSelectedb, newValuesb);
                        ArrayAdapter b = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_item, newValuesb);
                        b.notifyDataSetChanged();
                        spinnerHeroes.setAdapter(b);
                        spinnerHeroes.setSelection(newPositionb);
                        refMap = fourStarsMap;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        populateSpinner(R.id.spinner_stars, R.array.stars_array);
        adAdBanner();
    }

    private int getNewPosition(String selectedValue, String[] values) {
        for(int i=0; i< values.length; i++){
            if(values[i].equalsIgnoreCase(selectedValue)){
                return i;
            }
        }
        return 0;
    }


    private void adAdBanner() {
        PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
        PublisherAdRequest.Builder b = new PublisherAdRequest.Builder();
        PublisherAdRequest adRequest = b.build();
        mPublisherAdView.loadAd(adRequest);
    }

    private void populateSpinner(int id, int resource_id) {
        Spinner spinner = (Spinner) findViewById(id);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                resource_id, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    private class HeroSpinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            Object item = parent.getItemAtPosition(position);
            JsonHero selectHero = refMap.get((String) item);

            FEHAnalyserActivity.this.populateTableWithHero(selectHero);

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }

    }

    private void populateTableWithHero(JsonHero hero) {
        TableLayout tv = (TableLayout) findViewById(R.id.herotable);
        tv.removeAllViewsInLayout();

        TableRow headers = new TableRow(FEHAnalyserActivity.this);

        headers.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        headers.setPadding(0, 0, 0, 10);

        TextView attributeHeader = new TextView(FEHAnalyserActivity.this);
        attributeHeader.setText("Attribute");
        attributeHeader.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        attributeHeader.setTextSize(25);
        attributeHeader.setGravity(Gravity.LEFT);
        headers.addView(attributeHeader);

        TextView lvl1Header = new TextView(FEHAnalyserActivity.this);
        lvl1Header.setText("Level 1");
        lvl1Header.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        lvl1Header.setTextSize(25);
        lvl1Header.setGravity(Gravity.CENTER_HORIZONTAL);
        headers.addView(lvl1Header);

        TextView level40Header = new TextView(FEHAnalyserActivity.this);
        level40Header.setPadding(5, 0, 0, 0);
        level40Header.setTextSize(25);
        level40Header.setText("Level 40");
        level40Header.setGravity(Gravity.CENTER_HORIZONTAL);
        level40Header.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        headers.addView(level40Header);


        final View vline = new View(FEHAnalyserActivity.this);
        vline.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 2));
        vline.setBackgroundColor(getResources().getColor(R.color.colorPrimary));


        TableRow trHP = makeTableRow(getBaseContext().getResources().getString(R.string.hp), hero.HP);
        TableRow trMght = makeTableRow(getBaseContext().getResources().getString(R.string.atk), hero.atk);
        TableRow trSpd = makeTableRow(getBaseContext().getResources().getString(R.string.spd), hero.speed);
        TableRow trDef = makeTableRow(getBaseContext().getResources().getString(R.string.def), hero.def);
        TableRow trRes = makeTableRow(getBaseContext().getResources().getString(R.string.res), hero.res);
        tv.addView(headers);
        tv.addView(vline);

        TableRow blankRow = new TableRow(FEHAnalyserActivity.this);

        blankRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        blankRow.setPadding(0, 0, 0, 20);

        tv.addView(blankRow);

        tv.addView(trHP);
        tv.addView(trMght);
        tv.addView(trSpd);
        tv.addView(trDef);
        tv.addView(trRes);
    }

    TableRow makeTableRow(String attribute, int[] attributeValues) {
        TableRow tr = new TableRow(FEHAnalyserActivity.this);

        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        tr.setPadding(12, 0, 0, 12);


        TextView b = new TextView(FEHAnalyserActivity.this);
        b.setText(attribute);
        b.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        b.setTextSize(22);
        tr.addView(b);

        Spinner lvl1Spinner = new Spinner(FEHAnalyserActivity.this);
        lvl1Spinner.setPadding(5, 0, 0, 0);

        lvl1Spinner.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1));
        lvl1Spinner.setAdapter(new Level1StatSpinnerAdapter(attributeValues));
        lvl1Spinner.setSelection(1);

        final TextView lvl40Value = new TextView(FEHAnalyserActivity.this);
        lvl40Value.setPadding(5, 0, 0, 0);
        lvl40Value.setTextSize(22);
        lvl40Value.setGravity(Gravity.CENTER_HORIZONTAL);
        lvl40Value.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1));

        lvl40Value.setText(renderLvl40(attributeValues[4]) + "");

        lvl1Spinner.setOnItemSelectedListener(new SpinnerTextViewChanger(lvl40Value, attributeValues));
        tr.addView(lvl1Spinner);
        tr.addView(lvl40Value);

        return tr;
    }

    private String renderLvl40(int value){
        if(value < 0 || refMap==fourStarsMap)
            return "?";
        else
            return value+"";
    }

    public class SpinnerTextViewChanger implements AdapterView.OnItemSelectedListener {

        private TextView tv;
        private int[] attributeValues;

        public SpinnerTextViewChanger(TextView tv, int attributeValues[]) {
            this.tv = tv;
            this.attributeValues = attributeValues;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            tv.setText(renderLvl40(attributeValues[5-position]));
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
            if(tv.getText()=="?")
                color=parent.getContext().getResources().getColor(R.color.divider);

            tv.setTextColor(color);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
