package c4stor.com.feheroes.activities.download;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import c4stor.com.feheroes.R;
import c4stor.com.feheroes.activities.ModelSingleton;
import c4stor.com.feheroes.activities.ivcheck.IVCheckActivity;
import c4stor.com.feheroes.model.hero.Hero;
import c4stor.com.feheroes.model.hero.HeroInfo;
import c4stor.com.feheroes.model.hero.HeroRoll;

public class DownloadDataActivity extends AppCompatActivity {


    private ModelSingleton singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_data);
        if (Locale.getDefault().getDisplayLanguage().startsWith("fr")) {
            final DownloadTask localeDownloadTask = new DownloadTask(this, "skills.locale", false);
            localeDownloadTask.execute("https://nicolasdalsass.github.io/heroesjson/allskills-fr.json");
        }
        final DownloadTask skillDownloadTask = new DownloadTask(this, "skills.data", false);
        skillDownloadTask.execute("https://nicolasdalsass.github.io/heroesjson/allskills-inheritance.json");
        final DownloadTask growthTask = new DownloadTask(this, "hero.basics", false);
        growthTask.execute("https://nicolasdalsass.github.io/heroesjson/heroes-skillchain.json");
        final DownloadTask heroInfoTask = new DownloadTask(this, "heroinfo.data", true);
        heroInfoTask.execute("https://nicolasdalsass.github.io/heroesjson/v170630_cleaned.json");

    }


    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        File dataFile;
        boolean goToIVFinder;


        public DownloadTask(Context context, String outputFilePath, boolean goToIVFinder) {
            this.context = context;

            File directory = getBaseContext().getFilesDir();
            dataFile = new File(directory, outputFilePath);
            this.goToIVFinder = goToIVFinder;
        }


        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }


        @Override
        protected String doInBackground(String... sUrl) {
            if (!isOnline()) {
                return null;
            }
            InputStream input;
            OutputStream output;
            HttpURLConnection connection;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(2500);
                connection.setReadTimeout(2500);
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.w("", "Web update failed. Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage());
                }


                input = connection.getInputStream();
                int fileLength = connection.getContentLength();
                output = new FileOutputStream(dataFile);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                connection.disconnect();
                return "web";

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (goToIVFinder) {

                try {
                    singleton = ModelSingleton.getInstance(DownloadDataActivity.this);
                } catch (IOException e) {
                    DownloadDataActivity.this.finish();
                }

                if ("web".equals(result)) {
                    Toast t = Toast.makeText(context, "Hero data synchronized from web", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                } else {
                    Toast t = Toast.makeText(context, "Unable to download update, using local data instead", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                }
                Intent ivCheckIntent = new Intent(getBaseContext(), IVCheckActivity.class);
                startActivity(ivCheckIntent);
            }
        }
    }

    //this method is there to update old HeroCollections
    private void updateHeroAttributes() {
        for (HeroRoll heroRoll : singleton.collection) {
            Hero hero = heroRoll.hero;
            if (hero.movementType == null) {
                HeroInfo mapHero = singleton.basicsMap.get(hero.name);
                hero.movementType = mapHero.movementType;
                hero.weaponType = mapHero.weaponType;
            }
            if (hero.atkGrowth == 0) {
                HeroInfo mapHero = singleton.basicsMap.get(hero.name);
                hero.hpGrowth = mapHero.hpGrowth;
                hero.atkGrowth = mapHero.atkGrowth;
                hero.spdGrowth = mapHero.spdGrowth;
                hero.defGrowth = mapHero.defGrowth;
                hero.resGrowth = mapHero.resGrowth;
            }
            if (hero.weaponChain.length == 0) {
                HeroInfo mapHero = singleton.basicsMap.get(hero.name);
                hero.weaponChain = mapHero.weaponChain;
                hero.assistChain = mapHero.assistChain;
                hero.specialChain = mapHero.specialChain;
                hero.aChain = mapHero.aChain;
                hero.bChain = mapHero.bChain;
                hero.cChain = mapHero.cChain;
            }
            if (hero.rarity == 0) {
                hero.rarity = heroRoll.stars;
            }
        }
        singleton.collection.save(getBaseContext());
    }
}
