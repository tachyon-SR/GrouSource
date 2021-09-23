package com.grousale.grousource.viewHolder;

import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grousale.grousource.R;

public class productViewHolder extends RecyclerView.ViewHolder {
   public ImageView productImage;
   public TextView productName,shopName,shopAddress;

    public productViewHolder(@NonNull View itemView) {
        super(itemView);

        productImage = itemView.findViewById(R.id.prodImage);
        productName = itemView.findViewById(R.id.prodName);
        shopName = itemView.findViewById(R.id.prodShopName);
        shopAddress=itemView.findViewById(R.id.prodShopAddress);
    }



}
