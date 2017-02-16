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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Nicolas on 14/02/2017.
 */

public class SpinnerHeroesAdapter extends ArrayAdapter<Hero> {

    public SpinnerHeroesAdapter(Context context, Hero[] values) {
        super(context, android.R.layout.simple_spinner_item, values);

    }


    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    private TextView makeView(int position, Context context, int imageWidth, int imageHeight) {
        TextView label = new TextView(context);
        label.setGravity(Gravity.CENTER_VERTICAL);
        label.setCompoundDrawablePadding(50);
        Hero hero = getItem(position);
        int nameIdentifier = getContext().getResources().getIdentifier(hero.name.toLowerCase(),"string",getContext().getPackageName());
        label.setText(capitalize(getContext().getString(nameIdentifier)));
        label.setTextSize(20);
        int drawableId = getContext().getResources().getIdentifier(hero.name.toLowerCase(), "drawable", getContext().getPackageName());
        Bitmap b = BitmapFactory.decodeResource(getContext().getResources(), drawableId);
        Bitmap c = getRoundedCroppedBitmap(b, imageWidth, imageHeight);
        Drawable dCool = new BitmapDrawable(getContext().getResources(), c);
        dCool.setBounds(imageHeight, imageHeight, imageHeight,imageHeight);
        label.setCompoundDrawablesWithIntrinsicBounds(dCool, null, null, null);
        label.setPadding(0,4,0,0);
        return label;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       TextView v = makeView(position, parent.getContext(), 140, 140);
        v.setHeight(180);
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
       return makeView(position,  parent.getContext(), 80, 80);
    }

    private Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int finalWidth, int finalWeight) {
        int widthLight = bitmap.getWidth();
        int heightLight = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);

        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));

        canvas.drawRoundRect(rectF, widthLight / 2 ,heightLight / 2,paintColor);

        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, 0, 0, paintImage);

        return  Bitmap.createScaledBitmap(
                output, finalWidth, finalWidth, true);
    }
}

