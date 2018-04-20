package com.example.igorl.ececvagasdeestagio.DAO;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by igorl on 19/03/2018.
 */

public class ConfiguracaoFirebase {
    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth firebaseAuth;
    private static FirebaseAuth.AuthStateListener authStateListener;
    private static FirebaseUser firebaseUser;
    private static StorageReference firebaseStorage;

    public static DatabaseReference getFirebase(){
        if(referenciaFirebase == null){
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }

    public static FirebaseAuth getFirebaseAutenticacao(){
        if(firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if(user != null){
                        firebaseUser = user;
                    }
                }
            };
        }
        firebaseAuth.addAuthStateListener(authStateListener);
        return firebaseAuth;
    }

    public static StorageReference getFirebaseStorage(){
        if(firebaseStorage == null){
            firebaseStorage = FirebaseStorage.getInstance().getReference();
        }
        return firebaseStorage;
    }

    public static FirebaseUser getFirebaseUser(){
        return firebaseUser;
    }

    public static void logout(){
        firebaseAuth.signOut();
    }
}
