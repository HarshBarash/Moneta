package harshbarash.github.moneta;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import harshbarash.github.monetaandroid.R;

public class CreateProfile extends AppCompatActivity {

    EditText etname,etBio;
    Button button;
    ImageView imageView;
    ProgressBar progressBar;
    Uri imageUri;
    UploadTask uploadTask;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    private static final int PICK_IMAGE =1;
    All_UserMember member;
    String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        member = new All_UserMember();
        imageView = findViewById(R.id.iv_cp);
        etBio = findViewById(R.id.et_bio_cp);
        etname = findViewById(R.id.et_name_cp);
        button = findViewById(R.id.btn_cp);
//        progressBar = findViewById(R.id.progressbar_cp);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();


        documentReference = db.collection("user").document(currentUserId);
        storageReference = FirebaseStorage.getInstance().getReference("Profile images");
         databaseReference = database.getReference("All Users");


         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 uploadData();
             }
         });

         imageView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent();
                 intent.setType("image/*");
                 intent.setAction(Intent.ACTION_GET_CONTENT);
                 startActivityForResult(intent,PICK_IMAGE);
             }
         });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            if (requestCode == PICK_IMAGE || resultCode == RESULT_OK ||
                    data != null || data.getData() != null) {
                imageUri = data.getData();


                Picasso.get().load(imageUri).into(imageView);
            }
    }

    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));

        if (uri != null) {
            return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
        } else {
            return " "; //надо uri как-то вернуть или добавлять .query() gj
        }
        }



    private void uploadData() {

        final String name = etname.getText().toString();
        final String bio = etBio.getText().toString();


        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(bio)) {

//            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri));
            if (imageUri != null) {
                uploadTask = reference.putFile(imageUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        return reference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            Map<String, String> profile = new HashMap<>();
                            profile.put("name", name);
                            profile.put("url", downloadUri.toString());
                            profile.put("bio", bio);
                            profile.put("uid", currentUserId);
                            profile.put("privacy", "Открытый аккант");

                            member.setName(name.toUpperCase());
                            member.setBio(bio);
                            member.setUid(currentUserId);
                            member.setUrl(downloadUri.toString());

                            databaseReference.child(currentUserId).setValue(member);

                            documentReference.set(profile)
                                    .addOnSuccessListener(aVoid -> {

//                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(CreateProfile.this, "Профиль создан", Toast.LENGTH_SHORT).show();

                                        Handler handler = new Handler();
                                        handler.postDelayed(() -> getSupportFragmentManager().beginTransaction().
                                                add(R.layout.fragment_account, new AccountFragment()).commit(), 0);
                                    });
                        }

                    }
                });
            } else {
                Toast.makeText(this, "Добавьте свое изображение в целях безопастности", Toast.LENGTH_SHORT).show();
            }
            } else {
                Toast.makeText(this, "Пожалуйста заполните все данные", Toast.LENGTH_SHORT).show();
            }
        }
    }
