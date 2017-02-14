package c4stor.com.feheroes;

import android.content.Context;
import android.content.res.Resources;
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
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Nicolas on 14/02/2017.
 */

public class SpinnerHeroesAdapter extends ArrayAdapter<JsonHero> {

    public SpinnerHeroesAdapter(Context context, JsonHero[] values) {
        super(context, android.R.layout.simple_spinner_item, values);

    }


    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    private TextView makeView(int position, View convertView, ViewGroup parent, int imageWidth, int imageHeight) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setGravity(Gravity.CENTER);
        JsonHero jsonHero = getItem(position);
        int nameIdentifier = getContext().getResources().getIdentifier(jsonHero.name.toLowerCase(),"string",getContext().getPackageName());
        label.setText(capitalize(getContext().getString(nameIdentifier)));
        int drawableId = getContext().getResources().getIdentifier(jsonHero.name.toLowerCase(), "drawable", getContext().getPackageName());
//        Drawable d = ContextCompat.getDrawable(getContext(),drawableId);
        Bitmap b = BitmapFactory.decodeResource(getContext().getResources(), drawableId);
        Bitmap c = getRoundedCroppedBitmap(b, imageWidth, imageHeight);
        Drawable dCool = new BitmapDrawable(getContext().getResources(), c);
        dCool.setBounds(imageHeight, imageHeight, imageHeight,imageHeight);
//        dCool.set
        label.setCompoundDrawablesWithIntrinsicBounds(dCool, null, null, null);
label.setPadding(0,2,0,0);
        return label;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       TextView v = makeView(position, convertView, parent, 140, 140);
        v.setHeight(180);
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
       return makeView(position, convertView, parent, 80, 80);
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

