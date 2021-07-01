package harshbarash.github.monetaru;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RequestShow extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference, databaseReference1, profileRef;
    RecyclerView recyclerView, recyclerView_profile;
    RequestMember requestMember;
    TextView requesttv;
    ImageButton back;
    EditText editText;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.fragment_find);

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }

        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Requests").child(currentUserId);
        profileRef = database.getReference("All Users");

        requestMember = new RequestMember();

        recyclerView_profile = findViewById(R.id.recylerview_profile);


        recyclerView_profile.setHasFixedSize(true);

        recyclerView_profile.setLayoutManager(new LinearLayoutManager(RequestShow.this));
        recyclerView = findViewById(R.id.recylerview_requestf3);
        requesttv = findViewById(R.id.requeststv);

        editText = findViewById(R.id.search_friend);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RequestShow.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        //   MediaController mediaController;
        //  recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search();
            }
        });

    }

    private void search() {
        String query = editText.getText().toString().toUpperCase();
        Query search = profileRef.orderByChild("name").startAt(query).endAt(query + "\uf0ff");

        FirebaseRecyclerOptions<AllUserMember> options1 =
                new FirebaseRecyclerOptions.Builder<AllUserMember>()
                        .setQuery(search, AllUserMember.class)
                        .build();

        FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {


                        final String postkey = getRef(position).getKey();

                        holder.setProfile(RequestShow.this, model.getName(), model.getUid(), model.getBio(), model.getUrl());


                        String name = getItem(position).getName();
                        String bio = getItem(position).getBio();
                        String url = getItem(position).getUrl();
                        String uid = getItem(position).getUid();


                        holder.viewUserprofile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (currentUserId.equals(uid)) {
                                    Intent intent = new Intent(RequestShow.this, MainActivity.class);
                                    startActivity(intent);


                                } else {
                                    Intent intent = new Intent(RequestShow.this, ShowUser.class);
                                    intent.putExtra("n", name);
                                    intent.putExtra("n", bio);
                                    intent.putExtra("u", url);
                                    intent.putExtra("uid", uid);
                                    startActivity(intent);
                                }
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.profile, parent, false);

                        return new ProfileViewholder(view);
                    }
                };


        firebaseRecyclerAdapter1.startListening();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(RequestShow.this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView_profile.setLayoutManager(gridLayoutManager);
        recyclerView_profile.setAdapter(firebaseRecyclerAdapter1);
    }


    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    requesttv.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                } else {
                    requesttv.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseRecyclerOptions<AllUserMember> options1 =
                new FirebaseRecyclerOptions.Builder<AllUserMember>()
                        .setQuery(profileRef, AllUserMember.class)
                        .build();

        FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {


                        final String postkey = getRef(position).getKey();

                        holder.setProfile(RequestShow.this, model.getName(), model.getUid(), model.getBio(), model.getUrl());


                        String name = getItem(position).getName();
                        String bio = getItem(position).getBio();
                        String url = getItem(position).getUrl();
                        String uid = getItem(position).getUid();


                        holder.viewUserprofile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(RequestShow.this, ShowUser.class);
                                intent.putExtra("n", name);
//                                intent.putExtra("n", bio);
                                intent.putExtra("u", url);
                                intent.putExtra("uid", uid);
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.profile, parent, false);

                        return new ProfileViewholder(view);
                    }
                };


        firebaseRecyclerAdapter1.startListening();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(RequestShow.this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView_profile.setLayoutManager(gridLayoutManager);
        recyclerView_profile.setAdapter(firebaseRecyclerAdapter1);


        FirebaseRecyclerOptions<RequestMember> options =
                new FirebaseRecyclerOptions.Builder<RequestMember>()
                        .setQuery(databaseReference, RequestMember.class)
                        .build();

        FirebaseRecyclerAdapter<RequestMember, RequestViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<RequestMember, RequestViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RequestViewholder holder, int position, @NonNull RequestMember model) {


                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserId = user.getUid();
                        final String postkey = getRef(position).getKey();

                        holder.setRequest(RequestShow.this, model.getName(), model.getUrl()
                                , model.getBio(), model.getFollowers(), model.getUserid());

                        String uid = getItem(position).getUserid();
                        String name = getItem(position).getName();
                        String bio = getItem(position).getBio();
                        String privacy = getItem(position).getPrivacy();
                        String url = getItem(position).getUrl();


                        holder.button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name = getItem(position).getName();
                                String bio = getItem(position).getBio();
                                decline(name);
                                decline(bio);
                            }
                        });
                        holder.button1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String uid = getItem(position).getUserid();
                                databaseReference1 = database.getReference("followers").child(uid);
                                requestMember.setName(name);
                                requestMember.setBio(bio);
                                requestMember.setUserid(uid);
                                requestMember.setUrl(url);
                                String id = databaseReference1.push().getKey();
                                databaseReference1.child(uid).setValue(requestMember);

                                databaseReference.child(currentUserId).child(uid).removeValue();

                                Toast.makeText(RequestShow.this, "Принято", Toast.LENGTH_SHORT).show();
                                decline(name);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public RequestViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.request_item, parent, false);

                        return new RequestViewholder(view);
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void decline(String name) {

        Query query = databaseReference.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    dataSnapshot1.getRef().removeValue();
                }
                //   Toast.makeText(getActivity(), "Removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ///
            }
        });
    }


}
