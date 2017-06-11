package c4stor.com.feheroes.activities.ivcheck;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import c4stor.com.feheroes.R;
import c4stor.com.feheroes.activities.ToolbaredActivity;
import c4stor.com.feheroes.model.Hero;
import c4stor.com.feheroes.model.HeroCollection;
import c4stor.com.feheroes.model.HeroRoll;

public class IVCheckActivity extends ToolbaredActivity {


    protected Map<String, Hero> refMap = new TreeMap<>();
    private int[] selectedSpinners = new int[]{1, 1, 1, 1, 1};
    private int[] neutralSpinners = new int[]{1, 1, 1, 1, 1};
    private boolean resetSpinners = false;
    private static boolean nakedHeroes = false;

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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.app_name);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.icons));
        setSupportActionBar(myToolbar);
        onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbarmenu, menu);
        MenuItem nakedView = menu.findItem(R.id.toggleNakedView);
        if (nakedHeroes) {
            nakedView.setTitle(R.string.no_skills);
            nakedView.getIcon().setAlpha(130);
        } else {
            nakedView.setTitle(R.string.skills_on);
            nakedView.getIcon().setAlpha(255);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggleNakedView:
                nakedHeroes = !nakedHeroes;
                invalidateOptionsMenu();
                Hero hero = (Hero) ((Spinner) findViewById(R.id.spinner_heroes)).getSelectedItem();
                populateTableWithHero(hero);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        collection = HeroCollection.loadFromStorage(getBaseContext());


        final Spinner spinnerHeroes = (Spinner) findViewById(R.id.spinner_heroes);
        spinnerHeroes.setOnItemSelectedListener(new HeroSpinnerListener());

        Spinner spinnerStars = (Spinner) findViewById(R.id.spinner_stars);
        spinnerStars.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            resetSpinners = false;
            switch (position) {
                case 0:
                    refMap = fiveStarsMap;
                    break;
                case 1:
                    refMap = fourStarsMap;
                    break;
                case 2:
                    refMap = threeStarsMap;
                    break;
            }
            String selectedHero = "";
            if (spinnerHeroes.getSelectedItem() != null) {
                selectedHero = ((Hero) spinnerHeroes.getSelectedItem()).name;
            }
            Hero[] refStarValues = refMap.values().toArray(new Hero[]{});
            int newPositionc = getNewPosition(selectedHero, refStarValues);
            ArrayAdapter threeStarsAdapter = new SpinnerHeroesAdapter(IVCheckActivity.this, refStarValues);
            threeStarsAdapter.notifyDataSetChanged();
            spinnerHeroes.setAdapter(threeStarsAdapter);
            spinnerHeroes.setSelection(newPositionc);
        }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        populateSpinner();

        adAdBanner();
        //disableAdBanner();
    }

    private int getNewPosition(String selectedValue, Hero[] values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].name.equalsIgnoreCase(selectedValue)) {
                return i;
            }
        }
        return 0;
    }

    private void populateSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_stars);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.stars_array, R.layout.spinneritem_nopadding);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (refMap == threeStarsMap) {
            spinner.setSelection(2);
        } else if (refMap == fourStarsMap) {
            spinner.setSelection(1);
        } else {
            spinner.setSelection(0);
        }
    }


    private class HeroSpinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            Hero selectedHero = (Hero) parent.getItemAtPosition(position);
            if (resetSpinners)
                selectedSpinners = neutralSpinners.clone();
            resetSpinners = true;
            IVCheckActivity.this.populateTableWithHero(selectedHero);

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }

    }

    private void populateTableWithHero(final Hero hero) {
        TableLayout tv = (TableLayout) findViewById(R.id.herotable);
        tv.removeAllViewsInLayout();

        tv.addView(createHeaders());
        tv.addView(createLine());
        tv.addView(createPaddingRow());

        int[] lvl1mods = calculateMods(hero, 1, nakedHeroes);
        int[] lvl40mods = calculateMods(hero, 40, nakedHeroes);
        final HeroTableRow trHP = makeTableRow(getBaseContext().getResources().getString(R.string.hp), selectedSpinners, 0, hero.HP, lvl1mods[0], lvl40mods[0]);
        final HeroTableRow trMght = makeTableRow(getBaseContext().getResources().getString(R.string.atk), selectedSpinners, 1, hero.atk, lvl1mods[1], lvl40mods[1]);
        final HeroTableRow trSpd = makeTableRow(getBaseContext().getResources().getString(R.string.spd), selectedSpinners, 2, hero.speed, lvl1mods[2], lvl40mods[2]);
        final HeroTableRow trDef = makeTableRow(getBaseContext().getResources().getString(R.string.def), selectedSpinners, 3, hero.def, lvl1mods[3], lvl40mods[3]);
        final HeroTableRow trRes = makeTableRow(getBaseContext().getResources().getString(R.string.res), selectedSpinners, 4, hero.res, lvl1mods[4], lvl40mods[4]);
        tv.addView(trHP);
        tv.addView(trMght);
        tv.addView(trSpd);
        tv.addView(trDef);
        tv.addView(trRes);

        if (!nakedHeroes) {
            tv.addView(createSkillsRow(hero));
        }
        Button addHeroButton = (Button) findViewById(R.id.addToCollectionBtn);
        addHeroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hero h = (Hero) ((Spinner) findViewById(R.id.spinner_heroes)).getSelectedItem();
                int nameIdentifier = getBaseContext().getResources().getIdentifier(hero.name.toLowerCase(), "string", getBaseContext().getPackageName());
                String localizedName = capitalize(getBaseContext().getString(nameIdentifier));

                List<String> boons = new ArrayList<>();
                List<String> banes = new ArrayList<>();
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
                HeroRoll hr = new HeroRoll(h, stars, boons, banes);
                collection.add(hr);
                collection.save(getBaseContext());
                Toast.makeText(getBaseContext(), localizedName + " " + getBaseContext().getString(R.string.addedtocollection), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @NonNull
    private TableRow createSkillsRow(Hero hero) {
        TableRow skillsRow = (TableRow) View.inflate(this, R.layout.skillsrow, null);
        TextView skillsLabel = (TextView) skillsRow.findViewById(R.id.skillsLabel);
        LinearLayout skills1 = (LinearLayout) skillsRow.findViewById(R.id.skills1);
        LinearLayout skills40 = (LinearLayout) skillsRow.findViewById(R.id.skills40);
        skillsLabel.setText(R.string.skills);

        updateSkillView(skills1, hero.skills1);
        updateSkillView(skills40, hero.skills40);

        skillsRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        skillsRow.setPadding(3, 0, 0, 18);
        return skillsRow;
    }

    @NonNull
    private View createLine() {
        final View vline = new View(IVCheckActivity.this);
        vline.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
        vline.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        vline.setPadding(0, 0, 0, 10);
        return vline;
    }

    @NonNull
    private TableRow createPaddingRow() {
        TableRow blankRow = new TableRow(IVCheckActivity.this);

        blankRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        return blankRow;
    }

    @NonNull
    private TableRow createHeaders() {
        TableRow headers = (TableRow) View.inflate(this, R.layout.herotitlerow, null);

        headers.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        headers.setPadding(0, 10, 0, 4);

        TextView attributeHeader = (TextView) headers.findViewById(R.id.herotitlelabel);
        attributeHeader.setText(getResources().getString(R.string.attributeheader));

        TextView lvl1Header = (TextView) headers.findViewById(R.id.title1);
        lvl1Header.setText(getResources().getString(R.string.lvl1));

        TextView level40Header = (TextView) headers.findViewById(R.id.title40);
        level40Header.setText(getResources().getString(R.string.lvl40));
        return headers;
    }

    HeroTableRow makeTableRow(String attribute, int[] selectedSpinners, int spinnerPos, int[] attributeValues, int lvl1mod, int lvl40mod) {
        return new HeroTableRow(IVCheckActivity.this, selectedSpinners, spinnerPos, attribute, attributeValues, lvl1mod, lvl40mod);
    }
}
