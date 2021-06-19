package harshbarash.github.moneta;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import harshbarash.github.monetaandroid.R;

public class ProfileViewholder extends RecyclerView.ViewHolder {

    TextView textViewName,textViewBio,viewUserprofile,sendmessagebtn;
    TextView namell,vp_ll,namefollower,vpfollower,bioFollower;

    ImageView imageView,iv_ll,iv_follower;
    CardView cardView;

    public ProfileViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setProfile(FragmentActivity fragmentActivity, String name, String uid, String bio,
                           String url){

        cardView = itemView.findViewById(R.id.cardview_profile);
        textViewName = itemView.findViewById(R.id.tv_name_profile);
        textViewBio = itemView.findViewById(R.id.tv_bio_profile);
        viewUserprofile = itemView.findViewById(R.id.viewUser_profile);
        imageView = itemView.findViewById(R.id.profile_imageview);



        Picasso.get().load(url).into(imageView);
        textViewName.setText(name);
        textViewBio.setText(bio);

    }

    public void setProfileInchat(Application fragmentActivity, String name, String uid, String bio,
                                 String url){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();

        ImageView imageView = itemView.findViewById(R.id.iv_ch_item);
        TextView nametv = itemView.findViewById(R.id.name_ch_item_tv);
        TextView biotv = itemView.findViewById(R.id.ch_itemprof_tv);
        sendmessagebtn = itemView.findViewById(R.id.send_messagech_item_btn);

        if (userid.equals(uid)){
            Picasso.get().load(url).into(imageView);
            nametv.setText(name);
            biotv.setText(bio);
            sendmessagebtn.setVisibility(View.INVISIBLE);
        }else {
            Picasso.get().load(url).into(imageView);
            nametv.setText(name);
            biotv.setText(bio);
        }


    }

//    public void setLikeduser(Application fragmentActivity, String name, String uid, String prof,
//                                 String url){
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String userid = user.getUid();
//
//
//         vp_ll = itemView.findViewById(R.id.vp_ll);
//         namell = itemView.findViewById(R.id.name_ll);
//        iv_ll = itemView.findViewById(R.id.iv_ll);
//
//        Picasso.get().load(url).into(iv_ll);
//        namell.setText(name);
//
//
//    }

    public void setFollower( Application application, String name, String url,
             String bio, String privacy,  String followers ){

        iv_follower = itemView.findViewById(R.id.iv_follower);
        bioFollower = itemView.findViewById(R.id.bio_follower);
        namefollower = itemView.findViewById(R.id.name_follower);
        vpfollower = itemView.findViewById(R.id.vp_follower);

        Picasso.get().load(url).into(iv_follower);
        namefollower.setText(name);
        bioFollower.setText(bio);

    }



}
