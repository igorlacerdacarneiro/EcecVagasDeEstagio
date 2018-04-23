package com.example.igorl.ececvagasdeestagio.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Helper.Base64Custom;
import com.example.igorl.ececvagasdeestagio.Helper.Preferencias;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.Utils.CommonActivity;
import com.example.igorl.ececvagasdeestagio.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class Perfil extends CommonActivity {

    private EditText editNome;
    private EditText editMatricula;
    private EditText editEmail;
    private EditText editSenha;
    private EditText editConfirmaSenha;
    private Button botaoSalvar;
    private Button botaoEditar;
    private RadioGroup tipoCadastro;
    private RadioButton aluno;
    private RadioButton admin;
    private TextView tipo;
    private TextView confSenha;
    private Usuario usuario;

    Gson gson = new Gson();

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initViews();

        openProgressBar();

        Bundle b = getIntent().getExtras();
        if(b != null) {
            String result = b.getString("usuario");
            Usuario usuario = gson.fromJson(result, Usuario.class);

            if(usuario.getTipo() == 1){
                aluno.setChecked(true);
            }else{
                admin.setChecked(true);
            }
            editNome.setText(usuario.getNome());
            editMatricula.setText(usuario.getMatricula());
            editEmail.setText(usuario.getEmail());
            editSenha.setText(usuario.getSenha());
            editConfirmaSenha.setText(usuario.getSenha());
        }

        closeProgressBar();

        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editNome.getText().toString().trim().equals("") && !editSenha.getText().toString().trim().equals("") && !editConfirmaSenha.getText().toString().trim().equals("")){
                    if(editSenha.getText().toString().equals(editConfirmaSenha.getText().toString())){
                        openProgressBar();
                        initUser();
                        usuario.updateUserFBDatabase();
                        firebaseAuth.getCurrentUser().updatePassword(usuario.getSenha());
                        salvarDadosUsuario();
                    }else {
                        showDialogMessage("Senhas não correspondem.");
                    }
                }else{
                    showDialogMessage("Todos os campos são obrigatórios");
                }
            }
        });

        botaoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableButton(botaoEditar);
                enableButton(botaoSalvar);
                editConfirmaSenha.setVisibility(View.VISIBLE);
                enableEditText(editNome);
                enableEditText(editSenha);
                enableEditText(editConfirmaSenha);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        tipo.setVisibility(View.INVISIBLE);
        tipoCadastro.setVisibility(View.INVISIBLE);
        disableButton(botaoSalvar);
        enableButton(botaoEditar);
        disableEditText(editNome);
        disableEditText(editMatricula);
        disableEditText(editEmail);
        disableEditText(editSenha);
        confSenha.setVisibility(View.INVISIBLE);
        editConfirmaSenha.setVisibility(View.INVISIBLE);
    }


    protected void initViews(){
        editNome = (EditText) findViewById(R.id.textNomeUsuario);
        editMatricula = (EditText) findViewById(R.id.textMatriculaUsuario);
        editEmail = (EditText) findViewById(R.id.textEmailUsuario);
        editSenha = (EditText) findViewById(R.id.textSenhaUsuario);
        editConfirmaSenha = (EditText) findViewById(R.id.textConfirmarSenhaUsuario);
        botaoSalvar = (Button) findViewById(R.id.botaoSalvarUsuario);
        botaoEditar = (Button) findViewById(R.id.botaoEditarUsuario);
        tipoCadastro = (RadioGroup) findViewById(R.id.radioButtonUsuariosTipo);
        aluno = (RadioButton) findViewById(R.id.radioUsuarioTipoAluno);
        admin = (RadioButton) findViewById(R.id.radioUsuarioTipoAdmin);
        tipo = (TextView) findViewById(R.id.textViewTipoUsuario);
        progressBar = (ProgressBar) findViewById(R.id.usuario_progress);
        confSenha = (TextView) findViewById(R.id.textView27);
    }

    protected void initUser(){
        usuario = new Usuario();
        usuario.setId(firebaseAuth.getCurrentUser().getUid());
        usuario.setNome(editNome.getText().toString());
        usuario.setMatricula(editMatricula.getText().toString());
        usuario.setEmail(editEmail.getText().toString());
        usuario.setSenha(editSenha.getText().toString());
        if(aluno.isChecked()){
            usuario.setTipo(1);
        }else{
            usuario.setTipo(2);
        }
    }

    public void salvarDadosUsuario(){
        Intent intent = new Intent(Perfil.this, Principal.class);
        startActivity(intent);
        closeProgressBar();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            if(botaoSalvar.isEnabled()) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(Perfil.this);
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
