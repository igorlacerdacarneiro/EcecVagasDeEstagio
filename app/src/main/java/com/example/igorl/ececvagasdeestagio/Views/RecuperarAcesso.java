package com.example.igorl.ececvagasdeestagio.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.Utils.CommonActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarAcesso extends CommonActivity {

    private Button enviar;
    private EditText email;
    private FirebaseAuth firebaseAuth;
    private Toolbar mToobar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_acesso);

        mToobar = (Toolbar) findViewById(R.id.toolbar_recuperar_acesso);
        mToobar.setTitle(R.string.recuperar_acesso);
        mToobar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        initViews();
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmailValid(email.getText().toString())){
                    openProgressBar();
                    firebaseAuth
                            .sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RecuperarAcesso.this, "E-mail enviado com sucesso", Toast.LENGTH_SHORT).show();
                                voltarTelaLogin();
                            }else{
                                closeProgressBar();
                                Toast.makeText(RecuperarAcesso.this, "Ocorrou erro, tente novamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    protected void initViews(){
        enviar = (Button) findViewById(R.id.botaoRecuperarEnviar);
        email = (EditText) findViewById(R.id.campoRecuperarEmail);
        progressBar = (ProgressBar) findViewById(R.id.recuperar_progress);
    }

    protected void initUser(){
    }

    public void voltarTelaLogin(){
        Intent intent = new Intent(RecuperarAcesso.this, Login.class);
        startActivity(intent);
        closeProgressBar();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            if(!email.getText().toString().trim().equals("")) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(RecuperarAcesso.this);
                builder.setTitle("Sair")
                        .setMessage("Vai deseja sair?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }else{
                finish();
            }
        }

        return  super.onOptionsItemSelected(item);
    }
}
