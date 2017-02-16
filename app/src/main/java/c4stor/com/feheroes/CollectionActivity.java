package c4stor.com.feheroes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

public class CollectionActivity extends AppCompatActivity {

    private HeroCollection heroCollection;

    private void startFinderActivity() {
        Intent intent = new Intent(getBaseContext(), FEHAnalyserActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        View v = findViewById(R.id.collectionlist);
        View.OnTouchListener swipeListener = new OnSwipeTouchListener(getBaseContext()) {
            public void onSwipeRight() {
                startFinderActivity();
            }

            public void onSwipeLeft() {
                onSwipeRight();
            }
        };
        v.setOnTouchListener(swipeListener);
        View v2 = findViewById(R.id.nocollectiontext);
        v2.setOnTouchListener(swipeListener);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.mycollection);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.icons));
        setSupportActionBar(myToolbar);
        onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gotofinder:
                startFinderActivity();
                return true;
            case R.id.gotocollection:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbarmenu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        heroCollection = HeroCollection.loadFromStorage(getBaseContext());

        initListView();
        initTextView();
        adAdBanner();

    }

    private void adAdBanner() {
        PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdViewColl);
        PublisherAdRequest.Builder b = new PublisherAdRequest.Builder();
        PublisherAdRequest adRequest = b.build();
        mPublisherAdView.loadAd(adRequest);
    }

    private void initTextView() {
        TextView noCollectionText = (TextView) findViewById(R.id.nocollectiontext);
        if(heroCollection.size() >0)
            noCollectionText.setVisibility(View.INVISIBLE);
        else {
            noCollectionText.setText("You don't have anyone in your collection.\nAdd some using the + button in the IV finder.");

        }
    }

    private void initListView() {
        ListView v = (ListView) findViewById(R.id.collectionlist);

        if (heroCollection.size() > 0)
            v.setAdapter(new HeroCollectionAdapter(getBaseContext(), heroCollection));
        else
            v.setVisibility(View.INVISIBLE);
    }

    public class HeroCollectionAdapter extends ArrayAdapter<HeroRoll> {

        private final HeroCollection collection;

        public HeroCollectionAdapter(Context context, HeroCollection collection) {
            super(context, 0, collection);
            this.collection = collection;
        }

        private String capitalize(final String line) {
            return Character.toUpperCase(line.charAt(0)) + line.substring(1);
        }

        private Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int finalWidth, int finalWeight) {
            int widthLight = bitmap.getWidth();
            int heightLight = bitmap.getHeight();

            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(output);
            Paint paintColor = new Paint();
            paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);

            RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));

            canvas.drawRoundRect(rectF, widthLight / 2, heightLight / 2, paintColor);

            Paint paintImage = new Paint();
            paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            canvas.drawBitmap(bitmap, 0, 0, paintImage);

            return Bitmap.createScaledBitmap(
                    output, finalWidth, finalWidth, true);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;
            ViewHolder holder; // to reference the child views for later actions

            if (v == null) {
                LayoutInflater vi =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.collection_line, null);
                // cache view fields into the holder
                holder = new ViewHolder();
                holder.collImage = (ImageView) v.findViewById(R.id.collimage);
                holder.collName = (TextView) v.findViewById(R.id.collname);
                holder.boons = (TextView) v.findViewById(R.id.boons);
                holder.banes = (TextView) v.findViewById(R.id.banes);
                holder.deleteButton = (Button) v.findViewById(R.id.deleteBtn);
                // associate the holder with the view for later lookup
                v.setTag(holder);
            } else {
                // view already exists, get the holder instance from the view
                holder = (ViewHolder) v.getTag();
            }

            HeroRoll hero = heroCollection.get(position);

            int drawableId = getContext().getResources().getIdentifier(hero.hero.name.toLowerCase(), "drawable", getContext().getPackageName());
            Bitmap b = BitmapFactory.decodeResource(getContext().getResources(), drawableId);
            Bitmap c = getRoundedCroppedBitmap(b, 140, 140);
            Drawable dCool = new BitmapDrawable(getContext().getResources(), c);

            holder.collImage.setImageDrawable(dCool);
            int nameIdentifier = getContext().getResources().getIdentifier(hero.hero.name.toLowerCase(), "string", getContext().getPackageName());
            StringBuilder sb = new StringBuilder();
            sb.append(capitalize(getContext().getString(nameIdentifier)));
            sb.append("\n");
            for (int i = 1; i <= hero.stars; i++) {
                sb.append("★");
            }

            holder.collName.setText(sb.toString());

            StringBuilder boons = new StringBuilder();

            for (String s : hero.boons) {
                boons.append("+" + s + " ");
            }
            holder.boons.setText(boons);

            StringBuilder banes = new StringBuilder();

            for (String s : hero.banes) {
                banes.append("-" + s + " ");
            }
            holder.banes.setText(banes);

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    heroCollection.remove(position);
                    heroCollection.save(getBaseContext());
                    HeroCollectionAdapter.this.notifyDataSetChanged();
                }
            });

            return v;
        }
    }

    static class ViewHolder {
        ImageView collImage;
        TextView collName;
        TextView boons;
        TextView banes;
        Button deleteButton;
        int position;
    }
}
