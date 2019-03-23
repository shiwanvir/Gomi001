package info.gomi.gomi001;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        mView=itemView;
    }

    public void setDetails(Context ctx,String itemType,String itemName,String userName,String price,String telephoneNo,String image){

        TextView mItemTypeView=mView.findViewById(R.id.rTypeOfitem);
        TextView mItemNameView=mView.findViewById(R.id.ritemName);
        TextView mUserNameView=mView.findViewById(R.id.rUserName);
        TextView mpriceView=mView.findViewById(R.id.rPrice);
        TextView mPhoneNo=mView.findViewById(R.id.rPhoneNo);
        ImageView mImageView=mView.findViewById(R.id.rImageview);

        mItemTypeView.setText(itemType);
        mItemNameView.setText(itemName);
        mUserNameView.setText(userName);
        mpriceView.setText(price);
        mPhoneNo.setText(telephoneNo);
        Picasso.get().load(image).into(mImageView);


    }
}
