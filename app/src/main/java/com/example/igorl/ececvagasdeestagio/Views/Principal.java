package com.example.igorl.ececvagasdeestagio.Views;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.igorl.ececvagasdeestagio.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Principal extends AppCompatActivity {
    private TextView nome;
    private TextView email;

    private FirebaseAuth usuarioFirebase;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        nome = (TextView) findViewById(R.id.usuarioNome);
        email = (TextView) findViewById(R.id.usuarioEmail);

    }

    @Override
    protected void onStart() {
        super.onStart();
        usuarioFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao();
        nome.setText("id: "+ usuarioFirebase.getCurrentUser().getUid());
        email.setText("Email: "+ usuarioFirebase.getCurrentUser().getEmail());
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
                startActivity(intent_perfil);

                break;
            case R.id.action_sair:

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(Principal.this);
                builder.setTitle("Sair")
                        .setMessage("Vai deseja sair?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                usuarioFirebase.signOut();
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
