package harshbarash.github.moneta;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import harshbarash.github.monetaandroid.R;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth auth;


    private BottomNavigationView.OnNavigationItemSelectedListener onNav = item -> {

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        Fragment selected = null;
        switch (item.getItemId()) {
            case R.id.profile_bottom:
                selected = new AccountFragment();
                break;

            case R.id.queue_bottom:
                selected = new ChatFragment();
                break;

            case R.id.home_bottom:
                selected = new HomeFragment();
                break;
        }

        if (!currentFragment.getClass().getSimpleName().equals(selected.getClass().getSimpleName())) {
            setFragment(selected);
        }

        return false;

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNav);

        if (savedInstanceState == null) {
            setFragment(new HomeFragment());
        }
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
