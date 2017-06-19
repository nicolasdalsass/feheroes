package c4stor.com.feheroes.activities.heropage;

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
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import c4stor.com.feheroes.R;
import c4stor.com.feheroes.activities.ToolbaredActivity;
import c4stor.com.feheroes.model.hero.HeroCollection;
import c4stor.com.feheroes.model.hero.HeroRoll;

/**
 * Created by eclogia on 04/06/17.
 */

public class HeroPageActivity extends ToolbaredActivity {

    private HeroRoll heroRoll;
    private ImageView heroPortrait;
    private EditText comment;
    private TextView hp;
    private TextView atk;
    private TextView spd;
    private TextView def;
    private TextView res;
    private TextView bst;
    protected boolean skillOn = true;
    private LinearLayout equippedSkills;
    private boolean skillManagementOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        Intent intent = getIntent();
        int heroPosition = intent.getIntExtra("position", 1);
        collection = HeroCollection.loadFromStorage(getBaseContext());
        this.heroRoll = collection.get(heroPosition);

        StringBuilder sb = new StringBuilder(heroRoll.getDisplayName(this));
        sb.append(" ");
        for (int i = 0; i < heroRoll.stars; i++) {
            sb.append("★");
        }
        myToolbar.setTitle(sb.toString());
        myToolbar.setTitleTextColor(getResources().getColor(R.color.icons));
        setSupportActionBar(myToolbar);

        heroPortrait = (ImageView) this.findViewById(R.id.heroPortrait);
        comment = (EditText) findViewById(R.id.heroComment);
        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                heroRoll.comment = s.toString();
                collection.save(getBaseContext());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //do nothing
            }
        });

        hp = (TextView) findViewById(R.id.hero40LineHP);
        atk = (TextView) findViewById(R.id.hero40LineAtk);
        spd = (TextView) findViewById(R.id.hero40LineSpd);
        def = (TextView) findViewById(R.id.hero40LineDef);
        res = (TextView) findViewById(R.id.hero40LineRes);
        bst = (TextView) findViewById(R.id.hero40LineBST);
        equippedSkills = (LinearLayout) findViewById(R.id.equippedSkills);
        initViewHolder();
        onResume();
    }

    @NonNull
    private void initViewHolder() {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.skillLine = (LinearLayout) findViewById(R.id.skillLine);
        viewHolder.skillName = (TextView) findViewById(R.id.skillname);
        viewHolder.seekBar = (SeekBar) findViewById(R.id.skillslider);
        viewHolder.skillState = (TextView) findViewById(R.id.skillstate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbarmenu, menu);
        MenuItem nakedView = menu.findItem(R.id.toggleNakedView);
        if (!skillOn) {
            nakedView.setTitle(R.string.skills_on);
            nakedView.getIcon().setAlpha(130);
        } else {
            nakedView.setTitle(R.string.no_skills);
            nakedView.getIcon().setAlpha(255);
        }
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_hero;
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawHeroPortrait();
        showComment();
        calculateHeroStats();
        showSkills();

        adAdBanner();
        //disableAdBanner();
    }

    //this whole chunk of code is redundant with CollectionActivity's
    private void calculateHeroStats() {
        int[] mods = calculateMods(heroRoll.hero, 40, !skillOn);
        makePopupStat(hp, heroRoll, heroRoll.hero.HP, mods[0], getResources().getString(R.string.hp));
        makePopupStat(atk, heroRoll, heroRoll.hero.atk, mods[1], getResources().getString(R.string.atk));
        makePopupStat(spd, heroRoll, heroRoll.hero.speed, mods[2], getResources().getString(R.string.spd));
        makePopupStat(def, heroRoll, heroRoll.hero.def, mods[3], getResources().getString(R.string.def));
        makePopupStat(res, heroRoll, heroRoll.hero.res, mods[4], getResources().getString(R.string.res));

        int totalMods = mods[0] + mods[1] + mods[2] + mods[3] + mods[4];
        String bstText = heroRoll.getBST(getBaseContext()) < 0 ? "?" : heroRoll.getBST(getBaseContext()) - totalMods + "";
        bst.setText(getResources().getString(R.string.bst) + " " + bstText);
        bst.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    public void makePopupStat(TextView statTV, HeroRoll hero, int[] stat, int mod, String statName) {

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

    private String makeText(int i) {
        if (i < 0)
            return "?";
        else
            return i + "";
    }

    private void showSkills() {
        if (skillOn && heroRoll.hero.skills40 != null) {
            updateSkillView(equippedSkills, heroRoll.hero.skills40);
            equippedSkills.setVisibility(View.VISIBLE);
        } else {
            equippedSkills.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggleNakedView:
                //TODO change showSkills into ShowSkillManagement (change onCreateOptionsMenu too)
                //open overlay
                //modify sliders
                //check if skill slot already has something and unequip/change slider if yes
                //close overlay
                //calculate and show new skills and stats
                skillOn = !skillOn;
                showSkills();
                calculateHeroStats();
                invalidateOptionsMenu();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void showComment() {
        if (heroRoll.comment == null) {
            comment.setHint(R.string.comment);
        } else {
            comment.setText(heroRoll.comment);
        }
    }

    private void drawHeroPortrait() {
        int drawableId = this.getResources().getIdentifier(heroRoll.hero.name.toLowerCase(), "drawable", this.getPackageName());
        Bitmap b = BitmapFactory.decodeResource(this.getResources(), drawableId);

        int circleRadius = Math.min(100, Math.min(b.getHeight(), b.getWidth()));
        Bitmap c = getRoundedCroppedBitmap(b, circleRadius, circleRadius);
        Drawable drawable = new BitmapDrawable(this.getResources(), c);
        heroPortrait.setImageDrawable(drawable);
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

    static class ViewHolder {
        LinearLayout skillLine;
        TextView skillName;
        SeekBar seekBar;
        TextView skillState;
    }
}
