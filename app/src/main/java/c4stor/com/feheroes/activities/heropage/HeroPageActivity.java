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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
    EditText comment;
    Button saveComment;


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
        PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
        PublisherAdRequest.Builder b = new PublisherAdRequest.Builder();
        PublisherAdRequest adRequest = b.build();
        mPublisherAdView.loadAd(adRequest);
    }


    private void disableAdBanner() {
        PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
        mPublisherAdView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawHeroPortrait();
        showComment();
        adAdBanner();
        //disableAdBanner();
    }

    private void showComment() {
        if (heroRoll.getComment() == null) {
            comment.setHint(R.string.comment);
        } else {
            comment.setText(heroRoll.getComment());
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

        return Bitmap.createScaledBitmap(
                output, finalWidth, finalWidth, true);
    }

    private View.OnClickListener saveCommentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String commentString = comment.getText().toString();
            Toast.makeText(getBaseContext(), commentString, Toast.LENGTH_SHORT).show();
            heroRoll.setComment(commentString);
            collection.save(getBaseContext());
        }
    };
}
