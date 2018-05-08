package com.example.igorl.ececvagasdeestagio.Views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Utils.CommonActivity;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class Login extends CommonActivity {

    private EditText email;
    private EditText senha;
    private Button botaoEntrar;
    private Button botaoCadastrar;
    private Usuario usuario;
    private TextView esqueciSenha;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private Usuario mUsers;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();

        initViews();

        verificarUserLogado();

        botaoEntrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v){
                if(!email.getText().toString().equals("") && !senha.getText().toString().equals("")){
                    openProgressBar();
                    initUser();
                    login();
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

    private void login(){

        mFirebaseAuth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    readData(new MyCallback() {
                        @Override
                        public void onCallback(Usuario usuario) {

                            Log.i("log", "LOG usuario 1 === "+usuario.getId());
                            Log.i("log", "LOG usuario 2 === "+usuario.getTipo());
                            Log.i("log", "LOG usuario 3 === "+usuario.getNome());
                            Log.i("log", "LOG usuario 4 === "+usuario.getEmail());
                            Log.i("log", "LOG usuario 5 === "+usuario.getSenha());
                            Log.i("log", "LOG usuario 6 === "+usuario.getMatricula());

                            if(usuario.isUsuarioAdministrador()){
                                Toast.makeText(Login.this, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show();
                                abrirTelaPrincipalAdmin();
                            }else{
                                Toast.makeText(Login.this, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show();
                                abrirTelaPrincipalAluno();
                            }
                        }
                    });
                }else{
                    String erroExcessao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcessao = "Senha incorreta.";
                    }catch (FirebaseAuthInvalidUserException e){
                        erroExcessao = "E-mail incorreto ou não cadastrado.";
                    }catch (Exception e){
                        erroExcessao = "ERRO 500";
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

    private void abrirTelaPrincipalAdmin(){
        Intent intent = new Intent(Login.this, Principal.class);
        startActivity(intent);
        closeProgressBar();
        finish();
    }

    private void abrirTelaPrincipalAluno(){
        Intent intent = new Intent(Login.this, PrincipalAluno.class);
        startActivity(intent);
        closeProgressBar();
        finish();
    }

    private void verificarUserLogado(){
        if(mFirebaseAuth.getCurrentUser() != null){
            openProgressBar();
            readData(new MyCallback() {
                @Override
                public void onCallback(Usuario usuario) {

                    Log.i("log", "LOG usuario 1 === "+usuario.getId());
                    Log.i("log", "LOG usuario 2 === "+usuario.getTipo());
                    Log.i("log", "LOG usuario 3 === "+usuario.getNome());
                    Log.i("log", "LOG usuario 4 === "+usuario.getEmail());
                    Log.i("log", "LOG usuario 5 === "+usuario.getSenha());
                    Log.i("log", "LOG usuario 6 === "+usuario.getMatricula());

                    if(usuario.isUsuarioAdministrador()){
                        Toast.makeText(Login.this, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show();
                        abrirTelaPrincipalAdmin();
                    }else{
                        Toast.makeText(Login.this, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show();
                        abrirTelaPrincipalAluno();
                    }
                }
            });
        }
    }

    private void readData(final MyCallback myCallback){
        mFirebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        mFirebaseDatabase = ConfiguracaoFirebase.getFirebase()
                .child("usuarios")
                .child("alunos")
                .child(mFirebaseAuth.getCurrentUser().getUid());
        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){

                    mFirebaseDatabase = ConfiguracaoFirebase.getFirebase()
                            .child("usuarios")
                            .child("administradores")
                            .child(mFirebaseAuth.getCurrentUser().getUid());
                    mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mUsers = dataSnapshot.getValue(Usuario.class);
                            myCallback.onCallback(mUsers);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(Login.this, "Erro ao Recuperar Usuário", Toast.LENGTH_LONG).show();
                        }
                    });

                }else{
                    mUsers = dataSnapshot.getValue(Usuario.class);
                    myCallback.onCallback(mUsers);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Login.this, "Erro ao Recuperar Usuário", Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface MyCallback {
        void onCallback(Usuario usuario);
    }
}
