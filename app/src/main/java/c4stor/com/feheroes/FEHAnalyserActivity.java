package c4stor.com.feheroes;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

public class FEHAnalyserActivity extends AppCompatActivity {

    private Map<String, FEHero> fiveStarsMap = new TreeMap<String, FEHero>();

    private void initMap() {
        fiveStarsMap.put("Azura", new FEHero("Azura", 17, 17, 7, 4, 6, 36, 43, 36, 21, 28));
        fiveStarsMap.put("Camilla", new FEHero("Camilla", 18, 16, 3, 6, 7, 37, 38, 27, 28, 31));
        fiveStarsMap.put("Catria", new FEHero("Catria", 17, 18, 10, 7, 6, 39, 42, 34, 29, 25));
        fiveStarsMap.put("Cordelia", new FEHero("Cordelia", 17, 18, 4, 5, 6, 40, 43, 30, 22, 25));
        fiveStarsMap.put("Corrin (male)", new FEHero("CorrinM", 20, 24, 8, 6, 5, 48, 45, 35, 34, 24));
        fiveStarsMap.put("Effie", new FEHero("Effie", 22, 27, 5, 11, 4, 50, 55, 22, 33, 23));
        fiveStarsMap.put("Elise", new FEHero("Elise", 15, 8, 8, 4, 8, 33, 45, 32, 22, 35));
        fiveStarsMap.put("Fae", new FEHero("Fae", 16, 18, 4, 6, 8, 46, 46, 28, 26, 30));
        fiveStarsMap.put("Hawkeye", new FEHero("Hawkeye", 21, 20, 5, 6, 6, 48, 50, 22, 31, 33));
        fiveStarsMap.put("Hector", new FEHero("Hector", 24, 26, 5, 11, 4, 52, 52, 24, 37, 19));
        fiveStarsMap.put("Hinoka", new FEHero("Hinoka", 19, 15, 3, 6, 7, 41, 43, 27, 25, 24));
        fiveStarsMap.put("Jeorge", new FEHero("Jeorge", 18, 22, 7, 5, 5, 37, 46, 32, 24, 22));
        fiveStarsMap.put("Kagero", new FEHero("Kagero", 16, 14, 8, 5, 6, 31, 40, 32, 22, 28));
        fiveStarsMap.put("Leo", new FEHero("Leo", 17, 21, 5, 6, 8, 39, 43, 22, 25, 30));
        fiveStarsMap.put("Lilina", new FEHero("Lilina", 16, 23, 6, 4, 9, 35, 53, 25, 19, 31));
        fiveStarsMap.put("Linde", new FEHero("Linde", 16, 23, 10, 4, 5, 35, 49, 39, 14, 26));
        fiveStarsMap.put("Lucina", new FEHero("Lucina", 19, 24, 10, 6, 4, 43, 50, 36, 25, 19));
        fiveStarsMap.put("Lyn", new FEHero("Lyn", 18, 22, 11, 7, 5, 37, 44, 37, 26, 29));
        fiveStarsMap.put("Marth", new FEHero("Marth", 19, 23, 8, 7, 6, 41, 47, 34, 29, 23));
        fiveStarsMap.put("Merric", new FEHero("Merric", 25, 21, 8, 6, 4, 48, 40, 32, 28, 19));
        fiveStarsMap.put("Minerva", new FEHero("Minerva", 18, 23, 9, 8, 5, 40, 52, 38, 27, 20));
        fiveStarsMap.put("Nowi", new FEHero("Nowi", 17, 17, 5, 6, 5, 45, 42, 27, 33, 30));
        fiveStarsMap.put("Peri", new FEHero("Peri", 16, 20, 9, 6, 6, 35, 44, 33, 23, 33));
        fiveStarsMap.put("Robin (male)", new FEHero("Robin (male)", 18, 18, 7, 7, 5, 40, 40, 29, 29, 22));
        fiveStarsMap.put("Roy", new FEHero("Roy", 20, 24, 9, 6, 4, 44, 46, 31, 25, 28));
        fiveStarsMap.put("Ryoma", new FEHero("Ryoma", 19, 24, 11, 5, 4, 41, 53, 35, 27, 21));
        fiveStarsMap.put("Sheena", new FEHero("Sheena", 21, 19, 6, 12, 7, 45, 41, 25, 36, 33));
        fiveStarsMap.put("Takumi", new FEHero("Takumi", 18, 22, 7, 6, 5, 40, 46, 33, 25, 18));
        fiveStarsMap.put("Tharja", new FEHero("Tharja", 17, 21, 8, 6, 5, 39, 45, 34, 23, 20));
        fiveStarsMap.put("Young Tiki", new FEHero("Young Tiki", 15, 20, 4, 8, 7, 41, 46, 30, 22, 29));
    }

    private void initFiveStarsMap() throws IOException {
        InputStream inputStream = getBaseContext().getResources().openRawResource(R.raw.fivestarheroes);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        while (line != null) {
            FEHero hero = parseFromString(line);
            fiveStarsMap.put(hero.name, hero);
            line = reader.readLine();
        }
    }

    private FEHero parseFromString(String heroLine) {
        String[] values = heroLine.split(",");
        return new FEHero(values[0],
                Integer.valueOf(values[1]),
                Integer.valueOf(values[2]),
                Integer.valueOf(values[3]),
                Integer.valueOf(values[4]),
                Integer.valueOf(values[5]),
                Integer.valueOf(values[6]),
                Integer.valueOf(values[7]),
                Integer.valueOf(values[8]),
                Integer.valueOf(values[9]),
                Integer.valueOf(values[10]));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            initFiveStarsMap();
        } catch (IOException e){
            initMap();
        }
        setContentView(R.layout.activity_roller);
        Spinner spinnerHeroes = (Spinner) findViewById(R.id.spinner_heroes);
        spinnerHeroes.setOnItemSelectedListener(new HeroSpinnerListener());

        spinnerHeroes.setAdapter(new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_dropdown_item,fiveStarsMap.keySet().toArray(new String[]{})));
        populateSpinner(R.id.spinner_stars, R.array.stars_array);
        adAdBanner();
    }

    private void adAdBanner(){
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
            FEHero selectHero = fiveStarsMap.get((String) item);

            FEHAnalyserActivity.this.populateTableWithHero(selectHero);

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }

    }

    private void populateTableWithHero(FEHero hero) {
        TableLayout tv = (TableLayout) findViewById(R.id.herotable);
        tv.removeAllViewsInLayout();

        TableRow headers = new TableRow(FEHAnalyserActivity.this);

        headers.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));


        TextView attributeHeader = new TextView(FEHAnalyserActivity.this);
        attributeHeader.setText("Attribute");
        attributeHeader.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        attributeHeader.setTextSize(15);
        attributeHeader.setGravity(Gravity.LEFT);
        headers.addView(attributeHeader);

        TextView lvl1Header = new TextView(FEHAnalyserActivity.this);
        lvl1Header.setText("Level 1");
        lvl1Header.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        lvl1Header.setTextSize(15);
        lvl1Header.setGravity(Gravity.CENTER_HORIZONTAL);
        headers.addView(lvl1Header);

        TextView level40Header = new TextView(FEHAnalyserActivity.this);
        level40Header.setPadding(10, 0, 0, 0);
        level40Header.setTextSize(15);
        level40Header.setText("Level 40");
        level40Header.setGravity(Gravity.CENTER_HORIZONTAL);
        level40Header.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        headers.addView(level40Header);


        final View vline = new View(FEHAnalyserActivity.this);
        vline.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 2));
        vline.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tv.addView(vline);

        TableRow trHP = makeTableRow(getBaseContext().getResources().getString(R.string.hp), hero.HP, hero.HP40);
        TableRow trMght = makeTableRow(getBaseContext().getResources().getString(R.string.atk), hero.Atk, hero.Atk40);
        TableRow trSpd = makeTableRow(getBaseContext().getResources().getString(R.string.spd), hero.Spd, hero.Spd40);
        TableRow trDef = makeTableRow(getBaseContext().getResources().getString(R.string.def), hero.Def, hero.Def40);
        TableRow trRes = makeTableRow(getBaseContext().getResources().getString(R.string.res), hero.Res, hero.Res40);
        tv.addView(headers);
        tv.addView(trHP);
        tv.addView(trMght);
        tv.addView(trSpd);
        tv.addView(trDef);
        tv.addView(trRes);
        final View vline1 = new View(FEHAnalyserActivity.this);
        vline1.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1));
        vline1.setBackgroundColor(Color.WHITE);
        tv.addView(vline1);  // add line below each row
    }

    TableRow makeTableRow(String attribute, int lvl1, final int lvl40) {
        TableRow tr = new TableRow(FEHAnalyserActivity.this);

        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));


        TextView b = new TextView(FEHAnalyserActivity.this);
        b.setText(attribute);
        b.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        b.setTextSize(15);
        tr.addView(b);

        Spinner lvl1Spinner = new Spinner(FEHAnalyserActivity.this);
                lvl1Spinner.setPadding(10, 0, 0, 0);

        lvl1Spinner.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1));
        lvl1Spinner.setAdapter(new Level1StatSpinnerAdapter(lvl1));
        lvl1Spinner.setSelection(1);

        final TextView lvl40Value = new TextView(FEHAnalyserActivity.this);
        lvl40Value.setPadding(10, 0, 0, 0);
        lvl40Value.setTextSize(15);
        lvl40Value.setGravity(Gravity.CENTER_HORIZONTAL);
        lvl40Value.setLayoutParams(new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1));

        lvl40Value.setText(lvl40+"");

        lvl1Spinner.setOnItemSelectedListener(new SpinnerTextViewChanger(lvl40Value, lvl40));
        tr.addView(lvl1Spinner);
        tr.addView(lvl40Value);

        return tr;
    }

    public class SpinnerTextViewChanger implements AdapterView.OnItemSelectedListener {

        private TextView tv;
        private int lvl40;

        public SpinnerTextViewChanger(TextView tv, int lvl40) {
            this.tv = tv;
            this.lvl40 = lvl40;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            tv.setText((lvl40 + 3 * (position - 1)) + "");
            int color = 0;
            switch (position) {
                case 0:
                    color = parent.getContext().getResources().getColor(R.color.secondary_text);
                    break;
                case 1:
                    color = parent.getContext().getResources().getColor(R.color.colorPrimary);
                    break;
                case 2:
                    color = parent.getContext().getResources().getColor(R.color.colorAccent);
                    break;
            }

            tv.setTextColor(color);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
