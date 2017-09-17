package com.zhuimeng.zoomimage;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zhuimeng.zoomimage.view.ZoomImageView;

public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private int[] mImages = new int[]{R.drawable.girl_01, R.drawable.girl_02, R.drawable.girl_03,
            R.drawable.girl_04,R.drawable.game01, R.drawable.game02, R.drawable.game03,
            R.drawable.game04, R.drawable.game05, R.drawable.game06};
    private ImageView[] mImageViews = new ImageView[mImages.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setAdapter(new PagerAdapter() {//此处只做演示，未做viewHolder优化,放太多图片容易OOM

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ZoomImageView imageView = new ZoomImageView(getApplicationContext());
                imageView.setImageResource(mImages[position]);
                container.addView(imageView);
                mImageViews[position] = imageView;
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mImageViews[position]);
            }

            @Override
            public int getCount() {
                return mImageViews.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
    }
}
