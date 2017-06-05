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
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.io.IOException;

import c4stor.com.feheroes.R;
import c4stor.com.feheroes.activities.ToolbaredActivity;
import c4stor.com.feheroes.activities.collection.CollectionActivity;
import c4stor.com.feheroes.model.HeroCollection;
import c4stor.com.feheroes.model.HeroRoll;

/**
 * Created by eclogia on 04/06/17.
 */

public class HeroPageActivity extends ToolbaredActivity {

    private HeroRoll heroRoll;
    private ImageView heroPortrait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        Intent intent = getIntent();
        int heroPosition = Integer.valueOf(intent.getStringExtra("position"));
        this.heroRoll = collection.get(heroPosition);

        myToolbar.setTitle(heroRoll.getDisplayName(this));
        myToolbar.setTitleTextColor(getResources().getColor(R.color.icons));
        setSupportActionBar(myToolbar);


        //heroPortrait = (ImageView) this.findViewById(R.id.heroPortrait);

        onResume();
    }

    @Override
    protected boolean isHeroPage(){
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_hero;
    }

    private void adAdBanner() {
        PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdViewColl);
        PublisherAdRequest.Builder b = new PublisherAdRequest.Builder();
        PublisherAdRequest adRequest = b.build();
        mPublisherAdView.loadAd(adRequest);
    }


    private void disableAdBanner() {
        PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdViewColl);
        mPublisherAdView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        collection = HeroCollection.loadFromStorage(getBaseContext());
        try {
            initHeroData();
        } catch (IOException e) {

        }
        adAdBanner();
        //disableAdBanner();
    }

    private void drawHeroPortrait() {
        int drawableId = this.getResources().getIdentifier(heroRoll.hero.name.toLowerCase(), "drawable", this.getPackageName());
        Bitmap b = BitmapFactory.decodeResource(this.getResources(), drawableId);

        int circleRadius = Math.min(140, Math.min(b.getHeight() / 5, b.getWidth() / 5));
        Bitmap c = getRoundedCroppedBitmap(b, circleRadius, circleRadius);
        Drawable dCool = new BitmapDrawable(this.getResources(), c);

        //holder.collImage.setImageDrawable(dCool);
        //holder.collName.setText(hero.getDisplayName(getContext()));
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
}
