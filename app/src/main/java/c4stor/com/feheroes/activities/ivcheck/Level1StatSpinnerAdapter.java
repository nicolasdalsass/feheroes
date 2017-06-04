package c4stor.com.feheroes.activities.ivcheck;

import android.database.DataSetObserver;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import c4stor.com.feheroes.R;

/**
 * Created by Nicolas on 09/02/2017.
 */

public class Level1StatSpinnerAdapter implements SpinnerAdapter {

    private int[] baseStat;
    private int mod;

    public Level1StatSpinnerAdapter(int[] baseStat, int mod) {
        this.baseStat = baseStat;
        this.mod = mod;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return baseStat[2 - position] - mod;
    }

    @Override
    public long getItemId(int position) {
        return baseStat[2 - position] - mod;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = new TextView(parent.getContext());
        String text;
        if(baseStat[2-position]==-1){
            text="?";
        } else {
            text = (baseStat[2 - position] - mod) + "";
        }
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setGravity(Gravity.CENTER);
        int color = 0;
        switch (position) {
            case 0:
                color = parent.getContext().getResources().getColor(R.color.high_green);
                break;
            case 1:
                color = parent.getContext().getResources().getColor(R.color.colorPrimary);
                break;
            case 2:
                color = parent.getContext().getResources().getColor(R.color.low_red);
                break;

        }
        if (tv.getText() == "?")
            color = parent.getContext().getResources().getColor(R.color.divider);
        tv.setTextColor(color);
        //ViewGroup.LayoutParams params = tv.getLayoutParams();
        tv.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        return tv;
    }

    @Override
    public int getItemViewType(int position) {
        return IGNORE_ITEM_VIEW_TYPE;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
