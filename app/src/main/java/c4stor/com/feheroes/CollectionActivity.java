package c4stor.com.feheroes;

import android.content.Context;
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
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionActivity extends ToolbaredActivity {

    private HeroCollection heroCollection;

    private Comparator<HeroRoll> sorting = null;

    private Map<HeroRoll, Integer> statVisibility = new HashMap<HeroRoll, Integer>();
    private int defaultVisibility = View.GONE;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_collection;
    }

    @Override
    protected boolean isCollection() {
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
        getMenuInflater().inflate(R.menu.collectiontoolbar, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        heroCollection = HeroCollection.loadFromStorage(getBaseContext());
        try {
            initHeroData();
        } catch (IOException e) {

        }
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
        if (heroCollection.size() > 0)
            noCollectionText.setVisibility(View.INVISIBLE);
        else {
            noCollectionText.setText("You don't have anyone in your collection.\nAdd some using the + button in the IV finder.");
        }
    }

    private void initListView() {
        ListView v = (ListView) findViewById(R.id.collectionlist);

        if (heroCollection.size() > 0) {

            v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ViewHolder vh = (ViewHolder) view.getTag();
                    if (vh.extraData.getVisibility() != View.GONE) {
                        vh.extraData.setVisibility(View.GONE);
                        statVisibility.remove(vh.hero);
                    } else {
                        vh.extraData.setVisibility(View.VISIBLE);
                        statVisibility.put(vh.hero, View.VISIBLE);
                    }
                }
            });
            HeroCollectionAdapter adapter = new HeroCollectionAdapter(getBaseContext(), heroCollection, sorting);
            v.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else
            v.setVisibility(View.INVISIBLE);
    }

    private String makeText(int i) {
        if (i < 0)
            return "?";
        else
            return i + "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortByName:
                sorting = new Comparator<HeroRoll>() {
                    @Override
                    public int compare(HeroRoll o1, HeroRoll o2) {
                        return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
                    }
                };
                defaultVisibility=View.GONE;
                initListView();
                return true;

            case R.id.sortByStars:
                sorting = new Comparator<HeroRoll>() {
                    @Override
                    public int compare(HeroRoll o1, HeroRoll o2) {
                        if (o1.stars != o2.stars)
                            return o2.stars - o1.stars;
                        else
                            return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
                    }
                };
                initListView();
                defaultVisibility = View.GONE;
                return true;
            case R.id.sortByHP:
                sorting = new Comparator<HeroRoll>() {
                    @Override
                    public int compare(HeroRoll o1, HeroRoll o2) {
                        if (o1.getHP(getBaseContext()) != o2.getHP(getBaseContext()))
                            return o2.getHP(getBaseContext()) - o1.getHP(getBaseContext());
                        else
                            return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
                    }
                };
                initListView();
                defaultVisibility = View.VISIBLE;
                return true;
            case R.id.sortByAtk:
                sorting = new Comparator<HeroRoll>() {
                    @Override
                    public int compare(HeroRoll o1, HeroRoll o2) {
                        if (o1.getAtk(getBaseContext()) != o2.getAtk(getBaseContext()))
                            return o2.getAtk(getBaseContext()) - o1.getAtk(getBaseContext());
                        else
                            return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
                    }
                };
                initListView();
                defaultVisibility = View.VISIBLE;
                return true;
            case R.id.sortBySpd:
                sorting = new Comparator<HeroRoll>() {
                    @Override
                    public int compare(HeroRoll o1, HeroRoll o2) {
                        if (o1.getSpeed(getBaseContext()) != o2.getSpeed(getBaseContext()))
                            return o2.getSpeed(getBaseContext()) - o1.getSpeed(getBaseContext());
                        else
                            return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
                    }
                };
                initListView();
                defaultVisibility = View.VISIBLE;
                return true;
            case R.id.sortByDef:
                sorting = new Comparator<HeroRoll>() {
                    @Override
                    public int compare(HeroRoll o1, HeroRoll o2) {
                        if (o1.getDef(getBaseContext()) != o2.getDef(getBaseContext()))
                            return o2.getDef(getBaseContext()) - o1.getDef(getBaseContext());
                        else
                            return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
                    }
                };
                initListView();
                defaultVisibility = View.VISIBLE;
                return true;
            case R.id.sortByRes:
                sorting = new Comparator<HeroRoll>() {
                    @Override
                    public int compare(HeroRoll o1, HeroRoll o2) {
                        if (o1.getRes(getBaseContext()) != o2.getRes(getBaseContext()))
                            return o2.getRes(getBaseContext()) - o1.getRes(getBaseContext());
                        else
                            return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
                    }
                };
                initListView();
                defaultVisibility = View.VISIBLE;
                return true;
            case R.id.sortByDate:
                sorting = null;
                initListView();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public class HeroCollectionAdapter extends ArrayAdapter<HeroRoll> {

        private final HeroCollection collection;
        private final Comparator<HeroRoll> comparator;

        public HeroCollectionAdapter(Context context, HeroCollection collection, Comparator<HeroRoll> comparator) {
            super(context, 0, collection);
            this.collection = collection;
            this.comparator = comparator;
        }

        private HeroRoll getItemAtPos(int position) {
            if (comparator == null) {
                return collection.get(position);
            } else {
                List<HeroRoll> collCopy = new ArrayList<>(heroCollection);
                Collections.sort(collCopy, comparator);
                return collCopy.get(position);
            }
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
                holder.rarity = (TextView) v.findViewById(R.id.collnamerarity);
                holder.boons = (TextView) v.findViewById(R.id.boons);
                holder.banes = (TextView) v.findViewById(R.id.banes);
                holder.deleteButton = (Button) v.findViewById(R.id.deleteBtn);
                holder.extraData = (LinearLayout) v.findViewById(R.id.hero40Line);
                holder.lvl40HP = (TextView) v.findViewById(R.id.hero40LineHP);
                holder.lvl40Atk = (TextView) v.findViewById(R.id.hero40LineAtk);
                holder.lvl40Spd = (TextView) v.findViewById(R.id.hero40LineSpd);
                holder.lvl40Def = (TextView) v.findViewById(R.id.hero40LineDef);
                holder.lvl40Res = (TextView) v.findViewById(R.id.hero40LineRes);
                // associate the holder with the view for later lookup
                v.setTag(holder);
            } else {
                // view already exists, get the holder instance from the view
                holder = (ViewHolder) v.getTag();
            }

            final HeroRoll hero = getItemAtPos(position);
            holder.hero = hero;

            int drawableId = getContext().getResources().getIdentifier(hero.hero.name.toLowerCase(), "drawable", getContext().getPackageName());
            Bitmap b = BitmapFactory.decodeResource(getContext().getResources(), drawableId);
            Bitmap c = getRoundedCroppedBitmap(b, 140, 140);
            Drawable dCool = new BitmapDrawable(getContext().getResources(), c);

            holder.collImage.setImageDrawable(dCool);
            holder.collName.setText(hero.getDisplayName(getContext()));


            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= hero.stars; i++) {
                sb.append("★");
            }

            holder.rarity.setText(sb.toString());

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

            Hero statHero = hero.hero;
            if (hero.stars == 3 && threeStarsMap.containsKey(hero.hero.name)) {
                statHero = threeStarsMap.get(hero.hero.name);
            }
            if (hero.stars == 4 && fourStarsMap.containsKey(hero.hero.name)) {
                statHero = fourStarsMap.get(hero.hero.name);
            }
            if (hero.stars == 5 && fiveStarsMap.containsKey(hero.hero.name)) {
                statHero = fiveStarsMap.get(hero.hero.name);
            }
            makePopupStat(holder.lvl40HP, hero, statHero.HP, getResources().getString(R.string.hp));
            makePopupStat(holder.lvl40Atk, hero, statHero.atk, getResources().getString(R.string.atk));
            makePopupStat(holder.lvl40Spd, hero, statHero.speed, getResources().getString(R.string.spd));
            makePopupStat(holder.lvl40Def, hero, statHero.def, getResources().getString(R.string.def));
            makePopupStat(holder.lvl40Res, hero, statHero.res, getResources().getString(R.string.res));
            if (statVisibility.containsKey(hero)) {
                //noinspection WrongConstant
                holder.extraData.setVisibility(statVisibility.get(hero));
            } else {
                holder.extraData.setVisibility(defaultVisibility);
            }
            return v;
        }


        private void makePopupStat(TextView statTV, HeroRoll hero, int[] stat, String statName) {

            if (hero.boons != null && hero.boons.contains(statName)) {
                statTV.setText(statName+": "+makeText(stat[5]));
                statTV.setTextColor(getResources().getColor(R.color.high_green));
            } else if (hero.banes != null && hero.banes.contains(statName)) {
                statTV.setText(statName+": "+makeText(stat[3]));
                statTV.setTextColor(getResources().getColor(R.color.low_red));
            } else {
                statTV.setText(statName+": "+makeText(stat[4]));
                statTV.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    static class ViewHolder {
        HeroRoll hero;
        ImageView collImage;
        TextView collName;
        TextView rarity;
        TextView boons;
        TextView banes;
        Button deleteButton;
        LinearLayout extraData;
        TextView lvl40HP;
        TextView lvl40Atk;
        TextView lvl40Spd;
        TextView lvl40Def;
        TextView lvl40Res;
        Boolean visible = false;

        int position;
    }
}
