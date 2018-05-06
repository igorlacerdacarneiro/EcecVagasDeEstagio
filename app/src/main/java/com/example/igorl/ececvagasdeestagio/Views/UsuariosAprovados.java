package com.example.igorl.ececvagasdeestagio.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.Adapters.UserAdapter;
import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.Utils.RecyclerTouchListener;
import com.google.firebase.auth.FirebaseAuth;;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class UsuariosAprovados extends AppCompatActivity {

    private Toolbar mToobar;
    private RecyclerView mRecyclerView;
    private List<Usuario> mListUsuarios;
    private UserAdapter mUserAdapter;
    private Usuario mUsuario;
    private AlertDialog alerta;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FloatingActionButton addUsuarios;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios_aprovados);

        mToobar = (Toolbar) findViewById(R.id.toolbar_usuarios_aprovados);
        mToobar.setTitle("Usuários Aprovados");
        mToobar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addUsuarios = (FloatingActionButton) findViewById(R.id.floatingActionButtonAddUsuario);

        addUsuarios.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v){
                Intent intent = new Intent(UsuariosAprovados.this, CadastroUsuario.class);
                startActivity(intent);
            }
        });

        mFirebaseDatabase = ConfiguracaoFirebase.getFirebase();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewUsuariosAprovados);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mListUsuarios = new ArrayList<>();

        mUserAdapter = new UserAdapter(UsuariosAprovados.this, mListUsuarios);

        mRecyclerView.setAdapter(mUserAdapter);

        mFirebaseDatabase = ConfiguracaoFirebase.getFirebase().child("usuarios").child("aprovados");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListUsuarios.removeAll(mListUsuarios);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    mListUsuarios.add(usuario);
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
                mUsuario = mListUsuarios.get(position);
                Log.i("log", "LOG LOG mUsuario 1 =" + mUsuario.getId());
                Log.i("log", "LOG LOG mUsuario 2 =" + mUsuario.getNome());
                Log.i("log", "LOG LOG mUsuario 3 =" + mUsuario.getMatricula());
                Log.i("log", "LOG LOG mUsuario 4 =" + mUsuario.getTipo());
                Log.i("log", "LOG LOG mUsuario 5 =" + mUsuario.getEmail());
                Log.i("log", "LOG LOG mUsuario 6 =" + mUsuario.getSenha());
                Intent intent = new Intent(UsuariosAprovados.this, EditarUsuario.class);
                intent.putExtra("usuario",gson.toJson(mUsuario));
                startActivityForResult(intent, 2);
            }

            @Override
            public void onLongClick(View view, int position) {
                mUsuario = mListUsuarios.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(UsuariosAprovados.this);
                builder.setTitle("Confirma Exclusão?");
                builder.setMessage("Confirmar exclusão do usuário: " + mUsuario.getNome() + " ?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeUserChildSolicitadoFromDatabase();
                        Toast.makeText(UsuariosAprovados.this, "Usuário excluído com sucesso!", Toast.LENGTH_LONG).show();
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
        }));

    }

    private void removeUserChildSolicitadoFromDatabase(){

        mFirebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();

        mFirebaseDatabase = ConfiguracaoFirebase.getFirebase().child("usuarios").child("aprovados");
        mFirebaseDatabase.child(mUsuario.getId()).removeValue();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return  super.onOptionsItemSelected(item);
    }
}
