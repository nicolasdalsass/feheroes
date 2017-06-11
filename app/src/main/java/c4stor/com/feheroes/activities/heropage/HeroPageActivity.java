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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import c4stor.com.feheroes.R;
import c4stor.com.feheroes.activities.ToolbaredActivity;
import c4stor.com.feheroes.model.HeroCollection;
import c4stor.com.feheroes.model.HeroRoll;

/**
 * Created by eclogia on 04/06/17.
 */

public class HeroPageActivity extends ToolbaredActivity {

    private HeroRoll heroRoll;
    private ImageView heroPortrait;
    private EditText comment;
    private Button saveComment;
    private TextView hp;
    private TextView atk;
    private TextView spd;
    private TextView def;
    private TextView res;
    private TextView bst;
    protected boolean skillsOn = true;
    private LinearLayout skills;

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
        saveComment = (Button)findViewById(R.id.saveButton);
        saveComment.setOnClickListener(saveCommentListener);
        hp = (TextView) findViewById(R.id.hero40LineHP);
        atk = (TextView) findViewById(R.id.hero40LineAtk);
        spd = (TextView) findViewById(R.id.hero40LineSpd);
        def = (TextView) findViewById(R.id.hero40LineDef);
        res = (TextView) findViewById(R.id.hero40LineRes);
        bst = (TextView) findViewById(R.id.hero40LineBST);
        skills = (LinearLayout) findViewById(R.id.heroSkills);

        onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbarmenu, menu);
        MenuItem nakedView = menu.findItem(R.id.toggleNakedView);
        if (!skillsOn) {
            nakedView.setTitle(R.string.no_skills);
            nakedView.getIcon().setAlpha(130);
        } else {
            nakedView.setTitle(R.string.skills_on);
            nakedView.getIcon().setAlpha(255);
        }
        return true;
    }

    @Override
    protected boolean isHeroPage(){
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
        int[] mods = calculateMods(heroRoll.hero, 40, !skillsOn);
        makePopupStat(hp, heroRoll, heroRoll.hero.HP, mods[0], getResources().getString(R.string.hp));
        makePopupStat(atk, heroRoll, heroRoll.hero.atk, mods[1], getResources().getString(R.string.atk));
        makePopupStat(spd, heroRoll, heroRoll.hero.speed, mods[2], getResources().getString(R.string.spd));
        makePopupStat(def, heroRoll, heroRoll.hero.def, mods[3], getResources().getString(R.string.def));
        makePopupStat(res, heroRoll, heroRoll.hero.res, mods[4], getResources().getString(R.string.res));

        int totalMods = mods[0] + mods[1] + mods[2] + mods[3] + mods[4];
        String bstText = heroRoll.getBST(getBaseContext()) < 0 ? "?" : heroRoll.getBST(getBaseContext()) - totalMods + "";
        bst.setText(
                getResources().getString(R.string.bst) + " " +
                        bstText);
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
        if (skillsOn && heroRoll.hero.skills40 != null) {
            updateSkillView(skills, heroRoll.hero.skills40);
            skills.setVisibility(View.VISIBLE);
        } else {
            skills.setVisibility(View.GONE);
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

    private View.OnClickListener saveCommentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            heroRoll.comment = comment.getText().toString();
            collection.save(getBaseContext());
        }
    };
}
