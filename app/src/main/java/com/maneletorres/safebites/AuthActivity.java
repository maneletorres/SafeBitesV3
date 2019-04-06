package com.maneletorres.safebites;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maneletorres.safebites.entities.User;
import com.maneletorres.safebites.utils.Utils;

import java.util.Arrays;

import static com.maneletorres.safebites.utils.Utils.USER;

public class AuthActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;

    // Firebase variables:
    private DatabaseReference mUsersDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v("onCreate","onCreate");
        super.onCreate(savedInstanceState);

        // Initialize Firebase components:
        mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                Log.v("User is ","NULL");
                // User is signed in:
                //onSignedInInitialize(firebaseUser.getDisplayName());

                String mUid = firebaseUser.getUid();
                String mEmail = firebaseUser.getEmail();
                Utils.sUser = new User(mUid, mEmail);

                mUsersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (checkUserExists(mUid, dataSnapshot)) {
                            Toast.makeText(AuthActivity.this, "Welcome back to SafeBites " + mEmail + "!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AuthActivity.this, MainActivity.class));
                        } else {
                            Intent intent = new Intent(AuthActivity.this, AllergiesActivity.class);
                            intent.putExtra("CLASS_NAME", "AuthActivity");
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                Log.v("User is ","NOT NULL");
                // User is signed out:
                //onSignedOutCleanup();
                startActivityForResult(AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setLogo(R.drawable.food_icon)
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.TwitterBuilder().build()))
                                .build(),
                        RC_SIGN_IN);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.v("Pasando", " por aquí!");
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) {
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
        /*detachDatabaseReadListener();
        mMessageAdapter.clear();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public boolean checkUserExists(String uid, DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey() != null && ds.getKey().equals(uid)) {
                return true;
            }
        }

        return false;
    }

    /*private void onSignedInInitialize(String username){
        mUsername = username;
        attachDatabaseReadListener();

    }

    private void onSignedOutCleanup(){
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener(){
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    mMessageAdapter.add(friendlyMessage);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener(){
        if(mChildEventListener != null){
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }*/
}
