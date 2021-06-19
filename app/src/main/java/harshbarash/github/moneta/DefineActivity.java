package harshbarash.github.moneta;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.view.MenuItem;

import androidx.core.app.NavUtils;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;

import java.util.Calendar;

import harshbarash.github.moneta.CoinMember;
import harshbarash.github.moneta.Moneta;
import harshbarash.github.monetaandroid.R;

public class DefineActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {


    // firebase - NOSQL
    EditText fbYearEditText, fbPriceEditText, fbDescriptionEditText;

    //спинеры
    Spinner spinner1, spinner2, spinner3, spinner4, spinner5, spinner6;

    String[] spiner1_nominal = {"1 Рубль", "2 Рубля", "5 Рублей", "10 Рублей"};
    String[] spiner2_condition = {"1 - PR", "2 - G", "3 - VG", "4 - F", "5 - VF", "6 - XF",  "7 - UNC"};
    String[] spiner3_magnet = {"Нет", "Да"};
    String[] spiner4_stamp = {"ШТ.М(А)", "ШТ.СП"};
    String[] spiner5_type = {"В обращении", "Юбилейная", "Инвестиционная"};
    String[] spiner6_material = {"Белый металл", "Желтый металл", "Другой"};


    Button  addfbBtn, removeBtn;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AllCoins,UserCoins;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    CoinMember member;
    String name,url,privacy,uid;
    ImageButton back;

    ImageButton InfoCondition, InfoStamp, InfoMaterial, InfoType;


    //old OpenHelper- SQL

    private EditText mYearEditText, mPriceEditText, mDescriptionEditText;
    private Uri mCurrentCoinUri;

    private ImageButton define_image;
    private Button addBtn;

    public static final int COINLOADER = 0;

    private String mType1 = Moneta.CoinEntry.TYPEOFNOMINAL_1RUBEL;
    private String mType2 = Moneta.CoinEntry.TYPEOFCONDITION_5;
    private String mType3 = Moneta.CoinEntry.TYPEOFMAGNET_NET;
    private String mType4 = Moneta.CoinEntry.TYPEOFSTAMP_M;
    private String mType5 = Moneta.CoinEntry.TYPEOFTYPE_OB;
    private String mType6 = Moneta.CoinEntry.TYPEOFMATERIAL_WHITE;

    private boolean mCoinHasChanged = false;
    Spinner mSpinner1, mSpinner2, mSpinner3, mSpinner4, mSpinner5, mSpinner6;

    @SuppressLint("ClickableViewAccessibility")
    private final View.OnTouchListener mOnTouchListener = (v, event) -> {
        mCoinHasChanged = true;
        return false;
    };

    boolean hashAllRequiredValues = false;


    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); //base activity и наследование
        initView();

        Intent intent = getIntent();

        if(intent != null){
            mCurrentCoinUri = intent.getData();
        }

        define_image.setOnClickListener(v ->
                startActivity(new Intent(DefineActivity.this, ClassifierActivity.class)));

        addBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mYearEditText.getText().toString().trim()) || TextUtils.isEmpty(mPriceEditText.getText().toString().trim()) || TextUtils.isEmpty(mDescriptionEditText.getText().toString().trim()) ){
                Toast.makeText(this, "Введите все данные ", Toast.LENGTH_SHORT).show();
            }
            else {
                saveCoin();
                startActivity(new Intent(DefineActivity.this, CollectionActivity.class));
            }

        });

        removeBtn.setOnClickListener(v -> {
            deleteCoin();
            startActivity(new Intent(DefineActivity.this, CollectionActivity.class));
        });

        InfoCondition.setOnClickListener(v -> {
            InfoConditionFragment infoConditionFragment = new InfoConditionFragment();
            infoConditionFragment.show(getSupportFragmentManager(), "Info");
        });

        InfoMaterial.setOnClickListener(v -> {
            InfoMaterialFragment infoMaterialFragment = new InfoMaterialFragment();
            infoMaterialFragment.show(getSupportFragmentManager(), "Info");
        });

        InfoStamp.setOnClickListener(v -> {
            InfoStampFragment infoStampFragment = new InfoStampFragment();
            infoStampFragment.show(getSupportFragmentManager(), "Info");
        });

        InfoType.setOnClickListener(v -> {
            InfoTypeFragment infoTypeFragment = new InfoTypeFragment();
            infoTypeFragment.show(getSupportFragmentManager(), "Info");
        });

        mSpinner1.setOnTouchListener(mOnTouchListener);
        mSpinner2.setOnTouchListener(mOnTouchListener);
        mSpinner3.setOnTouchListener(mOnTouchListener);
        mSpinner4.setOnTouchListener(mOnTouchListener);
        mSpinner5.setOnTouchListener(mOnTouchListener);
        mSpinner6.setOnTouchListener(mOnTouchListener);
        mYearEditText.setOnTouchListener(mOnTouchListener);
        mPriceEditText.setOnTouchListener(mOnTouchListener);
        mDescriptionEditText.setOnTouchListener(mOnTouchListener);

        setUpSpinner();

        if(mCurrentCoinUri != null){
            getLoaderManager().initLoader(COINLOADER, null, this);
        }

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

        // firebase

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();


        fbYearEditText = findViewById(R.id.yearEt);
        fbPriceEditText = findViewById(R.id.priceEt);
        fbDescriptionEditText = findViewById(R.id.descriptionEt);

        addfbBtn = findViewById(R.id.addfbBtn);



        documentReference = db.collection("user").document(currentUserid);

        AllCoins = database.getReference("AllCoins");
        UserCoins = database.getReference("UserCoins").child(currentUserid);

        member = new CoinMember();


        //spinner

        spinner1 = findViewById(R.id.spinner);
        spinner2 = findViewById(R.id.spinner2);
        spinner3 = findViewById(R.id.spinner3);
        spinner4 = findViewById(R.id.spinner4);
        spinner5 = findViewById(R.id.spinner5);
        spinner6 = findViewById(R.id.spinner6);


        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, spiner1_nominal);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner1.setAdapter(arrayAdapter);
        spinner1.setOnItemSelectedListener(this);

        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, spiner2_condition);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner2.setAdapter(arrayAdapter2);
        spinner2.setOnItemSelectedListener(this);

        ArrayAdapter arrayAdapter3 = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, spiner3_magnet);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner3.setAdapter(arrayAdapter3);
        spinner3.setOnItemSelectedListener(this);

        ArrayAdapter arrayAdapter4 = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, spiner4_stamp);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner4.setAdapter(arrayAdapter4);
        spinner4.setOnItemSelectedListener(this);

        ArrayAdapter arrayAdapter5 = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, spiner5_type);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner5.setAdapter(arrayAdapter5);
        spinner5.setOnItemSelectedListener(this);

        ArrayAdapter arrayAdapter6 = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, spiner6_material);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner6.setAdapter(arrayAdapter6);
        spinner6.setOnItemSelectedListener(this);

        //

        addfbBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                //все значения в fb

                String nominal = spinner1.getSelectedItem().toString();
                String condition = spinner2.getSelectedItem().toString();
                String magnet = spinner3.getSelectedItem().toString();
                String stamp = spinner4.getSelectedItem().toString();
                String type = spinner5.getSelectedItem().toString();
                String material = spinner6.getSelectedItem().toString();
                String year = fbYearEditText.getText().toString();
                String price = fbPriceEditText.getText().toString();
                String description = fbDescriptionEditText.getText().toString();

                Calendar cdate = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                final  String savedate = currentdate.format(cdate.getTime());


                //Если надо до секунды
//                Calendar ctime = Calendar.getInstance();
//                @SuppressLint("SimpleDateFormat") SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
//                final String savetime = currenttime.format(ctime.getTime());


//                String time = savedate +":"+ savetime;

                String time = savedate;



                if (!TextUtils.isEmpty(nominal) && !TextUtils.isEmpty(condition) && !TextUtils.isEmpty(magnet)  && !TextUtils.isEmpty(stamp)  && !TextUtils.isEmpty(type) &&  !TextUtils.isEmpty(material)  && !TextUtils.isEmpty(year)  && !TextUtils.isEmpty(price)  && !TextUtils.isEmpty(description) ){


                    member.setNominal(nominal);
                    member.setCondition(condition);
                    member.setMagnet(magnet);
                    member.setStamp(stamp);
                    member.setType(type);
                    member.setMaterial(material);
                    member.setYear(year);
                    member.setPrice(price);
                    member.setDescription(description);

                    member.setName(name);
                    member.setPrivacy(privacy);
                    member.setUrl(url);
                    member.setUserid(uid);
                    member.setTime(time);

                    String id = UserCoins.push().getKey();
                    UserCoins.child(id).setValue(member);


                    String child = AllCoins.push().getKey();
                    member.setKey(id);
                    AllCoins.child(child).setValue(member);
                    Toast.makeText(DefineActivity.this, "Выставленно", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DefineActivity.this, CoinsActivity.class);
                    startActivity(intent);

                }else {

                    Toast.makeText(DefineActivity.this, "Заполните оставшиеся поля", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    // openHelper
    private void initView() {
        //OpenHelper
        InfoCondition = findViewById(R.id.infocondition);
        InfoStamp = findViewById(R.id.infostamp);
        InfoMaterial = findViewById(R.id.infomaterial);
        InfoType = findViewById(R.id.infotype);

        define_image = findViewById(R.id.define_image);
        addBtn = findViewById(R.id.addBtn);
        removeBtn = findViewById(R.id.removeBtn);

        mSpinner1 = findViewById(R.id.spinner);
        mSpinner2 = findViewById(R.id.spinner2);
        mSpinner3 = findViewById(R.id.spinner3);
        mSpinner4 = findViewById(R.id.spinner4);
        mSpinner5 = findViewById(R.id.spinner5);
        mSpinner6 = findViewById(R.id.spinner6);

        mYearEditText = findViewById(R.id.yearEt);
        mPriceEditText = findViewById(R.id.priceEt);
        mDescriptionEditText = findViewById(R.id.descriptionEt);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Пожалуйста заполните все поля", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();


        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            name = task.getResult().getString("Имя");
                            url = task.getResult().getString("url");
                            privacy = task.getResult().getString("privacy");
                            uid = task.getResult().getString("uid");


                            if (spinner1.equals("10 Рублей")){
                                spinner1.setSelection(3); //10
                            }else if (spinner1.equals("2 Рубля")){
                                spinner1.setSelection(1); //2
                            } else if (spinner1.equals("5 Рублей")){
                                spinner1.setSelection(2); //5
                            } else {
                                spinner1.setSelection(0); //1
                            }


                            if (spinner2.equals("1 - PR")){
                                spinner2.setSelection(0); //1
                            }else if (spinner2.equals("2 - G")){
                                spinner2.setSelection(1); //2
                            } else if (spinner2.equals("3 - VG")){
                                spinner2.setSelection(2); //3
                            } else if (spinner2.equals("4 - F")){
                                spinner2.setSelection(3); //4
                            } else if (spinner2.equals("7 - UNC")){
                                spinner2.setSelection(6); //7
                            } else if (spinner2.equals("6 - XF")){
                                spinner2.setSelection(5); //6
                            } else {
                                spinner2.setSelection(4); //5
                            }

                            if (spinner3.equals("Да")){
                                spinner3.setSelection(1); //Да
                            } else {
                                spinner3.setSelection(0); //Нет
                            }

                            if (spinner4.equals("ШТ.СП")){
                                spinner4.setSelection(1); //ШТ.СП
                            } else {
                                spinner4.setSelection(0); //ШТ.М(А)
                            }

                            if (spinner5.equals("Инвестиционная")){
                                spinner5.setSelection(2); //Инвестиционная
                            } else if (spinner5.equals("Юбилейная")) {
                                spinner5.setSelection(1); //Юбилейная
                            } else {
                                spinner5.setSelection(0); //В обращении
                            }


                            if (spinner6.equals("Другой")){
                                spinner6.setSelection(2); //Белый
                            } else if (spinner6.equals("Желтый Металл")) {
                                spinner6.setSelection(1); //Желтый
                            } else {
                                spinner6.setSelection(0); //Белый
                            }


                        } else {
//                            Toast.makeText(DefineActivity.this, "Что-то пошло не так", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void savefbCoin() {

        final  String nominal = spinner1.getSelectedItem().toString();
        final  String condition = spinner2.getSelectedItem().toString();
        final  String magnet = spinner3.getSelectedItem().toString();
        final  String stamp = spinner4.getSelectedItem().toString();
        final  String type = spinner5.getSelectedItem().toString();
        final  String material = spinner6.getSelectedItem().toString();
        String year = fbYearEditText.getText().toString();
        String price = fbPriceEditText.getText().toString();
        String description = fbDescriptionEditText.getText().toString();



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentid = user.getUid();
        final  DocumentReference sCoin = db.collection("User Coins").document(currentid);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sCoin);


                transaction.update(sCoin, "Номинал", nominal );
                transaction.update(sCoin, "Состояние", condition );
                transaction.update(sCoin, "Магнит", magnet );
                transaction.update(sCoin, "Штамп", stamp );
                transaction.update(sCoin, "Тип", type );
                transaction.update(sCoin, "Материал", material );
                transaction.update(sCoin, "Год", year );
                transaction.update(sCoin, "Цена", price );
                transaction.update(sCoin, "Описание", description );



                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DefineActivity.this, "Успешно добавленно", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(DefineActivity.this, "Что-то пошло не так", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void setUpSpinner() {

        ArrayAdapter spinner1 = ArrayAdapter.createFromResource(this, R.array.arrayspinner1, android.R.layout.simple_spinner_item);
        spinner1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner1.setAdapter(spinner1);

        ArrayAdapter spinner2 = ArrayAdapter.createFromResource(this, R.array.arrayspinner2, android.R.layout.simple_spinner_item);
        spinner2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner2.setAdapter(spinner2);

        ArrayAdapter spinner3 = ArrayAdapter.createFromResource(this, R.array.arrayspinner3, android.R.layout.simple_spinner_item);
        spinner3.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner3.setAdapter(spinner3);

        ArrayAdapter spinner4 = ArrayAdapter.createFromResource(this, R.array.arrayspinner4, android.R.layout.simple_spinner_item);
        spinner4.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner4.setAdapter(spinner4);

        ArrayAdapter spinner5 = ArrayAdapter.createFromResource(this, R.array.arrayspinner5, android.R.layout.simple_spinner_item);
        spinner5.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner5.setAdapter(spinner5);

        ArrayAdapter spinner6 = ArrayAdapter.createFromResource(this, R.array.arrayspinner6, android.R.layout.simple_spinner_item);
        spinner6.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner6.setAdapter(spinner6);

        mSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.__1Rubel))) {
                        mType1 = Moneta.CoinEntry.TYPEOFNOMINAL_1RUBEL;
                    } else if (selection.equals(getString(R.string.__2Rubel))) {
                        mType1 = Moneta.CoinEntry.TYPEOFNOMINAL_2RUBEL;
                    } else if (selection.equals(getString(R.string.__5Rubel))) {
                        mType1 = Moneta.CoinEntry.TYPEOFNOMINAL_5RUBEL;
                    } else {
                        mType1 = Moneta.CoinEntry.TYPEOFNOMINAL_10RUBEL;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType1 = Moneta.CoinEntry.TYPEOFNOMINAL_1RUBEL;
            }
        });

        mSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.__1))) {
                        mType2 = Moneta.CoinEntry.TYPEOFCONDITION_1;
                    } else if (selection.equals(getString(R.string.__2))) {
                        mType2 = Moneta.CoinEntry.TYPEOFCONDITION_2;
                    } else if (selection.equals(getString(R.string.__3))) {
                        mType2 = Moneta.CoinEntry.TYPEOFCONDITION_3;
                    } else if (selection.equals(getString(R.string.__4))) {
                        mType2 = Moneta.CoinEntry.TYPEOFCONDITION_4;
                    } else if (selection.equals(getString(R.string.__5))) {
                        mType2 = Moneta.CoinEntry.TYPEOFCONDITION_5;
                    } else if (selection.equals(getString(R.string.__6))) {
                        mType2 = Moneta.CoinEntry.TYPEOFCONDITION_6;
                    } else {
                        mType2 = Moneta.CoinEntry.TYPEOFCONDITION_7;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType2 = Moneta.CoinEntry.TYPEOFCONDITION_5;

            }
        });

        mSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.__DA))) {
                        mType3 = Moneta.CoinEntry.TYPEOFMAGNET_DA;
                    } else {
                        mType3 = Moneta.CoinEntry.TYPEOFMAGNET_NET;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType3 = Moneta.CoinEntry.TYPEOFMAGNET_DA;

            }
        });

        mSpinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.__M))) {
                        mType4 = Moneta.CoinEntry.TYPEOFSTAMP_M;
                    } else {
                        mType4 = Moneta.CoinEntry.TYPEOFSTAMP_S;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType4 = Moneta.CoinEntry.TYPEOFSTAMP_S;

            }
        });

        mSpinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.__OB))) {
                        mType5 = Moneta.CoinEntry.TYPEOFTYPE_OB;
                    } else if (selection.equals(getString(R.string.__YB))) {
                        mType5 = Moneta.CoinEntry.TYPEOFTYPE_YB;
                    } else {
                        mType5 = Moneta.CoinEntry.TYPEOFTYPE_IN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType5 = Moneta.CoinEntry.TYPEOFTYPE_OB;

            }
        });

        mSpinner6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.__WHITE))) {
                        mType6 = Moneta.CoinEntry.TYPEOFMATERIAL_WHITE;
                    } else if (selection.equals(getString(R.string.__YELLOW))) {
                        mType6 = Moneta.CoinEntry.TYPEOFMATERIAL_YELLOW;
                    } else {
                        mType6 = Moneta.CoinEntry.TYPEOFMATERIAL_OTHER;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType6 = Moneta.CoinEntry.TYPEOFMATERIAL_WHITE;

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menudefine, menu);
        return true;
    }

    //fix
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
        if (mCurrentCoinUri == null) {
            MenuItem item = (MenuItem) menu.findItem(R.id.delete);
            item.setVisible(true);
        }
        return true;
    }


    //гиблое дело выводить что-то в меню, но пусть будет здесь
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                saveCoin();
                if (hashAllRequiredValues == true) {
                    finish();
                }
                return true;

            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mCoinHasChanged) {

                    NavUtils.navigateUpFromSameTask(DefineActivity.this);
                    return true;

                }

                DialogInterface.OnClickListener discardButton = (dialog, which) ->
                        NavUtils.navigateUpFromSameTask(DefineActivity.this);
                showUnsavedChangesDialog(discardButton);
                return true;


        }
        return super.onOptionsItemSelected(item);
    }


    private boolean saveCoin() {

        String year = mYearEditText.getText().toString().trim();
        String price = mPriceEditText.getText().toString().trim();
        String description = mDescriptionEditText.getText().toString().trim();

        if (mCurrentCoinUri == null && TextUtils.isEmpty(year) && TextUtils.isEmpty(price) && TextUtils.isEmpty(description)
                && mType1 == Moneta.CoinEntry.TYPEOFNOMINAL_1RUBEL &&
                mType2 == Moneta.CoinEntry.TYPEOFCONDITION_5 &&
                mType3 == Moneta.CoinEntry.TYPEOFMAGNET_NET &&
                mType4 == Moneta.CoinEntry.TYPEOFSTAMP_M && mType5 == Moneta.CoinEntry.TYPEOFTYPE_OB
                && mType6 == Moneta.CoinEntry.TYPEOFMATERIAL_WHITE) {

            hashAllRequiredValues = true;
            return hashAllRequiredValues;
        }

        ContentValues values = new ContentValues();

        if (TextUtils.isEmpty(year)) {
            Toast.makeText(this, "Введите год", Toast.LENGTH_SHORT).show();
            return hashAllRequiredValues;

        } else {
            values.put(Moneta.CoinEntry.COLUMN_YEAR, year);
        }

        if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Введите цену ", Toast.LENGTH_SHORT).show();
            return hashAllRequiredValues;

        } else {
            values.put(Moneta.CoinEntry.COLUMN_PRICE, price);
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Добавьте описание", Toast.LENGTH_SHORT).show();
            return hashAllRequiredValues;

        } else {
            values.put(Moneta.CoinEntry.COLUMN_DESCRIPTION, description);
        }

        values.put(Moneta.CoinEntry.COLUMN_NOMINAL, mType1);
        values.put(Moneta.CoinEntry.COLUMN_CONDITION, mType2);
        values.put(Moneta.CoinEntry.COLUMN_MAGNET, mType3);
        values.put(Moneta.CoinEntry.COLUMN_STAMP, mType4);
        values.put(Moneta.CoinEntry.COLUMN_TYPE, mType5);
        values.put(Moneta.CoinEntry.COLUMN_MATERIAL, mType6);

        if (mCurrentCoinUri == null) {
            Uri newUri = getContentResolver().insert(Moneta.CoinEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Ошибка при сохранении", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Успешное сохранение", Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentCoinUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Ошибка обновленния", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Успешное обновление", Toast.LENGTH_SHORT).show();
            }
        }

        hashAllRequiredValues = true;
        return hashAllRequiredValues;
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                Moneta.CoinEntry._ID,
                Moneta.CoinEntry.COLUMN_NOMINAL,
                Moneta.CoinEntry.COLUMN_CONDITION,
                Moneta.CoinEntry.COLUMN_MAGNET,
                Moneta.CoinEntry.COLUMN_STAMP,
                Moneta.CoinEntry.COLUMN_TYPE,
                Moneta.CoinEntry.COLUMN_MATERIAL,
                Moneta.CoinEntry.COLUMN_YEAR,
                Moneta.CoinEntry.COLUMN_PRICE,
                Moneta.CoinEntry.COLUMN_DESCRIPTION};

        return new CursorLoader(this, mCurrentCoinUri, projection, null, null, null);
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nominal = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_NOMINAL);
            int condition = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_CONDITION);
            int magnet = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_MAGNET);
            int stamp = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_STAMP);
            int type = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_TYPE);
            int material = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_MATERIAL);
            int year = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_YEAR);
            int price = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_PRICE);
            int description = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_DESCRIPTION);


            String nominal_tos = cursor.getString(nominal);
            String condition_tos = cursor.getString(condition);
            String magnet_tos = cursor.getString(magnet);
            String stamp_tos = cursor.getString(stamp);
            String type_tos = cursor.getString(type);
            String material_tos = cursor.getString(material);
            String year_tos = cursor.getString(year);
            String price_tos = cursor.getString(price);
            String description_tos = cursor.getString(description);


            mYearEditText.setText(year_tos);
            mPriceEditText.setText(price_tos);
            mDescriptionEditText.setText(description_tos);





            switch (nominal_tos) {
                case Moneta.CoinEntry.TYPEOFNOMINAL_1RUBEL:
                    mSpinner1.setSelection(0);
                    break;
                case Moneta.CoinEntry.TYPEOFNOMINAL_2RUBEL:
                    mSpinner1.setSelection(1);
                    break;
                case Moneta.CoinEntry.TYPEOFNOMINAL_5RUBEL:
                    mSpinner1.setSelection(2);
                    break;
                case Moneta.CoinEntry.TYPEOFNOMINAL_10RUBEL:
                    mSpinner1.setSelection(3);
                    break;
                default:
                    mSpinner1.setSelection(0);
            }

            switch (condition_tos) {
                case Moneta.CoinEntry.TYPEOFCONDITION_1:
                    mSpinner2.setSelection(0);
                    break;

                case Moneta.CoinEntry.TYPEOFCONDITION_2:
                    mSpinner2.setSelection(1);
                    break;

                case Moneta.CoinEntry.TYPEOFCONDITION_3:
                    mSpinner2.setSelection(2);
                    break;

                case Moneta.CoinEntry.TYPEOFCONDITION_4:
                    mSpinner2.setSelection(3);
                    break;

                case Moneta.CoinEntry.TYPEOFCONDITION_5:
                    mSpinner2.setSelection(4);
                    break;

                case Moneta.CoinEntry.TYPEOFCONDITION_6:
                    mSpinner2.setSelection(5);
                    break;

                case Moneta.CoinEntry.TYPEOFCONDITION_7:
                    mSpinner2.setSelection(6);
                    break;

                default:
                    mSpinner2.setSelection(0);
            }

            switch (magnet_tos) {
                case Moneta.CoinEntry.TYPEOFMAGNET_DA:
                    mSpinner3.setSelection(0);
                    break;

                case Moneta.CoinEntry.TYPEOFMAGNET_NET:
                    mSpinner3.setSelection(1);
                    break;
                default:
                    mSpinner3.setSelection(0);
            }

            switch (stamp_tos) {
                case Moneta.CoinEntry.TYPEOFSTAMP_S:
                    mSpinner4.setSelection(1);
                    break;

                default:
                    mSpinner4.setSelection(0);
            }

            switch (type_tos) {
                case Moneta.CoinEntry.TYPEOFTYPE_OB:
                    mSpinner5.setSelection(0);
                    break;

                case Moneta.CoinEntry.TYPEOFTYPE_YB:
                    mSpinner5.setSelection(1);
                    break;

                case Moneta.CoinEntry.TYPEOFTYPE_IN:
                    mSpinner5.setSelection(2);
                    break;

                default:
                    mSpinner5.setSelection(0);
            }

            switch (material_tos) {
                case Moneta.CoinEntry.TYPEOFMATERIAL_WHITE:
                    mSpinner6.setSelection(0);
                    break;

                case Moneta.CoinEntry.TYPEOFMATERIAL_YELLOW:
                    mSpinner6.setSelection(2);
                    break;

                case Moneta.CoinEntry.TYPEOFMATERIAL_OTHER:
                    mSpinner6.setSelection(1);
                    break;

                default:
                    mSpinner6.setSelection(0);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mYearEditText.setText("");
        mPriceEditText.setText("");
        mDescriptionEditText.setText("");

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteCoin();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteCoin() {
        if (mCurrentCoinUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentCoinUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    @Override
    public void onBackPressed() {
        // Если продукт не изменился, продолжайте нажимать кнопку "Назад".
        if (!mCoinHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                (dialogInterface, i) -> {
                    // Пользователь нажал кнопку "Отменить", чтобы закрыть текущее действие.
                    finish();
                };

        showUnsavedChangesDialog(discardButtonClickListener);



    }
}


