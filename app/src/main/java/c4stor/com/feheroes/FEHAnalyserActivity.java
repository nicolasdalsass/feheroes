package c4stor.com.feheroes;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FEHAnalyserActivity extends ToolbaredActivity {

    private static boolean webSyncDone = false;

    Map<String, Hero> fiveStarsMap = new TreeMap<String, Hero>();
    Map<String, Hero> fourStarsMap = new TreeMap<String, Hero>();
    Map<String, Hero> refMap = fiveStarsMap;
    int[] selectedSpinners = new int[]{1, 1, 1, 1, 1};

    private HeroCollection collection = new HeroCollection();

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    private Gson gson = new Gson();

    private void cleanStat(int[] stat) {
        if (stat[4] == -1 || stat[5] == -1 || stat[3] == -1) {
            stat[4] = -1;
            stat[5] = -1;
            stat[3] = -1;
        }
    }

    private void initHeroData() throws IOException {
        File dataFile = new File(getBaseContext().getFilesDir(), "hero.data");
        if (dataFile.exists()) {
            try {
                initFromInputStream(new FileInputStream(dataFile));
            } catch (Exception e) {
                initFromCombo();
            }
        } else {
            initFromCombo();
        }

//        File wikiFile = new File(getBaseContext().getFilesDir(),"wiki.data");
//        if(wikiFile.exists()){
//            WikiParser parser = new WikiParser(getBaseContext());
//            parser.parse(wikiFile);
//            parser.enrichMap(fourStarsMap, parser.fourStarsMap);
//            parser.enrichMap(fiveStarsMap, parser.fiveStarsMap);
//        }
    }

    private void initFromCombo() throws IOException {
        InputStream inputStream = getBaseContext().getResources().openRawResource(R.raw.combojson);
        initFromInputStream(inputStream);
    }

    private void initFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        while (line != null) {
            Hero jH = gson.fromJson(line, Hero.class);
            int level = Integer.valueOf(jH.name.substring(jH.name.length() - 1));
            jH.name = jH.name.substring(0, jH.name.length() - 1);
            cleanStat(jH.atk);
            cleanStat(jH.HP);
            cleanStat(jH.def);
            cleanStat(jH.res);
            cleanStat(jH.speed);
            int nameIdentifier = this.getResources().getIdentifier(jH.name.toLowerCase(), "string", getPackageName());
            switch (level) {
                case 4:
                    fourStarsMap.put(capitalize(getResources().getString(nameIdentifier)), jH);
                case 5:
                    fiveStarsMap.put(capitalize(getResources().getString(nameIdentifier)), jH);
            }
            line = reader.readLine();
        }
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_roller;
    }

    @Override
    protected boolean isIVFinder() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onResume();

    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            initHeroData();
        } catch (IOException e) {
        }
        collection = HeroCollection.loadFromStorage(getBaseContext());


        final Spinner spinnerHeroes = (Spinner) findViewById(R.id.spinner_heroes);
        spinnerHeroes.setOnItemSelectedListener(new HeroSpinnerListener());

        Spinner spinnerStars = (Spinner) findViewById(R.id.spinner_stars);
        spinnerStars.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        String heroSelected = "";
                        if (spinnerHeroes.getSelectedItem() != null)
                            heroSelected = ((Hero) spinnerHeroes.getSelectedItem()).name;
                        Hero[] fiveStarsValues = fiveStarsMap.values().toArray(new Hero[]{});
                        int newPosition = getNewPosition(heroSelected, fiveStarsValues);
                        ArrayAdapter fiveStarsAdapater = new SpinnerHeroesAdapter(getBaseContext(), fiveStarsValues);
                        fiveStarsAdapater.notifyDataSetChanged();
                        spinnerHeroes.setAdapter(fiveStarsAdapater);
                        spinnerHeroes.setSelection(newPosition);
                        refMap = fiveStarsMap;
                        break;
                    case 1:
                        String heroSelectedb = "";
                        if (spinnerHeroes.getSelectedItem() != null)
                            heroSelectedb = ((Hero) spinnerHeroes.getSelectedItem()).name;
                        Hero[] fourStarsValues = fourStarsMap.values().toArray(new Hero[]{});
                        int newPositionb = getNewPosition(heroSelectedb, fourStarsValues);
                        ArrayAdapter fourStarAdapter = new SpinnerHeroesAdapter(getBaseContext(), fourStarsValues);
                        fourStarAdapter.notifyDataSetChanged();
                        spinnerHeroes.setAdapter(fourStarAdapter);
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

    private int getNewPosition(String selectedValue, Hero[] values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].name.equalsIgnoreCase(selectedValue)) {
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
        if (refMap == fourStarsMap)
            spinner.setSelection(1);
        else
            spinner.setSelection(0);
    }


    private class HeroSpinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            Hero selectedHero = (Hero) parent.getItemAtPosition(position);
            FEHAnalyserActivity.this.populateTableWithHero(selectedHero);

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }

    }

    private void populateTableWithHero(final Hero hero) {
        TableLayout tv = (TableLayout) findViewById(R.id.herotable);
        tv.removeAllViewsInLayout();

        TableRow headers = new TableRow(FEHAnalyserActivity.this);

        headers.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        headers.setPadding(0, 0, 0, 10);

        TextView attributeHeader = new TextView(FEHAnalyserActivity.this);
        attributeHeader.setText(getResources().getString(R.string.attributeheader));
        attributeHeader.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        attributeHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        attributeHeader.setGravity(Gravity.LEFT);
        headers.addView(attributeHeader);

        TextView lvl1Header = new TextView(FEHAnalyserActivity.this);
        lvl1Header.setText(getResources().getString(R.string.lvl1));
        lvl1Header.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        lvl1Header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        lvl1Header.setGravity(Gravity.CENTER_HORIZONTAL);
        headers.addView(lvl1Header);

        TextView level40Header = new TextView(FEHAnalyserActivity.this);
        level40Header.setPadding(5, 0, 0, 0);
        level40Header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        level40Header.setText(getResources().getString(R.string.lvl40));
        level40Header.setGravity(Gravity.CENTER_HORIZONTAL);
        level40Header.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        headers.addView(level40Header);


        final View vline = new View(FEHAnalyserActivity.this);
        vline.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
        vline.setBackgroundColor(getResources().getColor(R.color.colorPrimary));


        final HeroTableRow trHP = makeTableRow(getBaseContext().getResources().getString(R.string.hp), selectedSpinners, 0, hero.HP);
        final HeroTableRow trMght = makeTableRow(getBaseContext().getResources().getString(R.string.atk), selectedSpinners, 1, hero.atk);
        final HeroTableRow trSpd = makeTableRow(getBaseContext().getResources().getString(R.string.spd), selectedSpinners, 2, hero.speed);
        final HeroTableRow trDef = makeTableRow(getBaseContext().getResources().getString(R.string.def), selectedSpinners, 3, hero.def);
        final HeroTableRow trRes = makeTableRow(getBaseContext().getResources().getString(R.string.res), selectedSpinners, 4, hero.res);
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

        Button addButton = (Button) findViewById(R.id.addToCollectionBtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hero h = (Hero) ((Spinner) findViewById(R.id.spinner_heroes)).getSelectedItem();
                List<String> boons = new ArrayList<String>();
                List<String> banes = new ArrayList<String>();
                if (trDef.selectedPos == 0)
                    boons.add(trDef.attribute);
                if (trHP.selectedPos == 0)
                    boons.add(trHP.attribute);
                if (trRes.selectedPos == 0)
                    boons.add(trRes.attribute);
                if (trSpd.selectedPos == 0)
                    boons.add(trSpd.attribute);
                if (trMght.selectedPos == 0)
                    boons.add(trMght.attribute);
                if (trDef.selectedPos == 2)
                    banes.add(trDef.attribute);
                if (trHP.selectedPos == 2)
                    banes.add(trHP.attribute);
                if (trRes.selectedPos == 2)
                    banes.add(trRes.attribute);
                if (trSpd.selectedPos == 2)
                    banes.add(trSpd.attribute);
                if (trMght.selectedPos == 2)
                    banes.add(trMght.attribute);
                int stars = 5 - ((Spinner) findViewById(R.id.spinner_stars)).getSelectedItemPosition();
                HeroRoll hr = new HeroRoll(hero, stars, boons, banes);
                collection.add(hr);
                collection.save(getBaseContext());
                Toast.makeText(getBaseContext(), hero.name + " " + getBaseContext().getString(R.string.addedtocollection), Toast.LENGTH_SHORT).show();
            }
        });

    }

    HeroTableRow makeTableRow(String attribute, int[] selectedSpinners, int spinnerPos, int[] attributeValues) {
        return new HeroTableRow(FEHAnalyserActivity.this, selectedSpinners, spinnerPos, attribute, attributeValues);
    }
}
