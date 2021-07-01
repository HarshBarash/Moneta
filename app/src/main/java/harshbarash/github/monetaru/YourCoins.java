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

public class YourCoins extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AllCoins, UserCoins;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_coins);


        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }

        });

        recyclerView = findViewById(R.id.rv_yourque);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();


        AllCoins = database.getReference("AllCoins");
        UserCoins = database.getReference("UserCoins").child(currentUserid);

        FirebaseRecyclerOptions<CoinMember> options =
                new FirebaseRecyclerOptions.Builder<CoinMember>()
                        .setQuery(UserCoins, CoinMember.class)
                        .build();

        FirebaseRecyclerAdapter<CoinMember, Viewholder_Coin> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<CoinMember, Viewholder_Coin>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Viewholder_Coin holder, int position, @NonNull final CoinMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String currentUserid = user.getUid();

                        final String postkey = getRef(position).getKey();

                        holder.setitemdelete(getApplication(), model.getNominal(), model.getYear(), model.getStamp(), model.getCondition(), model.getType(),
                                model.getMagnet(), model.getMaterial(), model.getPrice(), model.getDescription(), model.getTime());


                        final String time = getItem(position).getTime();
                        holder.deletebtn.setOnClickListener(view -> delete(time));

                    }

                    @NonNull
                    @Override
                    public Viewholder_Coin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.yourcoin_item, parent, false);

                        return new Viewholder_Coin(view);


                    }
                };
        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    void delete(String time) {

        Query query = UserCoins.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    dataSnapshot1.getRef().removeValue();

                    Toast.makeText(YourCoins.this, "Удаленно", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query1 = AllCoins.orderByChild("time").equalTo(time);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    dataSnapshot1.getRef().removeValue();

                    Toast.makeText(YourCoins.this, "Удаленно", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}