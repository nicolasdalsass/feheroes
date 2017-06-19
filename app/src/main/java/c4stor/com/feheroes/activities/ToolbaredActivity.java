package c4stor.com.feheroes.activities;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import c4stor.com.feheroes.R;
import c4stor.com.feheroes.activities.collection.CollectionActivity;
import c4stor.com.feheroes.activities.ivcheck.IVCheckActivity;
import c4stor.com.feheroes.model.hero.Hero;
import c4stor.com.feheroes.model.hero.HeroCollection;
import c4stor.com.feheroes.model.hero.HeroRoll;
import c4stor.com.feheroes.model.skill.Skill;

import static c4stor.com.feheroes.model.skill.Skill.*;

/**
 * Created by Nicolas on 19/02/2017.
 */

public abstract class ToolbaredActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    protected static Map<String, Hero> fiveStarsMap = null;
    protected static Map<String, Hero> fourStarsMap = null;
    protected static Map<String, Hero> threeStarsMap = null;
    protected static Map<Integer, Skill> skillsMap = null;


    protected HeroCollection collection = new HeroCollection();

    protected ModelSingleton singleton = ModelSingleton.getInstance();
    protected Gson gson = new Gson();

    public static final int WEAPON_TYPE = 0;
    public static final int ASSIST_TYPE = 1;
    public static final int SPECIAL_TYPE = 2;
    public static final int PASSIVE_TYPE = 3;

    protected void makeSkillView(TextView tv, int skillType, String text) {
        Drawable d = null;
        switch(skillType){
            case WEAPON_TYPE:
                d = getResources().getDrawable(R.drawable.weapons);
                break;
            case ASSIST_TYPE:
                d = getResources().getDrawable(R.drawable.assists);
                break;
            case SPECIAL_TYPE:
                d = getResources().getDrawable(R.drawable.specials);
                break;
            case PASSIVE_TYPE:
                d = getResources().getDrawable(R.drawable.passives);
                break;
        }
        tv.setCompoundDrawablesWithIntrinsicBounds(d,null,null,null);
        tv.setText(text);
    }

    protected abstract int getLayoutResource();

    protected boolean isIVFinder() {
        return false;
    }

    protected boolean isCollection() {
        return false;
    }

    protected String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    protected void cleanStat(int[] stat) {
        if (stat[4] == -1 || stat[5] == -1 || stat[3] == -1) {
            stat[4] = -1;
            stat[5] = -1;
            stat[3] = -1;
        }
    }

    protected void initHeroData() throws IOException {
        if(threeStarsMap==null) {
            threeStarsMap=new TreeMap<>();
            fourStarsMap=new TreeMap<>();
            fiveStarsMap=new TreeMap<>();
            File dataFile = new File(getBaseContext().getFilesDir(), "hero.data");
            if (dataFile.exists()) {
                try {
                    initHeroesFromInputStream(new FileInputStream(dataFile));
                } catch (Exception e) {
                    initHeroesFromCombo();
                }
            } else {
                initHeroesFromCombo();
            }
        }
    }

    protected void initHeroesFromCombo() throws IOException {
        InputStream inputStream = getBaseContext().getResources().openRawResource(R.raw.combojson);
        initHeroesFromInputStream(inputStream);
    }

    protected void initHeroesFromInputStream(InputStream inputStream) throws IOException {
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
                case 3:
                    threeStarsMap.put(capitalize(getResources().getString(nameIdentifier)), jH);
                    break;
                case 4:
                    fourStarsMap.put(capitalize(getResources().getString(nameIdentifier)), jH);
                    break;
                case 5:
                    fiveStarsMap.put(capitalize(getResources().getString(nameIdentifier)), jH);
                    break;
            }
            line = reader.readLine();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        try {
            initHeroData();
            //initSkills();
            singleton.init(this);
        } catch (IOException e) {
        }
    }

    private void startCollectionActivity() {
        if (!isCollection()) {
            Intent intent = new Intent(getBaseContext(), CollectionActivity.class);
            startActivity(intent);
        }
    }

    private void startFinderActivity() {
        if (!isIVFinder()) {
            Intent intent = new Intent(getBaseContext(), IVCheckActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gotofinder:
                startFinderActivity();
                return true;

            case R.id.gotocollection:
                startCollectionActivity();
                return true;

            case R.id.copycollectiontoclipboard:
                copyCollectionToClipboard();
                return true;

            case R.id.contact:
                displayContactDialog();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);


        }
    }

    private void copyCollectionToClipboard() {
        HeroCollection c = HeroCollection.loadFromStorage(getBaseContext());
        StringBuilder sb = new StringBuilder();
        for (HeroRoll hero : c) {
            sb.append(hero.toString());
            sb.append("\n");
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("HeroCollection", sb.toString());
        Log.i("", sb.toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getBaseContext(), R.string.exportcollok, Toast.LENGTH_SHORT).show();
    }

    public static class ContactMeDialogFragment extends DialogFragment {
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.contactmetext)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    private void displayContactDialog() {
        new ContactMeDialogFragment().show(getSupportFragmentManager(), "");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbarmenu, menu);
        return true;
    }

    protected void updateSkillView(LinearLayout layout, int[] skills) {
        TextView wpnTV = findAndResetSkillTextView(layout, R.id.vertical_skill_tv_wpn);
        TextView assistTV = findAndResetSkillTextView(layout, R.id.vertical_skill_tv_assist);
        TextView spTV = findAndResetSkillTextView(layout, R.id.vertical_skill_tv_special);
        TextView aTV = findAndResetSkillTextView(layout, R.id.vertical_skill_tv_a);
        TextView bTV = findAndResetSkillTextView(layout, R.id.vertical_skill_tv_b);
        TextView cTV = findAndResetSkillTextView(layout, R.id.vertical_skill_tv_c);
        for (int i : skills) {
            if (isWeapon(i)) {
                makeSkillView(wpnTV, WEAPON_TYPE, singleton.skillsMap.get(i).name);
                wpnTV.setVisibility(View.VISIBLE);
            } else if (isAssist(i)) {
                makeSkillView(assistTV, ASSIST_TYPE, singleton.skillsMap.get(i).name);
                assistTV.setVisibility(View.VISIBLE);
            } else if (isSpecial(i)) {
                makeSkillView(spTV, SPECIAL_TYPE, singleton.skillsMap.get(i).name);
                spTV.setVisibility(View.VISIBLE);
            } else if (isPassiveA(i)) {
                makeSkillView(aTV, PASSIVE_TYPE, singleton.skillsMap.get(i).name);
                aTV.setVisibility(View.VISIBLE);
            } else if (isPassiveB(i)) {
                makeSkillView(bTV, PASSIVE_TYPE, singleton.skillsMap.get(i).name);
                bTV.setVisibility(View.VISIBLE);
            } else if (isPassiveC(i)) {
                makeSkillView(cTV, PASSIVE_TYPE, singleton.skillsMap.get(i).name);
                cTV.setVisibility(View.VISIBLE);
            }
        }
    }

    private TextView findAndResetSkillTextView(LinearLayout layout, int id) {
        TextView textView = (TextView) layout.findViewById(id);
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        textView.setText("");
        return textView;
    }

    protected void adAdBanner() {
        PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
        PublisherAdRequest.Builder b = new PublisherAdRequest.Builder();
        PublisherAdRequest adRequest = b.build();
        mPublisherAdView.loadAd(adRequest);
    }


    protected void disableAdBanner() {
        PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
        mPublisherAdView.setVisibility(View.GONE);
    }

    protected int[] calculateMods(Hero hero, int lvl, boolean nakedHeroes) {

        int[] result = new int[]{0, 0, 0, 0, 0};
        if (lvl == 1 && hero.skills1 != null && nakedHeroes) {
            for (int skill : hero.skills1) {
                int[] mods = skillsMap.get(skill).mods;
                result[0] += mods[0];
                result[1] += mods[1];
                result[2] += mods[2];
                result[3] += mods[3];
                result[4] += mods[4];
            }
        } else {
            if (hero.skills40 != null && nakedHeroes) {
                for (int skill : hero.skills40) {
                    int[] mods = skillsMap.get(skill).mods;
                    result[0] += mods[0];
                    result[1] += mods[1];
                    result[2] += mods[2];
                    result[3] += mods[3];
                    result[4] += mods[4];
                }
            }
        }
        return result;
    }
}
