package harshbarash.github.moneta;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import harshbarash.github.monetaandroid.R;

public class Viewholder_Coin extends RecyclerView.ViewHolder {

    ImageView imageView, coinView, deletebtn, sendmessage;
    TextView time_result,bio_result, nominal_result, year_result, stamp_result, condition_result, material_result, magnet_result, type_result, price_result;
    ImageButton fvrt_btn;
    DatabaseReference favouriteref;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    public Viewholder_Coin(@NonNull View itemView) {
        super(itemView);
    }

    @SuppressLint("SetTextI18n")
    public void setitem(CoinsActivity coinsActivity, String name, String bio, String url, String userid, String nominal, String year, String stamp,
                        String condition, String type, String magnet, String material, String price, String description, String time) {

        coinView = itemView.findViewById(R.id.iv_image);
        bio_result = itemView.findViewById(R.id.tv_city);
        nominal_result = itemView.findViewById(R.id.tv_title);
        year_result = itemView.findViewById(R.id.tv_year);
        stamp_result = itemView.findViewById(R.id.tv_stamp);
        condition_result = itemView.findViewById(R.id.tv_condition);
        magnet_result = itemView.findViewById(R.id.tv_magnet);
        type_result = itemView.findViewById(R.id.tv_type);
        material_result = itemView.findViewById(R.id.tv_material);
        price_result = itemView.findViewById(R.id.tv_price);

        sendmessage = itemView.findViewById(R.id.hand);


        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), MessageActivity.class);
                intent.putExtra("n",name);
                intent.putExtra("c",bio);
                intent.putExtra("u",url);
                intent.putExtra("uid",userid);
                view.getContext().startActivity(intent);
            }
        });

        bio_result.setText(bio);
        nominal_result.setText(nominal);

        switch (nominal) {
            case "1 Рубль": {
                coinView.setImageResource(R.drawable.rubel1);
                break;
            }
            case "2 Рубля": {
                coinView.setImageResource(R.drawable.rubel2);
                break;
            }
            case "5 Рублей": {
                coinView.setImageResource(R.drawable.rubel5);
                break;
            }
            default: {
                coinView.setImageResource(R.drawable.rubel10);
                break;
            }
        }


       year_result.setText(year);
       stamp_result.setText(stamp);
       type_result.setText(type);
       magnet_result.setText(magnet);
       material_result.setText(material);
       price_result.setText("Цена: " +price + "₽");

   }
    public void favouriteChecker(final String postkey) {
        fvrt_btn = itemView.findViewById(R.id.fvrt_f2_item);


        favouriteref = database.getReference("favourites");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        favouriteref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(postkey).hasChild(uid)){
                    fvrt_btn.setImageResource(R.drawable.ic_baseline_turned_in_24);
                }else {
                    fvrt_btn.setImageResource(R.drawable.ic_baseline_turned_in_not_24);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @SuppressLint("SetTextI18n")
    public void setitemRelated(Related activity, String name, String bio, String url, String userid, String nominal, String year, String stamp,
                               String condition, String type, String magnet, String material, String price, String description, String time){

        coinView = itemView.findViewById(R.id.iv_image);

        nominal_result = itemView.findViewById(R.id.tv_title);
        year_result = itemView.findViewById(R.id.tv_year);
        stamp_result = itemView.findViewById(R.id.tv_stamp);
        condition_result = itemView.findViewById(R.id.tv_condition);
        magnet_result = itemView.findViewById(R.id.tv_magnet);
        type_result = itemView.findViewById(R.id.tv_type);
        material_result = itemView.findViewById(R.id.tv_material);
        price_result = itemView.findViewById(R.id.tv_price);
        time_result = itemView.findViewById(R.id.time_que_item_tv);
        bio_result = itemView.findViewById(R.id.tv_city);

        nominal_result.setText(nominal);
        bio_result.setText(bio);


        switch (nominal) {
            case "1 Рубль": {
                coinView.setImageResource(R.drawable.rubel1);
                break;
            }
            case "2 Рубля": {
                coinView.setImageResource(R.drawable.rubel2);
                break;
            }
            case "5 Рублей": {
                coinView.setImageResource(R.drawable.rubel5);
                break;
            }
            default: {
                coinView.setImageResource(R.drawable.rubel10);
                break;
            }
        }

        year_result.setText(year);
        stamp_result.setText(stamp);
        condition_result.setText(condition);
        type_result.setText(type);
        magnet_result.setText(magnet);
        material_result.setText(material);
        price_result.setText("Цена: " + price + "₽");
        bio_result.setText(bio);


//        time_result.setText(time);

        sendmessage = itemView.findViewById(R.id.hand);


        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), MessageActivity.class);
                intent.putExtra("n",name);
                intent.putExtra("u",url);
                intent.putExtra("uid",userid);
                view.getContext().startActivity(intent);
            }
        });



    }


    @SuppressLint("SetTextI18n")
    public void setitemdelete(Application activity, String nominal, String year, String stamp, String condition,
                              String type, String magnet, String material, String price, String description, String time){

        coinView = itemView.findViewById(R.id.iv_image);

        nominal_result = itemView.findViewById(R.id.tv_title);
        year_result = itemView.findViewById(R.id.tv_year);
        stamp_result = itemView.findViewById(R.id.tv_stamp);
        condition_result = itemView.findViewById(R.id.tv_condition);
        magnet_result = itemView.findViewById(R.id.tv_magnet);
        type_result = itemView.findViewById(R.id.tv_type);
        material_result = itemView.findViewById(R.id.tv_material);
        price_result = itemView.findViewById(R.id.tv_price);
        time_result = itemView.findViewById(R.id.time_que_item_tv);
        bio_result = itemView.findViewById(R.id.tv_city);
        deletebtn= itemView.findViewById(R.id.delete_item_que_tv);



        nominal_result.setText(nominal);

        switch (nominal) {
            case "1 Рубль": {
                coinView.setImageResource(R.drawable.rubel1);
                break;
            }
            case "2 Рубля": {
                coinView.setImageResource(R.drawable.rubel2);
                break;
            }
            case "5 Рублей": {
                coinView.setImageResource(R.drawable.rubel5);
                break;
            }
            default: {
                coinView.setImageResource(R.drawable.rubel10);
                break;
            }
        }

        year_result.setText(year);
        stamp_result.setText(stamp);
        type_result.setText(type);
        condition_result.setText(condition);
        magnet_result.setText(magnet);
        material_result.setText(material);
        price_result.setText("Цена: " + price + "₽");


        time_result.setText(time);


    }

}














