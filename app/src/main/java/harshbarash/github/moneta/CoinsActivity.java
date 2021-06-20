package harshbarash.github.moneta;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import harshbarash.github.monetaandroid.R;

public class CoinsActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton fb;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference,fvrtref,fvrt_listRef;
    RecyclerView recyclerView;
    Boolean fvrtChecker = false;
    ImageView  coinView;
    ImageButton back, icCat;
    CircleImageView imageView;
    LinearLayoutManager linearLayoutManager;

    CoinMember member;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //запрет скриншотов
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.fragment_search);

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }

        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();

        recyclerView = findViewById(R.id.rv_f2);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        databaseReference = database.getReference("AllCoins");
        member = new CoinMember();
        fvrtref = database.getReference("favourites");
        fvrt_listRef = database.getReference("favoriteList").child(currentUserid);


        imageView = findViewById(R.id.iv_f2);
        coinView = findViewById(R.id.iv_image);

        fb = findViewById(R.id.floatingActionButton);
        reference = db.collection("user").document(currentUserid);
        //todo сортировка
//        icCat = findViewById(R.id.iv_cat);


        fb.setOnClickListener(this);
        imageView.setOnClickListener(this);
        //TODO сортировка
//        icCat.setOnClickListener(this);


        FirebaseRecyclerOptions<CoinMember> options =
                new FirebaseRecyclerOptions.Builder<CoinMember>()
                        .setQuery(databaseReference, CoinMember.class)
                        .build();

        FirebaseRecyclerAdapter<CoinMember, Viewholder_Coin> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<CoinMember, Viewholder_Coin>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Viewholder_Coin holder, int position, @NonNull final CoinMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String currentUserid = user.getUid();

                        final  String postkey = getRef(position).getKey();

                        holder.setitem(CoinsActivity.this , model.getName(), model.getBio(), model.getUrl(), model.getUserid(), model.getNominal(), model.getYear(), model.getStamp(), model.getCondition(),model.getType(),
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
                        holder.fvrt_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                fvrtChecker = true;

                                fvrtref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (fvrtChecker.equals(true)){
                                            if (snapshot.child(postkey).hasChild(currentUserid)){
                                                fvrtref.child(postkey).child(currentUserid).removeValue();
                                                delete(time);
                                                Toast.makeText(CoinsActivity.this, "Удалено из избранного", Toast.LENGTH_SHORT).show();
                                                fvrtChecker = false;
                                            }else {


                                                fvrtref.child(postkey).child(currentUserid).setValue(true);
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
                                                fvrtChecker = false;

                                                Toast.makeText(CoinsActivity.this, "Добавлено в избранное", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public Viewholder_Coin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.coin_item,parent,false);

                        return new Viewholder_Coin(view);



                    }
                };
        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    void delete(String time){

        Query query = fvrt_listRef.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();

                    Toast.makeText(CoinsActivity.this, "Убранно с избранного", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_f2:
                BottomSheetCoin bottomSheetCoin = new BottomSheetCoin();
                bottomSheetCoin.show(getSupportFragmentManager(),"bottom");
                break;
            case R.id.floatingActionButton:
                Intent intent = new Intent(CoinsActivity.this, DefineActivity.class);
                startActivity(intent);
                break;

                //todo сортировка
//            case R.id.iv_cat:
//                showCategoryShet();
//                break;

        }
    }

    //todo сортировка
//    private void showCategoryShet() {
//        final Dialog dialog = new Dialog(CoinsActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.category_layout);
//
//        TextView tvOld = dialog.findViewById(R.id.tvOld);
//        TextView tvNominal = dialog.findViewById(R.id.tvNominal);
//        TextView tvYear = dialog.findViewById(R.id.tvYear);
//        TextView tvStamp = dialog.findViewById(R.id.tvStamp);
//        TextView tvCondition = dialog.findViewById(R.id.tvCondition);
//        TextView tvType = dialog.findViewById(R.id.tvType);
//        TextView tvMaterial = dialog.findViewById(R.id.tvMaterial);
//
//        tvOld.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String  = "номинал";
//                sortCoin(category);
//            }
//        });
//    }

    @Override
    public void onStart() {
        super.onStart();
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            String url = task.getResult().getString("url");

                            Picasso.get().load(url).into(imageView);
                        }else {
//                            Toast.makeText(CoinsActivity.this, "Добавьте из", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}