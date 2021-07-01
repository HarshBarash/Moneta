package harshbarash.github.monetaru;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class Related extends AppCompatActivity {


    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference, fvrtref, fvrt_listRef;
    Boolean fvrtChecker = true;
    ImageButton back;


    CoinMember member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related);

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }

        });


        recyclerView = findViewById(R.id.rv_related);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();

        fvrt_listRef = database.getReference("favouriteList");
        fvrtref = database.getReference("favourites");
        reference = database.getReference("favoriteList").child(currentUserid);
        member = new CoinMember();


        FirebaseRecyclerOptions<CoinMember> options =
                new FirebaseRecyclerOptions.Builder<CoinMember>()
                        .setQuery(reference, CoinMember.class)
                        .build();

        FirebaseRecyclerAdapter<CoinMember, Viewholder_Coin> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<CoinMember, Viewholder_Coin>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Viewholder_Coin holder, int position, @NonNull final CoinMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String currentUserid = user.getUid();

                        final String postkey = getRef(position).getKey();

                        holder.setitemRelated(Related.this, model.getName(), model.getBio(), model.getUrl(), model.getUserid(), model.getNominal(), model.getYear(), model.getStamp(), model.getCondition(), model.getType(),
                                model.getMagnet(), model.getMaterial(), model.getPrice(), model.getDescription(), model.getTime());

                        final String nominal = getItem(position).getNominal();
                        final String condition = getItem(position).getCondition();
                        final String magnet = getItem(position).getMagnet();
                        final String stamp = getItem(position).getStamp();
                        final String type = getItem(position).getType();
                        final String material = getItem(position).getMaterial();
                        final String year = getItem(position).getYear();
                        final String price = getItem(position).getPrice();
                        final String description = getItem(position).getDescription();


                        final String name = getItem(position).getName();
                        final String bio = getItem(position).getBio();
                        final String url = getItem(position).getUrl();
                        final String time = getItem(position).getTime();
                        final String privacy = getItem(position).getPrivacy();
                        final String userid = getItem(position).getUserid();

                        holder.favouriteChecker(postkey);
                        holder.fvrt_btn.setOnClickListener(view -> {

                            fvrtChecker = false;

                            fvrtref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (fvrtChecker.equals(false)) {
                                        if (snapshot.child(postkey).hasChild(currentUserid)) {
                                            fvrtref.child(postkey).child(currentUserid).removeValue();
                                            delete(time);
                                            Toast.makeText(Related.this, "Удалено из избранного", Toast.LENGTH_SHORT).show();
                                            fvrtChecker = true;
                                        } else {


                                            fvrtref.child(postkey).child(currentUserid).setValue(false);
                                            member.setName(name);
                                            member.setBio(bio);
                                            member.setTime(time);
                                            member.setPrivacy(privacy);
                                            member.setUserid(userid);
                                            member.setUrl(url);
                                            member.setNominal(nominal);

                                            member.setCondition(condition);
                                            member.setMagnet(magnet);
                                            member.setStamp(stamp);
                                            member.setType(type);
                                            member.setMaterial(material);
                                            member.setYear(year);
                                            member.setPrice(price);
                                            member.setDescription(description);


                                            //  String id = fvrt_listRef.push().getKey();
                                            fvrt_listRef.child(postkey).setValue(member);
                                            fvrtChecker = true;

                                            Toast.makeText(Related.this, "Убранно с избранного", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        });
                    }

                    @NonNull
                    @Override
                    public Viewholder_Coin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.related_item, parent, false);

                        return new Viewholder_Coin(view);

                    }
                };

        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    void delete(String time) {

        Query query = reference.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    dataSnapshot1.getRef().removeValue();

                    Toast.makeText(Related.this, "Убранно с избранного", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}



