package com.example.igorl.ececvagasdeestagio;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.Adapters.UserAdapter;
import com.example.igorl.ececvagasdeestagio.Adapters.UsuarioAdapter;
import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.Views.RecuperarAcesso;
import com.example.igorl.ececvagasdeestagio.Views.SolicitarCadastro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class UsuariosSolicitados extends AppCompatActivity{

    private Toolbar mToobar;
    private RecyclerView mRecyclerView;
    private List<Usuario> mlistUsuarios;
    private DatabaseReference mFirebaseDatabase;
    private UserAdapter mUserAdapter;
    private Usuario mUsuario;
    private AlertDialog alerta;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios_solicitados);

        mToobar = (Toolbar) findViewById(R.id.toolbar_usuarios_solicitados);
        mToobar.setTitle("Usuários Solicitados");
        mToobar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseDatabase = ConfiguracaoFirebase.getFirebase();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewUsuariosSolicitados);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mlistUsuarios = new ArrayList<>();

        mUserAdapter = new UserAdapter(UsuariosSolicitados.this, mlistUsuarios);

        mRecyclerView.setAdapter(mUserAdapter);

        mFirebaseDatabase = ConfiguracaoFirebase.getFirebase().child("usuarios").child("solicitados");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mlistUsuarios.removeAll(mlistUsuarios);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    mlistUsuarios.add(usuario);
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
                builder.setMessage("Confirmar Solicitação do Usuário: " + mUsuario.getNome() + " ?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        createUserAuthentication();
                        removeUserChildSolicitadoFromDatabase();
                        firebaseAuth.signOut();
                        Toast.makeText(UsuariosSolicitados.this, "Usuário aprovado com sucesso!", Toast.LENGTH_LONG).show();
                    }
                });

                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(UsuariosSolicitados.this);
                        builder.setTitle("Confirma Exclusão?");
                        builder.setMessage("Confirmar Exclusão do Usuário: " + mUsuario.getNome() + " ?");
                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeUserChildSolicitadoFromDatabase();
                                Toast.makeText(UsuariosSolicitados.this, "Usuário removido!", Toast.LENGTH_LONG).show();
                            }
                        });
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

    private void createUserAuthentication(){

        firebaseAuth.createUserWithEmailAndPassword(
                mUsuario.getEmail(),
                mUsuario.getSenha()
        ).addOnCompleteListener(UsuariosSolicitados.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    mUsuario.setId(task.getResult().getUser().getUid());
                    mUsuario.salvarUserFBDatabase();

                }
            }
        });

    }

    private void removeUserChildSolicitadoFromDatabase(){
        firebaseDatabase = ConfiguracaoFirebase.getFirebase().child("usuarios").child("solicitados");
        firebaseDatabase.child(mUsuario.getNome().toString()).removeValue();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return  super.onOptionsItemSelected(item);
    }
}
