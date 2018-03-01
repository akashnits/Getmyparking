package com.example.akash.getmyparking;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.akash.getmyparking.utils.PictureImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageAdapterViewHolder> {


    private Context mContext;
    private String[] mImagesData;
    private int mWidth;
    private int mHeight;

    public static final String TAG = ImageAdapter.class.getSimpleName();


    public ImageAdapter(Context context) {
        mContext = context;
        mWidth= 100;
        mHeight= 100;
    }

    @Override
    public ImageAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //commit
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_image, parent, false);
        view.setFocusable(true);
        return new ImageAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageAdapterViewHolder holder, int position) {
        holder.itemView.setTag(position);

        Picasso.with(mContext).load(mImagesData[position])
                .resize(mWidth, mHeight).placeholder(R.drawable.loading).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (mImagesData == null)
            return 0;
        return mImagesData.length;
    }

    class ImageAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView)
        PictureImageView imageView;
        public ImageAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setImagessData(String[] data) {
        mImagesData = data;
        notifyDataSetChanged();
    }


    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }
}