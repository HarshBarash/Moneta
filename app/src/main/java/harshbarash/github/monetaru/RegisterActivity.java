package harshbarash.github.monetaru;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {

    EditText emailEt, passEt, confirm_pass;
    Button register_btn, login_btn;
    CheckBox checkBox;
    ProgressBar progressBar;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEt = findViewById(R.id.register_email_et);
        passEt = findViewById(R.id.register_password_et);
        confirm_pass = findViewById(R.id.register_confirmpassword_et);
        register_btn = findViewById(R.id.button_register);
        login_btn = findViewById(R.id.signup_to_login);
        checkBox = findViewById(R.id.register_checkbox);
        progressBar = findViewById(R.id.progressbar_register);
        mAuth = FirebaseAuth.getInstance();

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    passEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirm_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {

                    passEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirm_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailEt.getText().toString();
                String pass = passEt.getText().toString();
                String confirm_password = confirm_pass.getText().toString();

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass) || !TextUtils.isEmpty(confirm_password)) {
                    if (!TextUtils.isEmpty(pass) && pass.length() > 6) {


                        if (pass.equals(confirm_password)) {
                            progressBar.setVisibility(View.VISIBLE);

                            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {

                                if (task.isSuccessful()) {
                                    sendtoMain();
                                    progressBar.setVisibility(View.INVISIBLE);
                                } else {
                                    String error = Objects.requireNonNull(task.getException()).getMessage();
                                    Toast.makeText(RegisterActivity.this, "Проверьте, все ли верно заполненно", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Минимум 6 символов", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                }
            }
        });


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

    }

    private void sendtoMain() {
        Intent intent = new Intent(RegisterActivity.this, Splashscreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            sendtoMain();
        }

    }
}



