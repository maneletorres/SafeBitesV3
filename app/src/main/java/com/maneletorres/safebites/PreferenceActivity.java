package com.maneletorres.safebites;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.maneletorres.safebites.utils.Utils.CLASS_NAME;
import static com.maneletorres.safebites.utils.Utils.TOAST_MESSAGE;

public class PreferenceActivity extends AppCompatActivity implements View.OnClickListener {
    // Firebase variables:
    private String mUid;
    private String mDisplayName;
    private String mEmail;
    private DatabaseReference mUserDBRef;
    private DatabaseReference mAllergiesDBRef;
    private DatabaseReference mUsersProductDBRef;
    private ValueEventListener mAllergiesValueEventListener;
    private ValueEventListener mUsersChildEventListener;

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
    private String mAlertDialogMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        // Placement of the back arrow on the Action Bar:
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            callingActivityName = extras.getString(CLASS_NAME);

            // Firebase components initialization:
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                mUid = firebaseUser.getUid();
                mDisplayName = firebaseUser.getDisplayName();
                mEmail = firebaseUser.getEmail();
            } else {
                mUid = getString(R.string.anonymous);
                mDisplayName = getString(R.string.anonymous);
                mEmail = getString(R.string.anonymous);
            }

            mUserDBRef = FirebaseDatabase.getInstance().getReference("users").child(mUid);
            mAllergiesDBRef = mUserDBRef.child("allergies");

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
            sulphurDioxideAndSulphitesCheckBox =
                    findViewById(R.id.sulphur_dioxide_and_sulphites_allergy_text_view);
            lupinAllergyCheckBox = findViewById(R.id.lupin_allergy_text_view);
            molluscsAllergyCheckBox = findViewById(R.id.molluscs_allergy_text_view);

            // OnClickListener configuration on the button to save changes on the user's allergens:
            Button saveDataButton = findViewById(R.id.save_data_button);
            saveDataButton.setOnClickListener(this);

            // Depending on the value of the callingActivityName variable, both the separating view
            // and the Danger Zone LinearLayout will be hidden or displayed:
            if (callingActivityName.equals("AuthActivity")) {
                mAlertDialogMessage = getString(R.string.save_data_alert_dialog_message_2,
                        mDisplayName, mEmail);

                saveDataButton.setText(getString(R.string.create_user_button));
                findViewById(R.id.separator_view).setVisibility(View.GONE);
                findViewById(R.id.danger_zone_linear_layout).setVisibility(View.GONE);
            } else if (callingActivityName.equals("MainActivity")) {
                mAlertDialogMessage = getString(R.string.save_data_alert_dialog_message_1);

                // OnClickListener configuration on the button to save changes on the user's
                // allergens:
                findViewById(R.id.delete_user_button).setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_data_button:
                new AlertDialog.Builder(this)
                        .setPositiveButton(getString(R.string.alert_dialog_positive_button), (dialog, which) -> {
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
                            allergies.put("en:sesame-seeds",
                                    sesameSeedsAllergyCheckBox.isChecked());
                            allergies.put("en:sulphur-dioxide-and-sulphites",
                                    sulphurDioxideAndSulphitesCheckBox.isChecked());
                            allergies.put("en:lupin", lupinAllergyCheckBox.isChecked());
                            allergies.put("en:molluscs", molluscsAllergyCheckBox.isChecked());

                            // Modification of the allergens of the current user in the FRDB:
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("allergies", allergies);

                            Intent intent = new Intent(this, MainActivity.class);
                            if (callingActivityName.equals("AuthActivity")) {
                                map.put("displayName", mDisplayName);
                                map.put("eMail", mEmail);

                                intent.putExtra(TOAST_MESSAGE, 0);
                            } else if (callingActivityName.equals("MainActivity")) {
                                intent.putExtra(TOAST_MESSAGE, 2);
                            }

                            mUserDBRef.updateChildren(map);
                            startActivity(intent);
                        })
                        .setNegativeButton(getString(R.string.alert_dialog_negative_button), (dialog, which) -> {

                        })
                        .setTitle(getString(R.string.save_data_alert_dialog_title))
                        .setMessage(mAlertDialogMessage)
                        .create()
                        .show();
                break;
            case R.id.delete_user_button:
                new AlertDialog.Builder(this)
                        .setPositiveButton(getString(R.string.alert_dialog_positive_button), (dialog, which) -> {
                            // Removal the current user's account:

                            // 'productsUser':
                            FirebaseDatabase.getInstance().getReference().child("productsUser").child(mUid).removeValue();

                            // 'usersProduct':
                            mUsersProductDBRef = FirebaseDatabase.getInstance().getReference().child("usersProduct");
                            mUsersChildEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Log.v("STATUS", "Current UID: " + mUid);

                                    if (dataSnapshot.getValue() != null) {
                                        HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();

                                        for (Map.Entry<String, Object> products_upc : map.entrySet()) {
                                            Log.v("STATUS", "Bucle externo - Key: " + products_upc.getKey());

                                            String product_upc = products_upc.getKey();
                                            HashMap<String, Boolean> value = (HashMap<String, Boolean>) products_upc.getValue();

                                            int count = 0;
                                            ArrayList<String> arrayList = new ArrayList<>();
                                            for (Map.Entry<String, Boolean> uids : value.entrySet()) {
                                                Log.v("STATUS", "Bucle interno - Value: " + uids.getKey());
                                                arrayList.add(uids.getKey());
                                                count++;
                                            }

                                            if (count == 1) {
                                                if (arrayList.get(0).equals(mUid)) {
                                                    Log.v("STATUS", "Deleting the product " + product_upc + " from 'usersProduct'.");
                                                    mUsersProductDBRef.child(product_upc).removeValue();

                                                    // 'products':
                                                    Log.v("STATUS", "Deleting the product " + product_upc + " from 'products'.");
                                                    FirebaseDatabase.getInstance().getReference().child("products").child(product_upc).removeValue();
                                                }
                                            } else {
                                                Log.v("STATUS", "Deleting the UID " + mUid + " of the product " + product_upc + " from 'usersProduct'.");
                                                mUsersProductDBRef.child(product_upc).child(mUid).removeValue();
                                            }
                                        }
                                    }

                                    Log.v("STATUS", "¡Closing session!");
                                    signOff();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            };
                            mUsersProductDBRef.addListenerForSingleValueEvent(mUsersChildEventListener);

                            // 'users':
                            mUserDBRef.removeValue();
                        })
                        .setNegativeButton(getString(R.string.alert_dialog_negative_button), (dialog, which) -> {
                        })
                        .setTitle(getString(R.string.delete_user_alert_dialog_title))
                        .setMessage(getString(R.string.delete_user_alert_dialog_message,
                                mDisplayName, mEmail))
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
    protected void onResume() {
        super.onResume();

        attachDatabaseReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();

        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mAllergiesValueEventListener == null) {
            mAllergiesValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Boolean> allergies = (HashMap<String,
                            Boolean>) dataSnapshot.getValue();
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
                        sulphurDioxideAndSulphitesCheckBox
                                .setChecked(allergies.get("en:sulphur-dioxide-and-sulphites"));
                        lupinAllergyCheckBox.setChecked(allergies.get("en:lupin"));
                        molluscsAllergyCheckBox.setChecked(allergies.get("en:molluscs"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mAllergiesDBRef.addListenerForSingleValueEvent(mAllergiesValueEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mAllergiesValueEventListener != null) {
            mAllergiesDBRef.removeEventListener(mAllergiesValueEventListener);
            mAllergiesValueEventListener = null;
        }

        if (mUsersChildEventListener != null) {
            mUsersProductDBRef.removeEventListener(mUsersChildEventListener);
            mUsersChildEventListener = null;
        }
    }

    private void returnToParentActivity() {
        if (callingActivityName.equals("AuthActivity")) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(task -> {
                        // User is now signed out:
                        startActivity(new Intent(this, AuthActivity.class));
                        finish();
                    });
        } else if (callingActivityName.equals("MainActivity")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void signOff() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    // User is now signed out:
                    Intent intent = new Intent(this,
                            AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }
}