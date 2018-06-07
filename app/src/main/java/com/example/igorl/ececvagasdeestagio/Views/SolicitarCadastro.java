package com.example.igorl.ececvagasdeestagio.Views;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.Utils.AESCrypt;
import com.example.igorl.ececvagasdeestagio.Utils.CommonActivity;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.auth.FirebaseAuth;
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
    private DatabaseReference mFirebaseDatabase;
    public String msg = "";

    private Toolbar mToobar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_cadastro);

        dialog = new ProgressDialog(SolicitarCadastro.this);

        mToobar = (Toolbar) findViewById(R.id.toolbar_solicitar_cadastro);
        mToobar.setTitle(R.string.cadastar);
        mToobar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();

        initViews();

        tipoCadastro.setVisibility(View.INVISIBLE);

        solicitarCadastro.setOnClickListener(new View.OnClickListener() {
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
                                usuario.setId(createIdByMatricula(usuario.getMatricula()));
                                usuario.salvarUserSolicitadoFBDatabase(SolicitarCadastro.this);

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
        editNome = (EditText) findViewById(R.id.editTextNome);
        editMatricula = (EditText) findViewById(R.id.editTextMatricula);
        editEmail = (EditText) findViewById(R.id.editTextEmail);
        editSenha = (EditText) findViewById(R.id.editTextSenha);
        editConfirmaSenha = (EditText) findViewById(R.id.editTextConfirmarSenha);
        solicitarCadastro = (Button) findViewById(R.id.botaoSolicitar);
        tipoCadastro = (RadioGroup) findViewById(R.id.radioButtonCadastro);
        aluno = (RadioButton) findViewById(R.id.radioCadastroAluno);
    }

    protected void initUser(){
        try {
            usuario = new Usuario();
            usuario.setNome(editNome.getText().toString());
            usuario.setMatricula(editMatricula.getText().toString());
            usuario.setEmail(editEmail.getText().toString());
            usuario.setSenha(AESCrypt.encrypt(editSenha.getText().toString()));
            if(aluno.isChecked()){
                usuario.setTipo(1);
            }else{
                usuario.setTipo(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createIdByMatricula(String matricula){
        String c;
        try{
            String[] a = matricula.split("\\.");
            String[] b = a[3].split("-");
            c = a[0]+a[1]+a[2]+b[0]+b[1];
        }catch (Exception e) {
            c = matricula;
        }

        return c;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            if(!editNome.getText().toString().trim().equals("") || !editMatricula.getText().toString().trim().equals("") ||
                    !editEmail.getText().toString().trim().equals("")) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(SolicitarCadastro.this);
                builder.setMessage("Deseja descartar as alterações?")
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
        Intent intent = new Intent(SolicitarCadastro.this, Login.class);
        startActivity(intent);
        Toast.makeText(SolicitarCadastro.this, "Solicitação enviada com sucesso", Toast.LENGTH_LONG).show();
        closeDialog();
        finish();
    }
}
