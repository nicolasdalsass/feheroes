package c4stor.com.feheroes.activities.heropage;

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
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import c4stor.com.feheroes.R;
import c4stor.com.feheroes.activities.ToolbaredActivity;
import c4stor.com.feheroes.model.hero.Hero;
import c4stor.com.feheroes.model.hero.HeroCollection;
import c4stor.com.feheroes.model.hero.HeroRoll;
import c4stor.com.feheroes.model.hero.MovementType;
import c4stor.com.feheroes.model.skill.HeroSkill;
import c4stor.com.feheroes.model.skill.Skill;
import c4stor.com.feheroes.model.skill.SkillState;
import c4stor.com.feheroes.model.skill.WeaponType;

/**
 * Created by eclogia on 04/06/17.
 */

public class HeroPageActivity extends ToolbaredActivity {

    private HeroRoll heroRoll;
    private ImageView heroPortrait;
    private ImageView movementIcon;
    private ImageView weaponIcon;
    private EditText comment;
    private TextView hp;
    private TextView atk;
    private TextView spd;
    private TextView def;
    private TextView res;
    private TextView bst;
    protected boolean managementOn = false;
    private LinearLayout equippedSkillsLayout;
    private Map<HeroSkill, SeekBar> seekBarMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        Intent intent = getIntent();
        int heroPosition = intent.getIntExtra("position", 1);
        singleton.collection = HeroCollection.loadFromStorage(getBaseContext());//TODO check if this line is useless
        this.heroRoll = singleton.collection.get(heroPosition);

        setToolbar(myToolbar);

        findViewsById();

        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                heroRoll.comment = s.toString();
                singleton.collection.save(getBaseContext());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //do nothing
            }
        });
        seekBarMap = new HashMap<>();

        if (!heroRoll.hasOpenedPageOnce){
            heroRoll.hasOpenedPageOnce = true;
            initHeroRollSkills();
        }

        onResume();
    }

    private void findViewsById() {
        heroPortrait = (ImageView) this.findViewById(R.id.heroPortrait);
        movementIcon = (ImageView) this.findViewById(R.id.movementIcon);
        weaponIcon = (ImageView) this.findViewById(R.id.weaponIcon);
        comment = (EditText) findViewById(R.id.heroComment);

        hp = (TextView) findViewById(R.id.hero40LineHP);
        atk = (TextView) findViewById(R.id.hero40LineAtk);
        spd = (TextView) findViewById(R.id.hero40LineSpd);
        def = (TextView) findViewById(R.id.hero40LineDef);
        res = (TextView) findViewById(R.id.hero40LineRes);
        bst = (TextView) findViewById(R.id.hero40LineBST);

        equippedSkillsLayout = (LinearLayout) findViewById(R.id.equippedSkills);
    }

    private void initHeroRollSkills() {
        Hero hero = heroRoll.hero;
        if (heroRoll.skills.isEmpty()) {
            addActiveSkillChain(hero.weaponChain, heroRoll);
            addActiveSkillChain(hero.assistChain, heroRoll);
            addActiveSkillChain(hero.specialChain, heroRoll);
            addPassiveSkillChain(hero.aChain, heroRoll);
            addPassiveSkillChain(hero.bChain, heroRoll);
            addPassiveSkillChain(hero.cChain, heroRoll);
        }
        //complexity 2 * native skill list length (usually 13)
        for (Integer id : heroRoll.hero.skills1) {
            for (HeroSkill s : heroRoll.skills) {
                if (s.id == id) {
                    s.skillState = SkillState.EQUIPPED;
                    heroRoll.equippedSkills.add(s);
                }
            }
        }
    }

    private void addPassiveSkillChain(List<Integer> chain, HeroRoll heroRoll) {
        boolean reachedMaxSkill = false;
        List<Integer> copy = new ArrayList<>();
        for (Integer id : chain) {
            if (id == 0) { //In case someone put a int[] somewhere sometime
                copy.addAll(chain);
                break;
            } else {
                Skill s = singleton.skillsMap.get(id);
                if (heroRoll.hero.skills40.contains(s.id)) {
                    heroRoll.skills.add(new HeroSkill(s, SkillState.LEARNABLE));
                    reachedMaxSkill = true;
                } else if (!reachedMaxSkill) {
                    heroRoll.skills.add(new HeroSkill(s, SkillState.LEARNABLE));
                } else {
                    heroRoll.skills.add(new HeroSkill(s, SkillState.TO_INHERIT));
                }
            }
        }
        if (!copy.isEmpty()) {
            chain.removeAll(copy);
        }
    }

    private void addActiveSkillChain(List<Integer> chain, HeroRoll heroRoll) {
        boolean reachedDefaultSkill = false;
        boolean reachedMaxSkill = false;
        List<Integer> copy = new ArrayList<>();
        for (Integer id : chain) {
            if (id == 0) { //In case someone put a int[] somewhere sometime
                copy.addAll(chain);
                break;
            } else {
                Skill s = singleton.skillsMap.get(id);
                if (heroRoll.hero.skills1.contains(s.id)) {
                    heroRoll.skills.add(new HeroSkill(s, SkillState.EQUIPPED));
                    reachedDefaultSkill = true;
                    if (heroRoll.hero.skills40.contains(s.id))
                        reachedMaxSkill = true;
                } else if (!heroRoll.hero.skills1.contains(s.id) && heroRoll.hero.skills40.contains(s.id)) {
                    heroRoll.skills.add(new HeroSkill(s, SkillState.LEARNABLE));
                    reachedDefaultSkill = true;
                    reachedMaxSkill = true;
                } else if (!reachedDefaultSkill) {
                    heroRoll.skills.add(new HeroSkill(s, SkillState.LEARNED));
                } else if (heroRoll.hero.skills40.contains(s.id)) {
                    heroRoll.skills.add(new HeroSkill(s, SkillState.LEARNABLE));
                    reachedMaxSkill = true;
                } else if (!reachedMaxSkill) {
                    heroRoll.skills.add(new HeroSkill(s, SkillState.LEARNABLE));
                } else {
                    heroRoll.skills.add(new HeroSkill(s, SkillState.TO_INHERIT));
                }
            }
        }
        if (!copy.isEmpty()) {
            chain.removeAll(copy);
        }
    }

    private void setToolbar(Toolbar myToolbar) {
        StringBuilder sb = new StringBuilder(heroRoll.getDisplayName(this));
        sb.append(" ");
        for (int i = 0; i < heroRoll.stars; i++) {
            sb.append("★");
        }
        myToolbar.setTitle(sb.toString());
        myToolbar.setTitleTextColor(getResources().getColor(R.color.icons));
        setSupportActionBar(myToolbar);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_hero;
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawHeroPortrait();
        setMovementIcon(movementIcon, heroRoll.hero.movementType);
        setWeaponIcon(weaponIcon, heroRoll.hero.weaponType);
        showComment();
        calculateHeroStats();
        showEquippedSkills();

        adAdBanner();
        //disableAdBanner();
    }

    private void setMovementIcon(ImageView view, MovementType movementType) {
        Drawable d;
        switch(movementType){
            case CAVALRY:
                d = getResources().getDrawable(R.drawable.move_cavalry);
                break;
            case FLIER:
                d = getResources().getDrawable(R.drawable.move_flier);
                break;
            case ARMOR:
                d = getResources().getDrawable(R.drawable.move_armor);
                break;
            default:
                d = getResources().getDrawable(R.drawable.move_infantry);
                break;
        }
        view.setImageDrawable(d);
    }

    private void setWeaponIcon(ImageView view, WeaponType weaponType) {
        Drawable d;
        switch(weaponType){
            case SWORD:
                d = getResources().getDrawable(R.drawable.red_sword);
                break;
            case RTOME:
                d = getResources().getDrawable(R.drawable.red_tome);
                break;
            case RBREATH:
                d = getResources().getDrawable(R.drawable.red_breath);
                break;
            case LANCE:
                d = getResources().getDrawable(R.drawable.blue_lance);
                break;
            case BTOME:
                d = getResources().getDrawable(R.drawable.blue_tome);
                break;
            case BBREATH:
                d = getResources().getDrawable(R.drawable.blue_breath);
                break;
            case AXE:
                d = getResources().getDrawable(R.drawable.green_axe);
                break;
            case GTOME:
                d = getResources().getDrawable(R.drawable.green_tome);
                break;
            case GBREATH:
                d = getResources().getDrawable(R.drawable.green_breath);
                break;
            case DAGGER:
                d = getResources().getDrawable(R.drawable.colorless_dagger);
                break;
            case BOW:
                d = getResources().getDrawable(R.drawable.colorless_bow);
                break;
            default:
                d = getResources().getDrawable(R.drawable.colorless_staff);
                break;
        }
        view.setImageDrawable(d);
    }

    private void showSkillManagement() {
        ListView v = (ListView) findViewById(R.id.fullSkillList);
        Parcelable state = v.onSaveInstanceState();
        if (managementOn && !heroRoll.skills.isEmpty()) {
           SkillManagerAdapter adapter = new SkillManagerAdapter(getBaseContext(),
                   R.layout.hero_skill_list_line, heroRoll.skills);
            v.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            v.onRestoreInstanceState(state);
            v.setVisibility(View.VISIBLE);
        } else
            v.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.heropagetoolbar, menu);
        return true;
    }

    public class SkillManagerAdapter extends ArrayAdapter<HeroSkill> {

        private boolean hasBeenOpenedOnce = false;

        public SkillManagerAdapter(@NonNull Context context, @LayoutRes int resource, List<HeroSkill> skillList) {
            super(context, resource, skillList);
        }

        public void equipSkill(HeroSkill newSkill) {
            for (HeroSkill oldSkill : heroRoll.equippedSkills) {
                if (oldSkill.skillType == newSkill.skillType) {
                    oldSkill.skillState = SkillState.LEARNED;

System.out.println("CHANGING OLD " + oldSkill.name + " " + System.identityHashCode(oldSkill)
        + " " + seekBarMap.containsKey(oldSkill));//TODO remove print

                    seekBarMap.get(oldSkill).setProgress(oldSkill.skillState.stateNumber);
                    heroRoll.equippedSkills.remove(oldSkill);
                    heroRoll.equippedSkills.add(newSkill);
                    return;
                }
            }
            heroRoll.equippedSkills.add(newSkill);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final ViewHolder holder; // to reference the child views for later actions

            if (v == null) {
                LayoutInflater vi =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.hero_skill_list_line, null);
                // cache view fields into the holder
                holder = initViewHolder(v, heroRoll.skills.get(position));
                // associate the holder with the view for later lookup
                v.setTag(holder);
            } else {
                // view already exists, get the holder instance from the view
                holder = (ViewHolder) v.getTag();
            }
//this code snippet prevents the app from crashing but the seekbars sometimes do not update correctly
            if (!hasBeenOpenedOnce) {
                hasBeenOpenedOnce = true;
                for (HeroSkill s : heroRoll.equippedSkills) {
                    //seekBarMap.remove(holder.skill);
                    seekBarMap.put(s, holder.seekBar);
System.out.println("PUT EQUIPPED SKILL " + s.name + " " + System.identityHashCode(s) + " " + s.skillState);//TODO remove print
                }
            }

            holder.skillName.setText(holder.skill.name);
            holder.skillStateText.setText(holder.skill.skillState.stateStringId);
            holder.seekBar.setProgress(holder.skill.skillState.stateNumber);
            holder.seekBar.setMax(SkillState.values().length - 1);
            setOnSeekBarChangeListener(holder);
            return v;
        }

        public void setOnSeekBarChangeListener(final ViewHolder holder) {
            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress >= 0 && progress < SkillState.values().length) {
                        SkillState newState = SkillState.getStateFromIndex(progress);
                        SkillState oldState = holder.skill.skillState;
                        holder.skill.skillState = newState;
                        holder.skillStateText.setText(newState.stateStringId);
                        if (oldState == SkillState.EQUIPPED && newState != SkillState.EQUIPPED) {

System.out.println("UNEQUIPPING " + holder.skill.name + " " + System.identityHashCode(holder.skill)
        + " " + seekBarMap.containsKey(holder.skill));//TODO remove print

                            heroRoll.equippedSkills.remove(holder.skill);
                        }
                        else if (newState == SkillState.EQUIPPED) {

System.out.println("EQUIPPING " + holder.skill.name + " " + System.identityHashCode(holder.skill)
        + " " + seekBarMap.containsKey(holder.skill));//TODO remove print

                            equipSkill(holder.skill);
                        }
                        calculateHeroStats();
                        singleton.collection.save(getBaseContext());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        private ViewHolder initViewHolder(View v, HeroSkill s) {
            final ViewHolder holder = new ViewHolder();
            holder.skill = s;
            holder.skillLayout = (LinearLayout) v.findViewById(R.id.skillLine);
            holder.skillName = (TextView) v.findViewById(R.id.skillname);
            holder.seekBar = (SeekBar) v.findViewById(R.id.skillslider);
            holder.skillStateText = (TextView) v.findViewById(R.id.skillstate);

            seekBarMap.put(s, holder.seekBar);
            System.out.println("PUT " + s.name + " " + System.identityHashCode(s) + " " + s.skillState);//TODO remove print

            return holder;
        }
    }

    static class ViewHolder {
        HeroSkill skill;
        LinearLayout skillLayout;
        TextView skillName;
        SeekBar seekBar;
        TextView skillStateText;
    }

    //this whole chunk of code is redundant with CollectionActivity's
    protected void calculateHeroStats() {
        int[] mods = calculateMods(heroRoll, true);
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
            statTV.setText(statName + " " + makeText(stat[5] + mod));
            statTV.setTextColor(getResources().getColor(R.color.high_green));
        } else if (hero.banes != null && hero.banes.contains(statName)) {
            statTV.setText(statName + " " + makeText(stat[3] + mod));
            statTV.setTextColor(getResources().getColor(R.color.low_red));
        } else {
            statTV.setText(statName + " " + makeText(stat[4] + mod));
            statTV.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private String makeText(int i) {
        if (i < 0)
            return "?";
        else
            return i + "";
    }

    private void showEquippedSkills() {
        if (!managementOn && !heroRoll.equippedSkills.isEmpty()) {
            List<Integer> list = SkillListToIdArray(heroRoll.equippedSkills);
            updateSkillView(equippedSkillsLayout, list);
            equippedSkillsLayout.setVisibility(View.VISIBLE);
        } else {
            equippedSkillsLayout.setVisibility(View.GONE);
        }
    }

    private List<Integer> SkillListToIdArray(List<HeroSkill> list) {
        List<Integer> integers = new ArrayList<>(list.size());
        for( HeroSkill s : list ) {
            integers.add(s.id);
        }
        return integers;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggleNakedView:
                managementOn = !managementOn;
                //TODO calculate stats with new skills
                invalidateOptionsMenu();
                showSkillManagement();
                showEquippedSkills();
                //resetSeekbarMap();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void resetSeekbarMap() {
        List<HeroSkill> copy = new ArrayList<>();
        copy.addAll(seekBarMap.keySet());
        for (HeroSkill s : copy){
            seekBarMap.remove(s);
        }
    }

    private void showComment() {
        if (heroRoll.comment == null) {
            comment.setHint(R.string.comment);
            //comment.setHint(singleton.gson.toJson(heroRoll));
        } else {
            comment.setText(heroRoll.comment);
        }
    }

    private void drawHeroPortrait() {
        int drawableId = this.getResources().getIdentifier(heroRoll.hero.name.toLowerCase(), "drawable", this.getPackageName());
        Bitmap b = BitmapFactory.decodeResource(this.getResources(), drawableId);

        int circleRadius = Math.min(100, Math.min(b.getHeight(), b.getWidth()));
        Bitmap c = getRoundedCroppedBitmap(b, circleRadius);
        Drawable drawable = new BitmapDrawable(this.getResources(), c);
        heroPortrait.setImageDrawable(drawable);
    }

    private Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int finalWidth) {
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
}
