package com.example.igorl.ececvagasdeestagio.Views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.Adapters.UserAdapter;
import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.Utils.AESCrypt;
import com.example.igorl.ececvagasdeestagio.Utils.RecyclerTouchListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class UsuariosSolicitados extends AppCompatActivity implements DatabaseReference.CompletionListener{

    private Toolbar mToobar;
    private RecyclerView mRecyclerView;
    private List<Usuario> mlistUsuarios;
    private DatabaseReference mFirebaseDatabase;
    private UserAdapter mUserAdapter;
    private Usuario mUsuario;
    private AlertDialog alerta;
    private FirebaseAuth mFirebaseAuth;
    private TextView textTextoVazio;
    private ProgressBar progressBar;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios_solicitados);

        mToobar = (Toolbar) findViewById(R.id.toolbar_usuarios_solicitados);
        mToobar.setTitle(R.string.usuarios_solicitados);
        mToobar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progress_usuario_solicitantes);

        mFirebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        mFirebaseDatabase = ConfiguracaoFirebase.getFirebase();

        textTextoVazio = (TextView) findViewById(R.id.textViewTextoVazio) ;

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewUsuariosSolicitados);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mlistUsuarios = new ArrayList<>();

        mUserAdapter = new UserAdapter(UsuariosSolicitados.this, mlistUsuarios);

        mRecyclerView.setAdapter(mUserAdapter);

        mFirebaseDatabase = ConfiguracaoFirebase.getFirebase().child("usuarios").child("solicitantes");
        mFirebaseDatabase.orderByChild("nome").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mlistUsuarios.removeAll(mlistUsuarios);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    mlistUsuarios.add(usuario);
                }
                if(mlistUsuarios.isEmpty()){
                    textTextoVazio.setVisibility(View.VISIBLE);
                }
                mUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mUsuario = mlistUsuarios.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(UsuariosSolicitados.this);
                builder.setTitle("Confirma Solicitação?");
                builder.setMessage("Confirmar solicitação do usuário: " + mUsuario.getNome() + " ?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBar.setVisibility( View.VISIBLE );
                        createUserFBAutentication();
                     }
                });

                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                alerta = builder.create();
                alerta.show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void createUserFBAutentication(){

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:655248022019:android:ede0ecba58f106ad")
                .setApiKey("AIzaSyB94IF2jwcG9IDlJELHZC2gz8KVrV4LxkI")
                .setDatabaseUrl("https://ececvagasdeestagio.firebaseio.com/")
                .build();

        FirebaseApp app;

        try{
            FirebaseApp.initializeApp(this,options,"Secundary");
            app = FirebaseApp.getInstance("Secundary");
        }catch (IllegalStateException e){
            app = FirebaseApp.getInstance("Secundary");
        }

        try {
            mUsuario.setSenha(AESCrypt.decrypt(mUsuario.getSenha()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final FirebaseAuth firebaseAuth2 = FirebaseAuth.getInstance(app);
        firebaseAuth2.createUserWithEmailAndPassword(
                mUsuario.getEmail(),
                mUsuario.getSenha()
        ).addOnCompleteListener(UsuariosSolicitados.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    removeUserChildSolicitadoFromDatabase();
                    mUsuario.setId(task.getResult().getUser().getUid());
                    try {
                        mUsuario.setSenha(AESCrypt.encrypt(mUsuario.getSenha()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mUsuario.salvarUserFBDatabase(UsuariosSolicitados.this);
                    firebaseAuth2.signOut();

                }else{
                    progressBar.setVisibility( View.INVISIBLE );
                    Toast.makeText(UsuariosSolicitados.this, "Erro ao aprovar usuário", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void removeUserChildSolicitadoFromDatabase(){
        mFirebaseDatabase = ConfiguracaoFirebase.getFirebase().child("usuarios").child("solicitantes");
        mFirebaseDatabase.child(mUsuario.getId()).removeValue();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        progressBar.setVisibility( View.INVISIBLE );
        Toast.makeText(UsuariosSolicitados.this, "Usuário Aprovado", Toast.LENGTH_LONG).show();
    }
}
