package info.gomi.gomi001;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchAdActivity extends AppCompatActivity {

    RecyclerView mRecylaRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ad);

        //action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Advertisement List");
        mRecylaRecyclerView = findViewById(R.id.serachReayclerView);
        mRecylaRecyclerView.setHasFixedSize(true);

        mRecylaRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRef  = FirebaseDatabase.getInstance().getReference("post_ad_details");
        //mRef = mFirebaseDatabase.


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Model> options=
                new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(mRef,Model.class)
                .build();
        FirebaseRecyclerAdapter<Model,GetAdDetailsViewHolder> adpter=
                new FirebaseRecyclerAdapter<Model, GetAdDetailsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull GetAdDetailsViewHolder holder, int position, @NonNull Model model)
                    {
                        holder.mItemTypeView.setText(model.getItemType());
                        holder.mItemNameView.setText(model.getItemName());
                        holder.mUserNameView.setText(model.getUserName());
                        holder.mpriceView.setText(model.getPrice());
                        holder.mPhoneNo.setText(model.getPhoneNo());
                        Picasso.get().load(model.getAdImageUrl()).into(holder.mImageView);


                    }

                    @NonNull
                    @Override
                    public GetAdDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row,viewGroup,false);
                        GetAdDetailsViewHolder viewHolder=new GetAdDetailsViewHolder(view);
                        return viewHolder;
                    }
                };
        mRecylaRecyclerView.setAdapter(adpter);
        adpter.startListening();
    }

    public static  class  GetAdDetailsViewHolder extends  RecyclerView.ViewHolder {

       TextView mItemTypeView, mItemNameView,mUserNameView, mpriceView, mPhoneNo;
       ImageView mImageView;
        public GetAdDetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            mItemTypeView=itemView.findViewById(R.id.rTypeOfitem);
            mItemNameView=itemView.findViewById(R.id.ritemName);
            mUserNameView=itemView.findViewById(R.id.rUserName);
            mpriceView=itemView.findViewById(R.id.rPrice);
            mPhoneNo=itemView.findViewById(R.id.rPhoneNo);
            mImageView=itemView.findViewById(R.id.rImageview);


        }
    }
}
