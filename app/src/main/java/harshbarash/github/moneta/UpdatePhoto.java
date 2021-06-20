package harshbarash.github.moneta;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class UpdatePhoto extends AppCompatActivity {

    ImageView imageView;
    UploadTask uploadTask;
    ProgressBar progressBar;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    String currentUserId;
    Button button;
    ImageButton back;
    Uri imageUri;
    All_UserMember member;
    private  final static int  PICK_IMAGE = 1;

    Uri imageuri,url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_photo);

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }

        });

        member = new All_UserMember();
        imageView = findViewById(R.id.iv_updatephoto);
        button = findViewById(R.id.btn_updatephoto);
        progressBar = findViewById(R.id.pv_updatephoto);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();


        documentReference = db.collection("user").document(currentUserId);
        storageReference = FirebaseStorage.getInstance().getReference("Profile images");
        databaseReference = database.getReference("All Users");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                chooseimage();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateImage();
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
    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }



    private void updateImage() {

        if ( imageUri != null ){

        final StorageReference reference = storageReference.child(System.currentTimeMillis()+ "."+getFileExt(imageUri));
        uploadTask = reference.putFile(imageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw  task.getException();
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();

                    Map<String,String > profile = new HashMap<>();
                    profile.put("url",downloadUri.toString());
                    profile.put("uid",currentUserId);


                    member.setUid(currentUserId);
                    member.setUrl(downloadUri.toString());

                    databaseReference.child(currentUserId).setValue(member);

                    documentReference.set(profile)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

//                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(UpdatePhoto.this, "Фото добавленно усаешно", Toast.LENGTH_SHORT).show();

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(UpdatePhoto.this, AccountFragment.class);
                                            startActivity(intent);
                                        }
                                    },100);
                                }
                            });
                }

            }
        });

        }else {
            Toast.makeText(this, "Пожалуйста добавьте фото", Toast.LENGTH_SHORT).show();
        }
    }
}

