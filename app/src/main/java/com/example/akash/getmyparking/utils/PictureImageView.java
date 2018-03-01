package com.example.akash.getmyparking.utils;

/**
 * Created by akash on 24/02/18.
 */
  import android.content.Context;
  import android.util.AttributeSet;
  import android.widget.ImageView;

public class PictureImageView extends ImageView{

        public PictureImageView(Context context) {
            super(context);
        }

        public PictureImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public PictureImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        }
    }

