package harshbarash.github.moneta;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import harshbarash.github.monetaandroid.R;


public class Splashscreen extends AppCompatActivity {

    ImageView imageView;
    TextView nameTv;
    long animTime = 50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);

        imageView = findViewById(R.id.iv_logo_splash);
        nameTv = findViewById(R.id.tv_splash_name);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(imageView,"y",250f);
        ObjectAnimator animatorname = ObjectAnimator.ofFloat(nameTv,"x",225f);
        animatorY.setDuration(animTime);
        animatorname.setDuration(animTime);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorY,animatorname);
        animatorSet.start();

    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null){
                    Intent intent = new Intent(Splashscreen.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(Splashscreen.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        },1000);

    }
}
