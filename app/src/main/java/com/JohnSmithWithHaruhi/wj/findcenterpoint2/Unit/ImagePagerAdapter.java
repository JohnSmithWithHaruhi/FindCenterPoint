package com.JohnSmithWithHaruhi.wj.findcenterpoint2.Unit;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by wj on 16/3/29.
 */

public class ImagePagerAdapter extends PagerAdapter {

    public ImagePagerAdapter(Context context){
        this.context = context;
    }

    private Context context;
    private ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();

    public void add(Bitmap photo){
        bitmapArrayList.add(photo);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(bitmapArrayList.get(position));
        container.addView(imageView);
        return imageView;
    }

    @Override
    public int getCount() {
        return bitmapArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
