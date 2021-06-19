package harshbarash.github.moneta;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import harshbarash.github.monetaandroid.R;

public class AccountFragment extends Fragment implements View.OnClickListener{

    CircleImageView imageView;
    TextView nameEt,bioEt, lotTv, followertv, requestTv;
    ImageButton imageButtonEdit,imageButtonMenu;
    DocumentReference reference;
    FirebaseFirestore firestore;
    Uri imageUri;
    String url,userid;
    private static int PICK_IMAGE=1;
    int followerno, lot;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db1,db3;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account,container,false);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();

        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("user").document(userid);

        db1 = database.getReference("followers").child(userid);
        db3 = database.getReference("UserCoins").child(userid);

        imageView = getActivity().findViewById(R.id.iv_f1);
        nameEt = getActivity().findViewById(R.id.tv_name_f1);
        bioEt = getActivity().findViewById(R.id.tv_bio_f1);
        lotTv = getActivity().findViewById(R.id.tv_post_f1);
        followertv = getActivity().findViewById(R.id.tv_followers_f1);
        requestTv = getActivity().findViewById(R.id.tv_request);

        imageButtonEdit = getActivity().findViewById(R.id.ib_edit_f1);
        imageButtonMenu = getActivity().findViewById(R.id.ib_menu_f1);
        lotTv.setOnClickListener(this);


        imageButtonMenu.setOnClickListener(this);
        imageButtonEdit.setOnClickListener(this);
        imageView.setOnClickListener(this);
        requestTv.setOnClickListener(this);
        followertv.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_edit_f1:
                Intent intent = new Intent(getActivity(),UpdateProfile.class);
                startActivity(intent);
                break;

            case R.id.ib_menu_f1:
                BottomSheetMenu bottomSheetMenu = new BottomSheetMenu();
                bottomSheetMenu.show(getFragmentManager(),"bottomsheet");
                break;

            case R.id.iv_f1:
                Intent intent1 = new Intent(getActivity(),ImageActivity.class);
                startActivity(intent1);
                break;

            case R.id.tv_post_f1:
                Intent intent5 = new Intent(getActivity(), YourCoins.class);
                startActivity(intent5);
                break;

            case R.id.tv_followers_f1:
                Intent follower = new Intent(getActivity(), FollowerActivity.class);
                follower.putExtra("u", userid);
                startActivity(follower);
                break;

            case R.id.tv_request:
                Intent in = new Intent(getActivity(), RequestShow.class);
                startActivity(in);
                break;
        }
    }


    @Override
    public void onStart() {
        super.onStart();


        db1.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followerno = (int) snapshot.getChildrenCount();
//                followertv.setText(Integer.toString(followerno)+" Подписчиков");

                int count = followerno;

                Resources res = getContext().getResources();
                String followersFound = res.getQuantityString(R.plurals.numberOfFollower, count, count);
                followertv.setText(followerno + " " + followersFound); //конкатанирую речь в 3 ночи;)

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        db3.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lot = (int) snapshot.getChildrenCount();
                lotTv.setText( "Мои лоты");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.get()
                .addOnCompleteListener(task -> {

                    if (Objects.requireNonNull(task.getResult()).exists()) {

                        String nameResult = task.getResult().getString("name");
                        String bioResult = task.getResult().getString("bio");
                        url = task.getResult().getString("url");

                        Picasso.get().load(url).into(imageView);
                        nameEt.setText(nameResult);
                        bioEt.setText(bioResult);



                    } else {
                        Intent intent = new Intent(getActivity(), CreateProfile.class);
                        startActivity(intent);
                    }
                });


    }


}
