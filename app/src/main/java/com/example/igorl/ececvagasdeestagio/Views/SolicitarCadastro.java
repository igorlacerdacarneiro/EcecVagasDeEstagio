package com.example.igorl.ececvagasdeestagio.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.Utils.CommonActivity;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class SolicitarCadastro extends CommonActivity implements DatabaseReference.CompletionListener {

    private EditText editNome;
    private EditText editMatricula;
    private EditText editEmail;
    private EditText editSenha;
    private EditText editConfirmaSenha;
    private Button solicitarCadastro;
    private RadioGroup tipoCadastro;
    private RadioButton aluno;
    private Usuario usuario;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_cadastro);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();

        initViews();

        tipoCadastro.setVisibility(View.INVISIBLE);

        SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter("NNNN.N.NNNN.NNNN-N");
        MaskTextWatcher maskTextWatcher = new MaskTextWatcher(editMatricula, simpleMaskFormatter);
        editMatricula.addTextChangedListener(maskTextWatcher);

        solicitarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editNome.getText().toString().trim().equals("") && !editMatricula.getText().toString().trim().equals("") &&
                        !editEmail.getText().toString().trim().equals("") && !editSenha.getText().toString().trim().equals("") &&
                        !editConfirmaSenha.getText().toString().trim().equals("")){
                    if(editSenha.getText().toString().equals(editConfirmaSenha.getText().toString())){
                        if(isEmailValid(editEmail.getText().toString())){
                            openProgressBar();
                            initUser();
                            cadastrarUsuarioFirebase();
                        }else{
                            Toast.makeText(SolicitarCadastro.this, "E-mail digitado não é e-mail", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(SolicitarCadastro.this, "Senhas não correspondem", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(SolicitarCadastro.this, "Todos os campos são obrigatórios", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void initViews(){
        editNome = (EditText) findViewById(R.id.editTextNome);
        editMatricula = (EditText) findViewById(R.id.editTextMatricula);
        editEmail = (EditText) findViewById(R.id.editTextEmail);
        editSenha = (EditText) findViewById(R.id.editTextSenha);
        editConfirmaSenha = (EditText) findViewById(R.id.editTextConfirmarSenha);
        solicitarCadastro = (Button) findViewById(R.id.botaoSolicitar);
        tipoCadastro = (RadioGroup) findViewById(R.id.radioButtonCadastro);
        aluno = (RadioButton) findViewById(R.id.radioCadastroAluno);
        progressBar = (ProgressBar) findViewById(R.id.solicitar_cadastro_progress);
    }

    protected void initUser(){
        usuario = new Usuario();
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

    private void cadastrarUsuarioFirebase(){
        firebaseAuth.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(SolicitarCadastro.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    usuario.setId(task.getResult().getUser().getUid());
                    usuario.salvarUserFBDatabase(SolicitarCadastro.this);

                }else {
                    String erroExcessao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erroExcessao = "Senha fraca, digite uma mais forte";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcessao = "E-mail inválido";
                    }catch (FirebaseAuthUserCollisionException e){
                        erroExcessao = "E-mail já cadastrado";
                    }catch (Exception e){
                        erroExcessao = "Erro ao efetuar cadastro";
                        e.printStackTrace();
                    }
                    Toast.makeText(SolicitarCadastro.this, erroExcessao, Toast.LENGTH_LONG).show();
                    closeProgressBar();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            if(!editNome.getText().toString().trim().equals("") || !editMatricula.getText().toString().trim().equals("") ||
                    !editEmail.getText().toString().trim().equals("")) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(SolicitarCadastro.this);
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

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        firebaseAuth.signOut();
        Toast.makeText(SolicitarCadastro.this, "Usuário Cadastro com Sucesso", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SolicitarCadastro.this, Login.class);
        startActivity(intent);
        closeProgressBar();
        finish();
    }
}
