package com.example.igorl.ececvagasdeestagio.Views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Helper.Base64Custom;
import com.example.igorl.ececvagasdeestagio.Helper.Preferencias;
import com.example.igorl.ececvagasdeestagio.Utils.CommonActivity;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends CommonActivity {

    private EditText email;
    private EditText senha;
    private Button botaoEntrar;
    private Button botaoCadastrar;
    private Usuario usuario;
    private TextView esqueciSenha;

    private FirebaseDatabase firebase;
    private FirebaseAuth autenticacao;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        initViews();

        verificarUserLogado();

        botaoEntrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v){
                if(!email.getText().toString().equals("") && !senha.getText().toString().equals("")){
                    if(isEmailValid(email.getText().toString())){
                        openProgressBar();
                        initUser();
                        validarLogin();
                    }else{
                        Toast.makeText(Login.this, "E-mail digitado não é e-mail", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Login.this, "Preencha os campos de E-mail e Senha", Toast.LENGTH_SHORT).show();
                }
            }
        });

        botaoCadastrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v){
                Intent intent = new Intent(Login.this, SolicitarCadastro.class);
                startActivity(intent);
            }
        });

        esqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, RecuperarAcesso.class);
                startActivity(intent);
            }
        });
    }

    private void validarLogin(){
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    abrirTelaPrincipal();
                    Toast.makeText(Login.this, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show();
                }else{
                    String erroExcessao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcessao = "Senha incorreta.";
                    }catch (FirebaseAuthInvalidUserException e){
                        erroExcessao = "E-mail incorreto ou não cadastrado.";
                    }catch (Exception e){
                        erroExcessao = "ERRO";
                        e.printStackTrace();
                    }
                    Toast.makeText(Login.this, erroExcessao, Toast.LENGTH_SHORT).show();
                    closeProgressBar();
                }
            }
        });
    }

    protected void initViews(){
        email = (EditText) findViewById(R.id.campoEmail);
        senha = (EditText) findViewById(R.id.campoSenha);
        botaoEntrar = (Button) findViewById(R.id.botaoLogin);
        botaoCadastrar = (Button) findViewById(R.id.botaoCadastrar);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        esqueciSenha = (TextView) findViewById(R.id.textViewRecuperarSenha);
    }

    protected void initUser(){
        usuario = new Usuario();
        usuario.setEmail(email.getText().toString());
        usuario.setSenha(senha.getText().toString());
    }

    private void abrirTelaPrincipal(){
        Intent intent = new Intent(Login.this, Principal.class);
        startActivity(intent);
        closeProgressBar();
        finish();
    }

    private void verificarUserLogado(){
        if(autenticacao.getCurrentUser() != null){
            Intent intent = new Intent(Login.this, Principal.class);
            startActivity(intent);
            finish();
        }
    }
}
