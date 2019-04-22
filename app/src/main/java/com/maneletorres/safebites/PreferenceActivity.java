package com.maneletorres.safebites;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.maneletorres.safebites.utils.Utils.CLASS_NAME;
import static com.maneletorres.safebites.utils.Utils.TOAST_MESSAGE;
import static com.maneletorres.safebites.utils.Utils.sUID;
import static com.maneletorres.safebites.utils.Utils.sUser;

public class PreferenceActivity extends AppCompatActivity implements View.OnClickListener {
    // Firebase variables:
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mAllergiesDatabaseReference;
    private ValueEventListener mValueEventListener;

    // Other variables:
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
    private String callingActivityName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        // Placement of the back arrow on the Action Bar:
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Collection of data sent by AuthActivity and MainActivity:
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Collection of the variable sent by AuthActivity or MainActivity:
            callingActivityName = extras.getString(CLASS_NAME);

            // FRDB components initialization:
            mUserDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(sUID);
            mAllergiesDatabaseReference = mUserDatabaseReference.child("allergies");

            /*mAllergiesDatabaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Boolean value = (Boolean) dataSnapshot.getValue();
                    switch (Objects.requireNonNull(dataSnapshot.getKey())) {
                        case "en:gluten":
                            glutenAllergyCheckBox.setChecked(value);
                            break;
                        case "en:crustaceans":
                            crustaceansAllergyCheckBox.setChecked(value);
                            break;
                        case "en:eggs":
                            eggsAllergyCheckBox.setChecked(value);
                            break;
                        case "en:fish":
                            fishAllergyCheckBox.setChecked(value);
                            break;
                        case "en:peanuts":
                            peanutsAllergyCheckBox.setChecked(value);
                            break;
                        case "en:soybeans":
                            soyBeansAllergyCheckBox.setChecked(value);
                            break;
                        case "en:milk":
                            milkAllergyCheckBox.setChecked(value);
                            break;
                        case "en:nuts":
                            nutsAllergyCheckBox.setChecked(value);
                            break;
                        case "en:celery":
                            celeryAllergyCheckBox.setChecked(value);
                            break;
                        case "en:mustard":
                            mustardAllergyCheckBox.setChecked(value);
                            break;
                        case "en:sesame-seeds":
                            sesameSeedsAllergyCheckBox.setChecked(value);
                            break;
                        case "en:sulphur-dioxide-and-sulphites":
                            sulphurDioxideAndSulphitesCheckBox.setChecked(value);
                            break;
                        case "en:lupin":
                            lupinAllergyCheckBox.setChecked(value);
                            break;
                        case "en:molluscs":
                            molluscsAllergyCheckBox.setChecked(value);
                            break;
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });*/

            // Initialization of the components:
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

            // OnClickListener configuration on the button to discard changes on the user's allergens:
            findViewById(R.id.back_button).setOnClickListener(this);

            // OnClickListener configuration on the button to save changes on the user's allergens:
            findViewById(R.id.save_data_button).setOnClickListener(this);

            // Depending on the value of the callingActivityName variable, both the separating view
            // and the Danger Zone LinearLayout will be hidden or displayed:
            if (callingActivityName.equals("AuthActivity")) {
                findViewById(R.id.separator_view).setVisibility(View.GONE);
                findViewById(R.id.danger_zone_linear_layout).setVisibility(View.GONE);
            } else if (callingActivityName.equals("MainActivity")) {
                // OnClickListener configuration on the button to save changes on the user's allergens:
                findViewById(R.id.delete_user_button).setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                returnToParentActivity();
                break;
            case R.id.save_data_button:
                new AlertDialog.Builder(this)
                        .setPositiveButton("OK", (dialog, which) -> {
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

                            // Modification of the allergens of the current user in the FRDB:
                            //mUserDatabaseReference.setValue(sUser);
                            mAllergiesDatabaseReference.setValue(allergies);

                            Intent intent = new Intent(PreferenceActivity.this, MainActivity.class);
                            if (callingActivityName.equals("AuthActivity")) {
                                intent.putExtra(TOAST_MESSAGE, 0);
                            } else if (callingActivityName.equals("MainActivity")) {
                                intent.putExtra(TOAST_MESSAGE, 2);
                            }

                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {

                        })
                        .setTitle("Information:")
                        .setMessage("Are you sure you want to save the changes made to your allergies?")
                        .create()
                        .show();
                break;
            case R.id.delete_user_button:
                new AlertDialog.Builder(this)
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Deletion of the current user:
                            mUserDatabaseReference.removeValue();

                            // Closing of the current session:
                            AuthUI.getInstance()
                                    .signOut(this)
                                    .addOnCompleteListener(task -> {
                                        // User is now signed out:
                                        Intent intent = new Intent(PreferenceActivity.this, AuthActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    });
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                        })
                        .setTitle("Warning:")
                        .setMessage("This action cannot be undone. Are you sure you want to delete the user " + sUser.getDisplayName() + " with email " + sUser.getEmail() + "?")
                        .create()
                        .show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            returnToParentActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        returnToParentActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();

        detachDatabaseReadListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mValueEventListener == null) {
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Boolean> allergies = (HashMap<String, Boolean>) dataSnapshot.getValue();
                    if (allergies != null) {
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

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mAllergiesDatabaseReference.addListenerForSingleValueEvent(mValueEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mValueEventListener != null) {
            mAllergiesDatabaseReference.removeEventListener(mValueEventListener);
            mValueEventListener = null;
        }
    }

    public void returnToParentActivity() {
        if (callingActivityName.equals("AuthActivity")) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(task -> {
                        // User is now signed out:
                        startActivity(new Intent(PreferenceActivity.this, AuthActivity.class));
                        finish();
                    });
        } else if (callingActivityName.equals("MainActivity")) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}