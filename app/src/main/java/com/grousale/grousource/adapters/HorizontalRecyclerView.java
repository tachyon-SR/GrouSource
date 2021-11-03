package com.grousale.grousource.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grousale.grousource.R;

import java.util.ArrayList;

public class HorizontalRecyclerView extends RecyclerView.Adapter<HorizontalRecyclerView.HorizontalViewHolder> {

    private ArrayList<Uri> uri;

    public HorizontalRecyclerView(ArrayList<Uri> uri) {
        this.uri = uri;
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.single_image_item, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder horizontalViewHolder, int position) {
        horizontalViewHolder.mImageRecyclerView.setImageURI(uri.get(position));
    }

    @Override
    public int getItemCount() {
        return uri.size();
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageRecyclerView;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            mImageRecyclerView = itemView.findViewById(R.id.imageView);
        }
    }
}
