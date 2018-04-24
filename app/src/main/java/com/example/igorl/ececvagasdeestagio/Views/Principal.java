package com.example.igorl.ececvagasdeestagio.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class Principal extends AppCompatActivity {
    private TextView nome;
    private TextView email;
    private Usuario mUsers;

    private Toolbar mToobar;

    Gson gson = new Gson();

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        mToobar = (Toolbar) findViewById(R.id.toolbar_principal);
        mToobar.setTitle("ECEC Vagas de Estágio");
        mToobar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToobar);

        nome = (TextView) findViewById(R.id.usuarioNome);
        email = (TextView) findViewById(R.id.usuarioEmail);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();

        firebaseDatabase = ConfiguracaoFirebase.getFirebase().child("usuarios").child("alunos").child(firebaseAuth.getCurrentUser().getUid());
        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers = dataSnapshot.getValue(Usuario.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Principal.this, "Erro ao Recuperar dados de usuário", Toast.LENGTH_LONG).show();
                firebaseAuth.signOut();
                Intent intent_sair = new Intent(Principal.this, Login.class);
                startActivity(intent_sair);
                finish();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        nome.setText("id: "+ firebaseAuth.getCurrentUser().getUid());
        email.setText("Email: "+ firebaseAuth.getCurrentUser().getEmail());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_adm_vagas:

                Intent intent_vagas = new Intent(Principal.this, AdministracaoVagas.class);
                startActivity(intent_vagas);

                break;
            case R.id.action_adm_users:

                Intent intent_users = new Intent(Principal.this, AdministracaoUsuarios.class);
                startActivity(intent_users);

                break;
            case R.id.action_perfil:

                Intent intent_perfil = new Intent(Principal.this, Perfil.class);
                intent_perfil.putExtra("usuario",gson.toJson(mUsers));
                startActivityForResult(intent_perfil, 2);

                break;
            case R.id.action_sair:

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(Principal.this);
                builder.setTitle("Sair")
                        .setMessage("Vai deseja sair?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                firebaseAuth.signOut();
                                Intent intent_sair = new Intent(Principal.this, Login.class);
                                startActivity(intent_sair);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
