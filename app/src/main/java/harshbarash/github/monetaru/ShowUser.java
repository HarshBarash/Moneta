package harshbarash.github.monetaru;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class ShowUser extends AppCompatActivity {


    TextView nametv, biotv, websitetv, requesttv;
    ImageView imageView;
    FirebaseDatabase database;
    DatabaseReference databaseReference, databaseReference1, databaseReference2, databaseReference3, postnoref, db1, db2;
    TextView button, followers_tv, posts_tv;
    CardView followers_cv, posts_cd;
    String url, name, privacy, p, website, bio, userid;
    RequestMember requestMember;
    String name_result;
    String uidreq, namereq, urlreq, bioreq;
    ImageButton back;

    Button sendmessage;
    int postNo;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference, documentReference1;

    int followercount, postiv, postvv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {

        }
        setContentView(R.layout.activity_show_user);

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        database = FirebaseDatabase.getInstance();

        sendmessage = findViewById(R.id.btn_sendmessage_showuser);
        requestMember = new RequestMember();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();


        nametv = findViewById(R.id.name_tv_showprofile);
        biotv = findViewById(R.id.bio_tv_showprofile);
        imageView = findViewById(R.id.imageView_showprofile);
        button = findViewById(R.id.btn_requestshowprofile);
        requesttv = findViewById(R.id.tv_requestshowprofile);

        followers_tv = findViewById(R.id.followerNo_tv);
        posts_tv = findViewById(R.id.postsNo_tv);
        followers_cv = findViewById(R.id.followers_cardview);
        posts_cd = findViewById(R.id.followers_cardview);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("u");
            name = extras.getString("n");
            userid = extras.getString("uid");
        } else {
            Toast.makeText(this, "Закрытый аккаунт", Toast.LENGTH_SHORT).show();
        }

        databaseReference = database.getReference("Requests").child(userid);
        databaseReference1 = database.getReference("followers").child(userid);
        documentReference = db.collection("user").document(userid);
        postnoref = database.getReference("UserCoins").child(userid);
        databaseReference3 = database.getReference("UserCoins");
        databaseReference2 = database.getReference("followers");
        documentReference1 = db.collection("user").document(currentUserId);

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = button.getText().toString();
                if (p.equals("Закрытый аккаунт") && (status.equals("Подписаться") || status.equals("Запрошено"))) {
                    Toast.makeText(ShowUser.this, "Вас нет в подписчиках", Toast.LENGTH_LONG).show();

                } else {
                    Intent intent = new Intent(ShowUser.this, MessageActivity.class);
                    intent.putExtra("n", name);
                    intent.putExtra("u", url);
                    intent.putExtra("uid", userid);
                    startActivity(intent);

                }

            }
        });

        postnoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postNo = (int) snapshot.getChildrenCount();
                //   posts_tv.setText(Integer.toString(postNo));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = button.getText().toString();
                if (status.equals("Подписаться")) {
                    follow();
                } else if (status.equals("Запрошено")) {
                    delRequest();
                } else if (status.equals("Вы подписаны")) {
                    unFollow();
                }

            }
        });

        followers_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowUser.this, FollowerActivity.class);
                intent.putExtra("u", userid);
                startActivity(intent);
            }
        });
    }

    private void delRequest() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();
        databaseReference.child(currentUserId).removeValue();
        button.setText("Подписаться");
    }

    @Override
    protected void onStart() {
        super.onStart();


        postnoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postvv = (int) snapshot.getChildrenCount();
                posts_tv.setText("" + postvv);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()) {
                            String name_result = task.getResult().getString("name");
                            String bio_result = task.getResult().getString("bio");
                            String Url = task.getResult().getString("url");
                            p = task.getResult().getString("privacy");


                            String u = button.getText().toString();
                            if (u.equals("Вы подписаны")) {
                                button.setText("Вы подписаны");
                                nametv.setText(name_result);
                                biotv.setText(bio_result);
                                Picasso.get().load(Url).into(imageView);
                                requesttv.setVisibility(View.GONE);
                            } else {
                                button.setText("Подписаться");
                                nametv.setText(name_result);
                                biotv.setText(bio_result);
                                Picasso.get().load(Url).into(imageView);
                                requesttv.setVisibility(View.VISIBLE);
                            }


                        } else {
                            Toast.makeText(ShowUser.this, "Нет такого профиля", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        documentReference1.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()) {
                            namereq = task.getResult().getString("name");
                            bioreq = task.getResult().getString("bio");
                            urlreq = task.getResult().getString("url");


                        } else {
                            //  Toast.makeText(ShowUser.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        // refernce for following
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    followercount = (int) snapshot.getChildrenCount();
                    followers_tv.setText(Integer.toString(followercount));


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(currentUserId)) {
                    button.setText("Запрошено");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userid).hasChild(currentUserId)) {
                    button.setText("Вы подписаны");
                } else {
                    button.setText("Подписаться");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    void follow() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();


        if (p.equals("Открытый аккаунт")) {
            button.setText("Вы подписаны");
            requestMember.setUserid(currentUserId);
            requestMember.setBio(bioreq);
            requestMember.setUrl(urlreq);
            requestMember.setName(namereq);

            databaseReference1.child(currentUserId).setValue(requestMember);
        } else {

            button.setText("Запрошено");
            requestMember.setUserid(currentUserId);
            requestMember.setBio(bioreq);
            requestMember.setUrl(urlreq);
            requestMember.setName(namereq);
            databaseReference.child(currentUserId).setValue(requestMember);
            requesttv.setText("Подождите, пока ваш запрос не будет принят");

        }
    }

    private void unFollow() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(ShowUser.this);
        builder.setTitle("Отписаться")
                .setMessage("Вы уверены, что хотите отписаться?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseReference1.child(currentUserId).removeValue();
                        followers_tv.setText("" + followercount);
                        Toast.makeText(ShowUser.this, "Больше не подписаны", Toast.LENGTH_SHORT).show();
                        button.setText("Подписаться");

                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create();
        builder.show();
    }


}

