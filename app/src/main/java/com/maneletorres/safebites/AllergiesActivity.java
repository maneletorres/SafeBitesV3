package com.maneletorres.safebites;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

import static com.maneletorres.safebites.utils.Utils.sUser;

public class AllergiesActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mAllergiesDatabaseReference;
    private CheckBox fishAllergyCheckBox;
    private CheckBox glutenAllergyCheckBox;
    private CheckBox crustaceansAllergyCheckBox;
    private CheckBox eggsAllergyCheckBox;
    private CheckBox peanutsAllergyCheckBox;
    private CheckBox soyBeansAllergyCheckBox;
    private CheckBox milkAllergyCheckBox;
    private CheckBox nutsAllergyCheckBox;
    private CheckBox celeryAllergyCheckBox;
    private CheckBox mustardAllergyCheckBox;
    private CheckBox sesameSeedsAllergyCheckBox;
    private CheckBox sulphurDioxideAndSulphitesCheckBox;
    private CheckBox lupinAllergyCheckBox;
    private CheckBox molluscsAllergyCheckBox;
    private String parentActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            parentActivity = extras.getString("CLASS_NAME");
        }

        // Firebase Realtime Database components initialization:
        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(sUser.getUser_id());
        mAllergiesDatabaseReference = mUserDatabaseReference.child("allergies");
        mAllergiesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot != null){
                    Map<String, Boolean> allergies = (HashMap<String, Boolean>) snapshot.getValue();
                    if(allergies != null){
                        glutenAllergyCheckBox.setChecked(allergies.get("en:gluten"));
                        crustaceansAllergyCheckBox.setChecked(allergies.get("en:crustaceans"));
                        eggsAllergyCheckBox.setChecked(allergies.get("en:eggs"));
                        fishAllergyCheckBox.setChecked(allergies.get("en:fish"));
                        peanutsAllergyCheckBox.setChecked(allergies.get("en:peanuts"));
                        soyBeansAllergyCheckBox.setChecked(allergies.get("en:soybeans"));
                        milkAllergyCheckBox.setChecked(allergies.get("en:milk"));
                        nutsAllergyCheckBox.setChecked(allergies.get("en:nuts"));
                        celeryAllergyCheckBox.setChecked(allergies.get("en:celery"));
                        mustardAllergyCheckBox.setChecked(allergies.get("en:mustard"));
                        sesameSeedsAllergyCheckBox.setChecked(allergies.get("en:sesame-seeds"));
                        sulphurDioxideAndSulphitesCheckBox.setChecked(allergies.get("en:sulphur-dioxide-and-sulphites"));
                        lupinAllergyCheckBox.setChecked(allergies.get("en:lupin"));
                        molluscsAllergyCheckBox.setChecked(allergies.get("en:molluscs"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // CheckBox initialization:
        glutenAllergyCheckBox = findViewById(R.id.gluten_allergy_text_view);
        crustaceansAllergyCheckBox = findViewById(R.id.crustaceans_allergy_text_view);
        eggsAllergyCheckBox = findViewById(R.id.eggs_allergy_text_view);
        fishAllergyCheckBox = findViewById(R.id.fish_allergy_text_view);
        peanutsAllergyCheckBox = findViewById(R.id.peanuts_allergy_text_view);
        soyBeansAllergyCheckBox = findViewById(R.id.soybeans_allergy_text_view);
        milkAllergyCheckBox = findViewById(R.id.milk_allergy_text_view);
        nutsAllergyCheckBox = findViewById(R.id.nuts_allergy_text_view);
        celeryAllergyCheckBox = findViewById(R.id.celery_allergy_text_view);
        mustardAllergyCheckBox = findViewById(R.id.mustard_allergy_text_view);
        sesameSeedsAllergyCheckBox = findViewById(R.id.sesame_seeds_allergy_text_view);
        sulphurDioxideAndSulphitesCheckBox = findViewById(R.id.sulphur_dioxide_and_sulphites_allergy_text_view);
        lupinAllergyCheckBox = findViewById(R.id.lupin_allergy_text_view);
        molluscsAllergyCheckBox = findViewById(R.id.molluscs_allergy_text_view);

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.save_button:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton("OK", (dialog, which) -> {
                    Map<String, Boolean> allergies = new HashMap<>();
                    allergies.put("en:gluten", glutenAllergyCheckBox.isChecked());
                    allergies.put("en:crustaceans", crustaceansAllergyCheckBox.isChecked());
                    allergies.put("en:eggs", eggsAllergyCheckBox.isChecked());
                    allergies.put("en:fish", fishAllergyCheckBox.isChecked());
                    allergies.put("en:peanuts", peanutsAllergyCheckBox.isChecked());
                    allergies.put("en:soybeans", soyBeansAllergyCheckBox.isChecked());
                    allergies.put("en:milk", milkAllergyCheckBox.isChecked());
                    allergies.put("en:nuts", nutsAllergyCheckBox.isChecked());
                    allergies.put("en:celery", celeryAllergyCheckBox.isChecked());
                    allergies.put("en:mustard", mustardAllergyCheckBox.isChecked());
                    allergies.put("en:sesame-seeds", sesameSeedsAllergyCheckBox.isChecked());
                    allergies.put("en:sulphur-dioxide-and-sulphites", sulphurDioxideAndSulphitesCheckBox.isChecked());
                    allergies.put("en:lupin", lupinAllergyCheckBox.isChecked());
                    allergies.put("en:molluscs", molluscsAllergyCheckBox.isChecked());

                    sUser.setAllergies(allergies);

                    //mUserDatabaseReference.setValue(sUser);
                    mAllergiesDatabaseReference.setValue(allergies);

                    if(parentActivity.equals("AuthActivity")){
                        Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                    } else if(parentActivity.equals("MainActivity")){
                        Toast.makeText(this, "Changes in stored allergens.", Toast.LENGTH_SHORT).show();
                    }

                    startActivity(new Intent(AllergiesActivity.this, MainActivity.class));
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> {

                });
                builder.setMessage("Are you sure you want to save the changes made to your allergies?").setTitle("Warning");
                builder.create();
                builder.show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        switch (parentActivity) {
            case "AuthActivity":
                Log.v("Volviendo a ", "AuthActivity");
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // User is now signed out:
                                Intent intent = new Intent(AllergiesActivity.this, AuthActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                Intent intent = new Intent(this, AuthActivity.class);
                startActivity(intent);
                break;
            case "MainActivity":
                Log.v("Volviendo a ", "MainActivity");
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                super.onBackPressed();
                break;
        }
    }
}