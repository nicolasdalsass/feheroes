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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import c4stor.com.feheroes.R;
import c4stor.com.feheroes.activities.ivcheck.IVCheckActivity;

import static java.lang.Thread.sleep;

public class DownloadDataActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_data);

        final DownloadTask downloadTask = new DownloadTask(this, "hero.data", false, true);
        downloadTask.execute("https://nicolasdalsass.github.io/heroesjson/v1906.json");

    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        File dataFile;
        boolean goToIVFinder;
        boolean downloadAnotherFile;


        public DownloadTask(Context context, String outputFilePath, boolean goToIVFinder, boolean downloadAnotherFile) {
            this.context = context;

            File directory = getBaseContext().getFilesDir();
            dataFile = new File(directory, outputFilePath);
            this.goToIVFinder = goToIVFinder;
            this.downloadAnotherFile = downloadAnotherFile;
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
                if ("web".equals(result)) {
                    Toast t = Toast.makeText(context, "Hero data synchronized from web", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                } else {
                    Toast t = Toast.makeText(context, "Unable to download update, using local data instead", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                }
                if (downloadAnotherFile && Locale.getDefault().getDisplayLanguage().startsWith("fr")){
                    final DownloadTask localeDownloadTask = new DownloadTask(context, "skills.locale", true, false);
                    localeDownloadTask.execute("https://nicolasdalsass.github.io/heroesjson/allskills-fr.json");
                } else {
                    Intent ivCheckIntent = new Intent(getBaseContext(), IVCheckActivity.class);
                    startActivity(ivCheckIntent);
                }
            } else {
                boolean dlOtherFile = false;
                if (Locale.getDefault().getDisplayLanguage().startsWith("fr")) {
                    dlOtherFile = true;
                }
                final DownloadTask skillDownloadTask = new DownloadTask(context, "skills.data", true, dlOtherFile);
                skillDownloadTask.execute("https://nicolasdalsass.github.io/heroesjson/allskills.json");

            }
        }
    }
}
