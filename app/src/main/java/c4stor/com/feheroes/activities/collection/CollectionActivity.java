package c4stor.com.feheroes.activities.collection;

import android.app.Activity;
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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
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
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import c4stor.com.feheroes.R;
import c4stor.com.feheroes.activities.ToolbaredActivity;
import c4stor.com.feheroes.activities.heropage.HeroPageActivity;
import c4stor.com.feheroes.model.hero.Hero;
import c4stor.com.feheroes.model.hero.HeroCollection;
import c4stor.com.feheroes.model.hero.HeroInfo;
import c4stor.com.feheroes.model.hero.HeroRoll;

public class CollectionActivity extends ToolbaredActivity {

    private static Comparator<HeroRoll> sorting = null;
    private static boolean skillsOn = true;

    private List<SuppressedItem> supressedItems = new ArrayList<>();

    private static Map<HeroRoll, Integer> statVisibility = new HashMap<>();
    private static int defaultVisibility = View.GONE;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_collection;
    }

    @Override
    protected boolean isHeroCollection() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        toolBar.setTitle(R.string.mycollection);
        toolBar.setTitleTextColor(getResources().getColor(R.color.icons));
        setSupportActionBar(toolBar);
        onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.collectiontoolbar, menu);
        if (skillsOn) {
            menu.findItem(R.id.toggleNakedView).getIcon().setAlpha(255);
        } else {
            menu.findItem(R.id.toggleNakedView).getIcon().setAlpha(130);
        }
        menu.findItem(R.id.undo).setEnabled(supressedItems.size() > 0);
        if (!menu.findItem(R.id.undo).isEnabled())
            menu.findItem(R.id.undo).getIcon().setAlpha(130);
        else
            menu.findItem(R.id.undo).getIcon().setAlpha(255);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHeroAttributes();
        initListView();
        initTextView();
        adAdBanner();
//        disableAdBanner();
    }

    //this method is there to update old HeroCollections
    private void updateHeroAttributes() {
        for (HeroRoll heroRoll : singleton.collection) {
            if (heroRoll.hero.movementType == null) {
                HeroInfo mapHero = singleton.heroMap.get(heroRoll.hero.name);
                heroRoll.hero.movementType = mapHero.movementType;
                heroRoll.hero.weaponType = mapHero.weaponType;
            }
            if (heroRoll.hero.atkGrowth == 0) {
                HeroInfo mapHero = singleton.heroMap.get(heroRoll.hero.name);
                heroRoll.hero.hpGrowth = mapHero.hpGrowth;
                heroRoll.hero.atkGrowth = mapHero.atkGrowth;
                heroRoll.hero.spdGrowth = mapHero.spdGrowth;
                heroRoll.hero.defGrowth = mapHero.defGrowth;
                heroRoll.hero.resGrowth = mapHero.resGrowth;
                heroRoll.hero.availability = mapHero.availability;
            }
        }
        singleton.collection.save(getBaseContext());
    }

    private void initTextView() {
        TextView noCollectionText = (TextView) findViewById(R.id.nocollectiontext);
        if (singleton.collection.size() > 0)
            noCollectionText.setVisibility(View.GONE);
        else {
            noCollectionText.setText(R.string.empty_collection);
        }
    }

    private void initListView() {
        ListView v = (ListView) findViewById(R.id.collectionlist);
        Parcelable state = v.onSaveInstanceState();
        if (singleton.collection.size() > 0) {

            v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ViewHolder vh = (ViewHolder) view.getTag();
                    if (vh.extraData.getVisibility() != View.GONE) {
                        vh.extraData.setVisibility(View.GONE);
                        statVisibility.remove(vh.hero);
                        vh.comment.setVisibility(View.GONE);
                    } else {
                        vh.extraData.setVisibility(View.VISIBLE);
                        statVisibility.put(vh.hero, View.VISIBLE);
                        if (vh.hero.comment != null && vh.hero.comment.length() > 0)
                            vh.comment.setVisibility(View.VISIBLE);
                    }
                }
            });
            HeroCollectionAdapter adapter = new HeroCollectionAdapter(this, singleton.collection, sorting);
            v.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            v.onRestoreInstanceState(state);
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
                sortByName();
                defaultVisibility = View.GONE;
                initListView();
                return true;
            case R.id.sortByStars:
                sortByStar();
                defaultVisibility = View.GONE;
                initListView();
                return true;
            case R.id.sortByHP:
                sortByHP();
                defaultVisibility = View.VISIBLE;
                initListView();
                return true;
            case R.id.sortByAtk:
                sortByAtk();
                defaultVisibility = View.VISIBLE;
                initListView();
                return true;
            case R.id.sortBySpd:
                sortBySpd();
                defaultVisibility = View.VISIBLE;
                initListView();
                return true;
            case R.id.sortByDef:
                sortByDef();
                defaultVisibility = View.VISIBLE;
                initListView();
                return true;
            case R.id.sortByRes:
                sortByRes();
                defaultVisibility = View.VISIBLE;
                initListView();
                return true;
            case R.id.sortByBST:
                sortByBST();
                defaultVisibility = View.VISIBLE;
                initListView();
                return true;
            case R.id.sortByDate:
                sorting = null;
                initListView();
                return true;
            case R.id.toggleNakedView:
                skillsOn = !skillsOn;
                invalidateOptionsMenu();
                initListView();
            case R.id.undo:
                undoHeroSupression();
                initListView();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void undoHeroSupression() {
        if (supressedItems.size() > 0) {
            SuppressedItem suppressedItem = supressedItems.get(supressedItems.size() - 1);
            singleton.collection.add(suppressedItem.position, suppressedItem.heroRoll);
            singleton.collection.save(getBaseContext());
            supressedItems.remove(supressedItems.size() - 1);
            invalidateOptionsMenu();
        }
    }

    private void sortByBST() {
        sorting = new Comparator<HeroRoll>() {
            @Override
            public int compare(HeroRoll o1, HeroRoll o2) {
                int[] m1 = calculateMods(o1.hero, 40, !skillsOn);
                int[] m2 = calculateMods(o2.hero, 40, !skillsOn);
                int mod1 = m1[0] + m1[1] + m1[2] + m1[3] + m1[4];
                int mod2 = m2[0] + m2[1] + m2[2] + m2[3] + m2[4];
                if (o1.getBST(getBaseContext()) - mod1 != o2.getBST(getBaseContext()) - mod2)
                    return o2.getBST(getBaseContext()) - o1.getBST(getBaseContext()) - mod2 + mod1;
                else
                    return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
            }
        };
    }

    private void sortByRes() {
        sorting = new Comparator<HeroRoll>() {
            @Override
            public int compare(HeroRoll o1, HeroRoll o2) {
                int mod1 = calculateMods(o1.hero, 40, !skillsOn)[4];
                int mod2 = calculateMods(o2.hero, 40, !skillsOn)[4];
                if (o1.getRes(getBaseContext()) != o2.getRes(getBaseContext()))
                    return o2.getRes(getBaseContext()) - o1.getRes(getBaseContext()) - mod2 + mod1;
                else
                    return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
            }
        };
    }

    private void sortByDef() {
        sorting = new Comparator<HeroRoll>() {
            @Override
            public int compare(HeroRoll o1, HeroRoll o2) {
                int mod1 = calculateMods(o1.hero, 40, !skillsOn)[3];
                int mod2 = calculateMods(o2.hero, 40, !skillsOn)[3];
                if (o1.getDef(getBaseContext()) != o2.getDef(getBaseContext()))
                    return o2.getDef(getBaseContext()) - o1.getDef(getBaseContext()) - mod2 + mod1;
                else
                    return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
            }
        };
    }

    private void sortBySpd() {
        sorting = new Comparator<HeroRoll>() {
            @Override
            public int compare(HeroRoll o1, HeroRoll o2) {
                int mod1 = calculateMods(o1.hero, 40, !skillsOn)[2];
                int mod2 = calculateMods(o2.hero, 40, !skillsOn)[2];
                if (o1.getSpeed(getBaseContext()) != o2.getSpeed(getBaseContext()))
                    return o2.getSpeed(getBaseContext()) - o1.getSpeed(getBaseContext()) - mod2 + mod1;
                else
                    return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
            }
        };
    }

    private void sortByAtk() {
        sorting = new Comparator<HeroRoll>() {
            @Override
            public int compare(HeroRoll o1, HeroRoll o2) {
                int mod1 = calculateMods(o1.hero, 40, !skillsOn)[1];
                int mod2 = calculateMods(o2.hero, 40, !skillsOn)[1];
                if (o1.getAtk(getBaseContext()) - mod1 != o2.getAtk(getBaseContext()) - mod2)
                    return o2.getAtk(getBaseContext()) - o1.getAtk(getBaseContext()) - mod2 + mod1;
                else
                    return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
            }
        };
    }

    private void sortByHP() {
        sorting = new Comparator<HeroRoll>() {
            @Override
            public int compare(HeroRoll o1, HeroRoll o2) {
                int mod1 = calculateMods(o1.hero, 40, !skillsOn)[0];
                int mod2 = calculateMods(o2.hero, 40, !skillsOn)[0];
                if (o1.getHP(getBaseContext()) - mod1 != o2.getHP(getBaseContext()) - mod2)
                    return o2.getHP(getBaseContext()) - o1.getHP(getBaseContext()) - mod2 + mod1;
                else
                    return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
            }
        };
    }

    private void sortByStar() {
        sorting = new Comparator<HeroRoll>() {
            @Override
            public int compare(HeroRoll o1, HeroRoll o2) {
                if (o1.hero.rarity != o2.hero.rarity)
                    return o2.hero.rarity - o1.hero.rarity;
                else
                    return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
            }
        };
    }

    private void sortByName() {
        sorting = new Comparator<HeroRoll>() {
            @Override
            public int compare(HeroRoll o1, HeroRoll o2) {
                return o1.getDisplayName(getBaseContext()).compareTo(o2.getDisplayName(getBaseContext()));
            }
        };
    }

    public class HeroCollectionAdapter extends ArrayAdapter<HeroRoll> {

        private final HeroCollection collection;
        private final Comparator<HeroRoll> comparator;
        private ArrayList<HeroRoll> view;
        private int height;
        private int width;

        public HeroCollectionAdapter(Context context, HeroCollection collection, Comparator<HeroRoll> comparator) {
            super(context, 0, collection);
            this.collection = collection;
            this.comparator = comparator;
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
            width = displayMetrics.widthPixels;
            makeView();
        }

        private void makeView() {
            if (comparator == null) {
                view = singleton.collection;
            } else {
                view = new ArrayList<>(CollectionActivity.this.singleton.collection);
                Collections.sort(view, comparator);
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

            return Bitmap.createScaledBitmap(output, finalWidth, finalWidth, true);
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
                holder = initViewHolder(v);
                // associate the holder with the view for later lookup
                v.setTag(holder);
            } else {
                // view already exists, get the holder instance from the view
                holder = (ViewHolder) v.getTag();
            }

            if (position % 2 == 1) {
                holder.globalLayout.setBackgroundColor(getResources().getColor(R.color.background_light));
            }

            final HeroRoll hero = view.get(position);
            holder.hero = hero;
            refreshHero(hero);

            drawHeroPortrait(holder, hero);

            selectHeroRarity(holder, hero);
            setRarityItemListener(holder, hero);

            showBoonsOrBanes(holder.boons, hero.boons, '+');
            showBoonsOrBanes(holder.banes, hero.banes, '-');
            calculateHeroStats(holder, hero);

            showComment(holder, hero);

            setDeleteButtonListener(holder, hero);
            setHeroPageButtonListener(holder);

            if (statVisibility.containsKey(hero)) {
                //noinspection WrongConstant
                holder.extraData.setVisibility(statVisibility.get(hero));
            } else {
                holder.extraData.setVisibility(defaultVisibility);
            }
            return v;
        }

        private void calculateHeroStats(ViewHolder holder, HeroRoll hero) {
            int[] mods = calculateMods(hero.hero, 40, !skillsOn);
            makePopupStat(holder.lvl40HP, hero, hero.hero.HP, mods[0], getResources().getString(R.string.hp));
            makePopupStat(holder.lvl40Atk, hero, hero.hero.atk, mods[1], getResources().getString(R.string.atk));
            makePopupStat(holder.lvl40Spd, hero, hero.hero.speed, mods[2], getResources().getString(R.string.spd));
            makePopupStat(holder.lvl40Def, hero, hero.hero.def, mods[3], getResources().getString(R.string.def));
            makePopupStat(holder.lvl40Res, hero, hero.hero.res, mods[4], getResources().getString(R.string.res));

            int totalMods = mods[0] + mods[1] + mods[2] + mods[3] + mods[4];
            String bstText = hero.getBST(getContext()) < 0 ? "?" : hero.getBST(getContext()) - totalMods + "";
            holder.lvl40BST.setText(
                    getResources().getString(R.string.bst) + " " +
                            bstText);
            holder.lvl40BST.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        private void setHeroPageButtonListener(final ViewHolder holder) {
            holder.detailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), HeroPageActivity.class);
                    intent.putExtra("position", singleton.collection.indexOf(holder.hero));
                    startActivity(intent);
                }
            });
        }

        private void setDeleteButtonListener(ViewHolder holder, final HeroRoll hero) {
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = CollectionActivity.this.singleton.collection.indexOf(hero);
                    CollectionActivity.this.singleton.collection.remove(hero);
                    SuppressedItem s = new SuppressedItem();
                    s.position = position;
                    s.heroRoll = hero;
                    supressedItems.add(s);
                    CollectionActivity.this.singleton.collection.save(getBaseContext());
                    invalidateOptionsMenu();
                    makeView();
                    HeroCollectionAdapter.this.notifyDataSetChanged();
                }
            });
        }

        private void setRarityItemListener(ViewHolder holder, final HeroRoll hero) {
            holder.rarity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0 && hero.hero.rarity != 5) {
                        hero.hero = singleton.fiveStarsMap.get(hero.getDisplayName(getContext()));
                        hero.hero.rarity = 5;
                        collection.save(getContext());
                        initListView();
                    } else if (position == 1 && hero.hero.rarity != 4) {
                        hero.hero = singleton.fourStarsMap.get(hero.getDisplayName(getContext()));
                        hero.hero.rarity = 4;
                        collection.save(getContext());
                        initListView();
                    } else if (position == 2 && hero.hero.rarity != 3) {
                        hero.hero = singleton.threeStarsMap.get(hero.getDisplayName(getContext()));
                        hero.hero.rarity = 3;
                        collection.save(getContext());
                        initListView();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        private void selectHeroRarity(ViewHolder holder, HeroRoll hero) {
            ArrayAdapter<CharSequence> adapter;
            if (singleton.threeStarsMap.containsKey(hero.getDisplayName(getContext()))) {
                adapter = ArrayAdapter.createFromResource(getContext(), R.array.stars_array, R.layout.spinneritem_nopadding);
            } else if (singleton.fourStarsMap.containsKey(hero.getDisplayName(getContext()))) {
                adapter = ArrayAdapter.createFromResource(getContext(), R.array.stars_array_4_5, R.layout.spinneritem_nopadding);
            } else {
                adapter = ArrayAdapter.createFromResource(getContext(), R.array.stars_array_5, R.layout.spinneritem_nopadding);
            }

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.rarity.setAdapter(adapter);
            if (hero.hero.rarity == 3) {
                holder.rarity.setSelection(2);
            } else if (hero.hero.rarity == 4) {
                holder.rarity.setSelection(1);
            } else {
                holder.rarity.setSelection(0);
            }
        }

        private void drawHeroPortrait(ViewHolder holder, HeroRoll hero) {
            int drawableId = getContext().getResources().getIdentifier(hero.hero.name.toLowerCase(), "drawable", getContext().getPackageName());
            Bitmap b = BitmapFactory.decodeResource(getContext().getResources(), drawableId);

            int circleRadius = Math.min(140, Math.min(height / 5, width / 5));
            Bitmap c = getRoundedCroppedBitmap(b, circleRadius, circleRadius);
            Drawable dCool = new BitmapDrawable(getContext().getResources(), c);

            holder.collImage.setImageDrawable(dCool);
            holder.collName.setText(hero.getDisplayName(getContext()));
        }


        private void makePopupStat(TextView statTV, HeroRoll hero, int[] stat, int mod, String statName) {

            if (hero.boons != null && hero.boons.contains(statName)) {
                statTV.setText(statName + " " + makeText(stat[5] - mod));
                statTV.setTextColor(getResources().getColor(R.color.high_green));
            } else if (hero.banes != null && hero.banes.contains(statName)) {
                statTV.setText(statName + " " + makeText(stat[3] - mod));
                statTV.setTextColor(getResources().getColor(R.color.low_red));
            } else {
                statTV.setText(statName + " " + makeText(stat[4] - mod));
                statTV.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    private void showComment(ViewHolder holder, HeroRoll hero) {
        holder.comment.setText(hero.comment);
    }

    @NonNull
    private ViewHolder initViewHolder(View v) {
        ViewHolder holder;
        holder = new ViewHolder();
        holder.globalLayout = (LinearLayout) v.findViewById(R.id.collection_line_layout);
        holder.collImage = (ImageView) v.findViewById(R.id.collimage);
        holder.collName = (TextView) v.findViewById(R.id.collname);
        holder.rarity = (Spinner) v.findViewById(R.id.collnamerarity);
        holder.boons = (TextView) v.findViewById(R.id.boons);
        holder.banes = (TextView) v.findViewById(R.id.banes);
        holder.detailsButton = (Button) v.findViewById(R.id.heropageBtn);
        holder.deleteButton = (Button) v.findViewById(R.id.deleteBtn);
        holder.extraData = (LinearLayout) v.findViewById(R.id.hero40StatLine);
        holder.lvl40HP = (TextView) v.findViewById(R.id.hero40LineHP);
        holder.lvl40Atk = (TextView) v.findViewById(R.id.hero40LineAtk);
        holder.lvl40Spd = (TextView) v.findViewById(R.id.hero40LineSpd);
        holder.lvl40Def = (TextView) v.findViewById(R.id.hero40LineDef);
        holder.lvl40Res = (TextView) v.findViewById(R.id.hero40LineRes);
        holder.lvl40BST = (TextView) v.findViewById(R.id.hero40LineBST);
        holder.comment = (TextView) v.findViewById(R.id.collection_comment);
        return holder;
    }

    private void showBoonsOrBanes(TextView textView, List<String> list, char plusMinus) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size() - 1; i++) {
            sb.append(plusMinus).append(list.get(i)).append("\n");
        }
        if (list.size() > 0) {
            sb.append(plusMinus).append(list.get(list.size() - 1));
        }
        textView.setText(sb);
    }

    private void refreshHero(HeroRoll hero) {
        if (hero.hero.rarity == 5) {
            hero.hero = singleton.fiveStarsMap.get(hero.getDisplayName(this));
        } else if (hero.hero.rarity == 4) {
            hero.hero = singleton.fourStarsMap.get(hero.getDisplayName(this));
        } else if (hero.hero.rarity == 3) {
            hero.hero = singleton.threeStarsMap.get(hero.getDisplayName(this));
        }
    }

    static class ViewHolder {
        HeroRoll hero;
        LinearLayout globalLayout;
        ImageView collImage;
        TextView collName;
        Spinner rarity;
        TextView boons;
        TextView banes;
        Button deleteButton;
        LinearLayout extraData;
        TextView lvl40HP;
        TextView lvl40Atk;
        TextView lvl40Spd;
        TextView lvl40Def;
        TextView lvl40Res;
        TextView lvl40BST;
        Button detailsButton;
        TextView comment;
    }

    static class SuppressedItem {
        int position;
        HeroRoll heroRoll;
    }
}
