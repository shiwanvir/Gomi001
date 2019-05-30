package info.gomi.gomi001;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MailReadActivity extends AppCompatActivity {
    LinearLayoutManager mLayoutManager;//for sorting purposes
    SharedPreferences mSharedPref;//for saving sort settings
    RecyclerView mRecylaRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_read);

        //action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Messages");


        mSharedPref=getSharedPreferences("SortSettings",MODE_PRIVATE);
        String mSorting=mSharedPref.getString("Sort","newest");// default newst adds will showing first

        if(mSorting.equals("newest")){
            mLayoutManager=new LinearLayoutManager(this);
            //load newest items first
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);

        }

        else if(mSorting.equals("oldest")){

            mLayoutManager=new LinearLayoutManager(this);
            //load oldest items first
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);
        }



        mRecylaRecyclerView = findViewById(R.id.mailReayclerView);
        mRecylaRecyclerView.setHasFixedSize(true);

        mRecylaRecyclerView.setLayoutManager(mLayoutManager);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId=user.getUid();


        mRef  =FirebaseDatabase.getInstance().getReference("contact_us_details");
        //mRef = mFirebaseDatabase.






    }

    private void firebaseSearch(String searchText){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId=user.getUid();
        String query=searchText.toLowerCase();
        Query firebaseSearchQuery =mRef.orderByChild("subject").startAt(query).endAt(query+"\uf8ff");

        FirebaseRecyclerOptions<ModelContctUs> options=
                new FirebaseRecyclerOptions.Builder<ModelContctUs>()
                        .setQuery(firebaseSearchQuery,ModelContctUs.class)
                        .build();
        FirebaseRecyclerAdapter<ModelContctUs,GetAdDetailsViewHolder> adpter=
                new FirebaseRecyclerAdapter<ModelContctUs, GetAdDetailsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull GetAdDetailsViewHolder holder, int position, @NonNull ModelContctUs model)
                    {
                        holder.mEmailView.setText(model.getEmail());
                        holder.mMessageiew.setText(model.getMessage());
                        holder.mPhoneNoView.setText(model.getPhoneNo());
                        holder.mSubjectView.setText(model.getSubject());
                        holder.mUserIdview.setText(model.getUserId());
                    }

                    @NonNull
                    @Override
                    public GetAdDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_contact_us,viewGroup,false);
                        GetAdDetailsViewHolder viewHolder=new GetAdDetailsViewHolder(view);
                        viewHolder.SetOnclickListner(new GetAdDetailsViewHolder.ClickListner() {
                            @Override
                            public void onItemClick(View view, int position) {
                                TextView mEmailTv=view.findViewById(R.id.rcEmail);
                                TextView mSubjectTv=findViewById(R.id.rcSubject);
                                TextView mUserIDTv=findViewById(R.id.rCUserId);


                                String to=mEmailTv.getText().toString();
                                String subject=mSubjectTv.getText().toString();
                                //Log.i("","recipients"+recipients);
                                //Log.i("","subject"+subject);



                                //phrase data to new activity
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_EMAIL,new String[]{ to});
                                intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                                intent.setType("message/rfc822");
                                startActivity(Intent.createChooser(intent,"choose an email client"));



                            }

                            @Override
                            public void onItemLongclick(View view, int position) {

                            }
                        });


                        return viewHolder;
                    }
                };
        mRecylaRecyclerView.setAdapter(adpter);
        adpter.startListening();


    }



    @Override
    protected void onStart() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId=user.getUid();

        super.onStart();
        Query query=FirebaseDatabase.getInstance().getReference("contact_us_details")
                .orderByChild("userId").equalTo(userId);

        FirebaseRecyclerOptions<ModelContctUs> options=
                new FirebaseRecyclerOptions.Builder<ModelContctUs>()
                        .setQuery(query,ModelContctUs.class)
                        .build();
        FirebaseRecyclerAdapter<ModelContctUs,GetAdDetailsViewHolder> adpter=
                new FirebaseRecyclerAdapter<ModelContctUs, GetAdDetailsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull GetAdDetailsViewHolder holder, int position, @NonNull ModelContctUs model)
                    {
                        holder.mEmailView.setText(model.getEmail());
                        holder.mMessageiew.setText(model.getMessage());
                        holder.mPhoneNoView.setText(model.getPhoneNo());
                        holder.mSubjectView.setText(model.getSubject());
                        holder.mUserIdview.setText(model.getUserId());


                    }

                    @NonNull
                    @Override
                    public GetAdDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_contact_us,viewGroup,false);
                        GetAdDetailsViewHolder viewHolder=new GetAdDetailsViewHolder(view);
                        viewHolder.SetOnclickListner(new GetAdDetailsViewHolder.ClickListner() {
                            @Override
                            public void onItemClick(View view, int position) {
                                TextView mEmailTv=view.findViewById(R.id.rcEmail);
                                TextView mSubjectTv=findViewById(R.id.rcSubject);
                                TextView mUserIDTv=findViewById(R.id.rCUserId);


                                String to=mEmailTv.getText().toString();

                                String subject=mSubjectTv.getText().toString();
                                String mUserId=mUserIDTv.getText().toString();

                                //Log.i("recipient  is ","recipients="+to);
                                //Log.i("subject is ","subject="+subject);

                                //phrase data to new activity
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_EMAIL,new String[]{ to});
                                intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                                intent.setType("message/rfc822");
                                startActivity(Intent.createChooser(intent,"choose an email client"));




                            }

                            @Override
                            public void onItemLongclick(View view, int position) {

                            }
                        });


                        return viewHolder;
                    }
                };
        mRecylaRecyclerView.setAdapter(adpter);
        adpter.startListening();
    }

    public static  class  GetAdDetailsViewHolder extends  RecyclerView.ViewHolder {

        TextView mUserIdview, mEmailView,mPhoneNoView, mMessageiew,mSubjectView;

        public GetAdDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            mUserIdview=itemView.findViewById(R.id.rCUserId);
            mEmailView=itemView.findViewById(R.id.rcEmail);
            mPhoneNoView=itemView.findViewById(R.id.rcPhoneNo);
            mMessageiew=itemView.findViewById(R.id.rcMessage);
            mSubjectView=itemView.findViewById(R.id.rcSubject);



            //item Click
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListner.onItemClick(view, getAdapterPosition());
                }
            });
            //item long click
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mClickListner.onItemLongclick(view,getAdapterPosition());
                    return true;
                }
            });






        }
        private  ClickListner mClickListner;


        //interface to sendCallbacks

        public  interface ClickListner{

            void onItemClick(View view,int position);
            void onItemLongclick(View view,int position);


        }

        public  void SetOnclickListner(ClickListner clickListner){
            mClickListner=clickListner;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item=menu.findItem(R.id.action_serach);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //filtering against the text
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        //handle other action bar item clicks here


        if(id==R.id.action_sort){
            //show dialog
            showSortDialog();

            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {

        String[] sortOptions={"Newest","Oldest"};
        // alert dialog creation

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Sort by")
                .setIcon(R.drawable.ic_action_sort)
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 0 indicates newst post 1indicates oldest
                        if(which==0){
                            //sort by newst

                            SharedPreferences.Editor editor=mSharedPref.edit();
                            editor.putString("Sort","newest");
                            editor.apply();
                            recreate();

                        }
                        else if(which==1){
                            //sort by oldest

                            SharedPreferences.Editor editor=mSharedPref.edit();
                            editor.putString("Sort","oldest");
                            editor.apply();
                            recreate();

                        }
                    }
                });
        builder.show();




    }


}
