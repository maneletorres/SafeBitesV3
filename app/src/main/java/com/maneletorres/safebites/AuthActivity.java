package com.maneletorres.safebites;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maneletorres.safebites.entities.User;

import java.util.Arrays;

import static com.maneletorres.safebites.utils.Utils.CLASS_NAME;
import static com.maneletorres.safebites.utils.Utils.TOAST_MESSAGE;
import static com.maneletorres.safebites.utils.Utils.sUID;
import static com.maneletorres.safebites.utils.Utils.sUser;

public class AuthActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;

    // Firebase variables:
    private DatabaseReference mUsersDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private AuthStateListener mAuthStateListener;
    private ValueEventListener mValueEventListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase components:
        mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                // User is signed in:
                sUID = firebaseUser.getUid();
                sUser = new User(firebaseUser.getEmail(), firebaseUser.getDisplayName());
            } else {
                // User is signed out:
                startActivityForResult(AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setLogo(R.drawable.food_icon)
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                                        new AuthUI.IdpConfig.TwitterBuilder().build(),
                                        new AuthUI.IdpConfig.EmailBuilder().build()))
                                .build(),
                        RC_SIGN_IN);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                //attachDatabaseReadListener();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Signed in canceled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mValueEventListener == null) {
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(sUID)) {
                        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                        intent.putExtra(TOAST_MESSAGE, 1);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(AuthActivity.this, PreferenceActivity.class);
                        intent.putExtra(CLASS_NAME, "AuthActivity");
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mUsersDatabaseReference.addListenerForSingleValueEvent(mValueEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mValueEventListener != null) {
            mUsersDatabaseReference.removeEventListener(mValueEventListener);
            mValueEventListener = null;
        }
    }
}
