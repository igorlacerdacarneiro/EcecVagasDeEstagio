package com.example.igorl.ececvagasdeestagio.Views;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.Utils.AESCrypt;
import com.example.igorl.ececvagasdeestagio.Utils.CommonActivity;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class CadastroUsuario extends CommonActivity implements DatabaseReference.CompletionListener{

    private EditText editNome;
    private EditText editMatricula;
    private EditText editEmail;
    private EditText editSenha;
    private EditText editConfirmaSenha;
    private Button botaoSalvar;
    private RadioGroup tipoCadastro;
    private RadioButton aluno;
    private RadioButton admin;
    private TextView confSenha;
    private Usuario mUsuario;
    private Toolbar mToobar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        dialog = new ProgressDialog(CadastroUsuario.this);

        mToobar = (Toolbar) findViewById(R.id.toolbar_cadastro_usuario);
        mToobar.setTitle(R.string.tela_cadastro_usuario);
        mToobar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();

        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editNome.getText().toString().trim().equals("") && !editMatricula.getText().toString().trim().equals("") &&
                        !editEmail.getText().toString().trim().equals("") && !editSenha.getText().toString().trim().equals("") &&
                        !editConfirmaSenha.getText().toString().trim().equals("")){
                    if(editSenha.getText().toString().equals(editConfirmaSenha.getText().toString())){
                        if(editSenha.getText().toString().length() >= 6){
                            if(isEmailValid(editEmail.getText().toString())){

                                openDialog("Aguarde...");
                                initUser();
                                createUserFBAutentication();

                            }else{
                                showDialogMessage("E-mail digitado não é um e-mail valido.");
                            }
                        }else{
                            showDialogMessage("Senha deve ter no minimo 6 caracteres");
                        }
                    }else {
                        showDialogMessage("Senhas não correspondem.");
                    }
                }else{
                    showDialogMessage("Todos os campos são obrigatórios.");
                }
            }
        });

        editMatricula.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i == 13){
                    SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter("NNNN.N.NNNN.NNNN-N");
                    MaskTextWatcher maskTextWatcher = new MaskTextWatcher(editMatricula, simpleMaskFormatter);
                    editMatricula.addTextChangedListener(maskTextWatcher);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    protected void initViews(){
        editNome = (EditText) findViewById(R.id.textNomeUsuario);
        editMatricula = (EditText) findViewById(R.id.textMatriculaUsuario);
        editEmail = (EditText) findViewById(R.id.textEmailUsuario);
        editSenha = (EditText) findViewById(R.id.textSenhaUsuario);
        editConfirmaSenha = (EditText) findViewById(R.id.textConfirmarSenhaUsuario);
        botaoSalvar = (Button) findViewById(R.id.botaoSalvarUsuario);
        tipoCadastro = (RadioGroup) findViewById(R.id.radioButtonUsuariosTipo);
        aluno = (RadioButton) findViewById(R.id.radioUsuarioTipoAluno);
        admin = (RadioButton) findViewById(R.id.radioUsuarioTipoAdmin);
        confSenha = (TextView) findViewById(R.id.textView27);
    }

    protected void initUser(){
        mUsuario = new Usuario();
        mUsuario.setNome(editNome.getText().toString());
        mUsuario.setMatricula(editMatricula.getText().toString());
        mUsuario.setEmail(editEmail.getText().toString());
        mUsuario.setSenha(editSenha.getText().toString());
        if(aluno.isChecked()){
            mUsuario.setTipo(1);
        }else{
            mUsuario.setTipo(2);
        }
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

        final FirebaseAuth firebaseAuth2 = FirebaseAuth.getInstance(app);
        firebaseAuth2.createUserWithEmailAndPassword(
                mUsuario.getEmail(),
                mUsuario.getSenha()
        ).addOnCompleteListener(CadastroUsuario.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    try {
                        mUsuario.setId(task.getResult().getUser().getUid());
                        mUsuario.setSenha(AESCrypt.encrypt(mUsuario.getSenha()));
                        mUsuario.salvarUserFBDatabase(CadastroUsuario.this);
                        firebaseAuth2.signOut();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroUsuario.this, "Usuário Criado com Sucesso", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(CadastroUsuario.this, "Erro ao criar usuário", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            if(!editNome.getText().toString().equals("") || !editSenha.getText().toString().equals("") ||
                    !editMatricula.getText().toString().equals("") || !editEmail.getText().toString().equals("") ||
                    !editConfirmaSenha.getText().toString().equals("")) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(CadastroUsuario.this);
                builder.setTitle("Sair")
                        .setMessage("Deseja sair sem salvar as alterações?")
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

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        Intent intent = new Intent(CadastroUsuario.this, AdministracaoUsuarios.class);
        startActivity(intent);
        closeDialog();
        finish();
    }
}
