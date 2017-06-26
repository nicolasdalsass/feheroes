package c4stor.com.feheroes.activities.ivcheck;

import android.app.Activity;
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
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import c4stor.com.feheroes.model.hero.StarredHero;
import c4stor.com.feheroes.R;

/**
 * Created by Nicolas on 14/02/2017.
 */

public class SpinnerHeroesAdapter extends ArrayAdapter<StarredHero> {

    private int height;
    private int width;

    public SpinnerHeroesAdapter(Context context, StarredHero[] values) {
        super(context, android.R.layout.simple_spinner_item, values);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    private View makeView(int position, View convertView, Context context, int imageWidth) {
        View v = convertView;
        ViewHolderSpinner holder; // to reference the child views for later actions

        if (v == null) {
            LayoutInflater vi =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.spinner_item, null);
            // cache view fields into the holder
            holder = new ViewHolderSpinner();
            holder.heroIcon = (ImageView) v.findViewById(R.id.herospinnerimage);
            holder.heroName = (TextView) v.findViewById(R.id.herospinnertext);
            // associate the holder with the view for later lookup
            v.setTag(holder);
        } else {
            // view already exists, get the holder instance from the view
            holder = (ViewHolderSpinner) v.getTag();
        }


        StarredHero hero = getItem(position);
        int nameIdentifier = getContext().getResources().getIdentifier(hero.name.toLowerCase(), "string", getContext().getPackageName());
        holder.heroName.setText(capitalize(getContext().getString(nameIdentifier)));
        holder.heroName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        int drawableId = getContext().getResources().getIdentifier(hero.name.toLowerCase(), "drawable", getContext().getPackageName());
        Bitmap b = BitmapFactory.decodeResource(getContext().getResources(), drawableId);
        Bitmap c = getRoundedCroppedBitmap(b, imageWidth, imageWidth);
        Drawable dCool = new BitmapDrawable(getContext().getResources(), c);
        holder.heroIcon.setImageDrawable(dCool);
        v.setPadding(0,2,0,2);
        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return makeView(position, convertView, parent.getContext(),  Math.min(200,Math.min(height/5, width/5)));

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return makeView(position, convertView, parent.getContext(), Math.min(140, Math.min(height/8, width/8)));
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

    static class ViewHolderSpinner {
        ImageView heroIcon;
        TextView heroName;
    }
}

