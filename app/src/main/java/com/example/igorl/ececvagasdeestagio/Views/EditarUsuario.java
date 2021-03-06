package com.example.igorl.ececvagasdeestagio.Views;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.Utils.AESCrypt;
import com.example.igorl.ececvagasdeestagio.Utils.CommonActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

public class EditarUsuario extends CommonActivity {

    private Toolbar mToobar;
    private EditText editNome;
    private EditText editMatricula;
    private EditText editEmail;
    private EditText editSenha;
    private EditText editConfirmaSenha;
    private Button botaoSalvar;
    private ImageButton botaoEditar;
    private RadioGroup tipoCadastro;
    private RadioButton aluno;
    private RadioButton admin;
    private TextView confSenha;
    private Usuario usuario;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mFirebaseDatabase;
    private TextView textId;
    private Boolean mudouTipoCadastro = false;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        dialog = new ProgressDialog(EditarUsuario.this);

        mToobar = (Toolbar) findViewById(R.id.toolbar_editar_usuario);
        mToobar.setTitle(R.string.tela_editar_usuario);
        mToobar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        openDialog("Carregando...");

        mFirebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();

        initViews();

        Bundle b = getIntent().getExtras();
        if(b != null) {
            modoEditar();

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
            try {
                editSenha.setText(AESCrypt.decrypt(usuario.getSenha()));
                editConfirmaSenha.setText(AESCrypt.decrypt(usuario.getSenha()));
            } catch (Exception e) {
                editSenha.setText(usuario.getSenha());
                editConfirmaSenha.setText(usuario.getSenha());
            }
            textId.setText(usuario.getId());
            closeDialog();
        }else{
            Toast.makeText(EditarUsuario.this, "Erro ao recuperar dados do usuário", Toast.LENGTH_LONG).show();
            voltarTelaAdministracao();
        }

        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editNome.getText().toString().trim().equals("") && !editSenha.getText().toString().trim().equals("") &&
                        !editConfirmaSenha.getText().toString().trim().equals(""))
                {
                    openDialog("Aguarde...");
                    initUser();
                    try{
                        usuario.setSenha(AESCrypt.encrypt(usuario.getSenha()));
                        if(mudouTipoCadastro){

                            if(usuario.getTipo() == 1 ){
                                mFirebaseDatabase = ConfiguracaoFirebase.getFirebase().child("usuarios").child("administradores");
                                mFirebaseDatabase.child(usuario.getId()).removeValue();
                            }else{
                                mFirebaseDatabase = ConfiguracaoFirebase.getFirebase().child("usuarios").child("alunos");
                                mFirebaseDatabase.child(usuario.getId()).removeValue();
                            }
                            usuario.salvarUserFBDatabase();

                        }else{
                            usuario.updateUserFBDatabaseEditar();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(EditarUsuario.this, "Usuário editado com sucesso", Toast.LENGTH_SHORT).show();
                    voltarTelaAdministracao();
                }else{
                    showDialogMessage("Todos os campos são obrigatórios");
                }
            }
        });

        botaoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botaoEditar.setVisibility(View.INVISIBLE);
                enableButton(botaoSalvar);
                editConfirmaSenha.setVisibility(View.VISIBLE);
                enableEditText(editNome);
                enableEditText(editMatricula);
                aluno.setClickable(true);
                admin.setClickable(true);
            }
        });

        tipoCadastro.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mudouTipoCadastro = true;
            }
        });
    }

    private void modoEditar() {
        disableEditText(editNome);
        disableEditText(editMatricula);
        disableEditText(editEmail);
        disableEditText(editSenha);
        disableEditText(editConfirmaSenha);
        disableButton(botaoSalvar);
        aluno.setClickable(false);
        admin.setClickable(false);
        editConfirmaSenha.setVisibility(View.INVISIBLE);
        confSenha.setVisibility(View.INVISIBLE);
    }

    protected void initViews(){
        editNome = (EditText) findViewById(R.id.textNomeUsuario);
        editMatricula = (EditText) findViewById(R.id.textMatriculaUsuario);
        editEmail = (EditText) findViewById(R.id.textEmailUsuario);
        editSenha = (EditText) findViewById(R.id.textSenhaUsuario);
        botaoEditar = (ImageButton) findViewById(R.id.imageButtonEditar);
        editConfirmaSenha = (EditText) findViewById(R.id.textConfirmarSenhaUsuario);
        botaoSalvar = (Button) findViewById(R.id.botaoSalvarUsuario);
        tipoCadastro = (RadioGroup) findViewById(R.id.radioButtonUsuariosTipo);
        aluno = (RadioButton) findViewById(R.id.radioUsuarioTipoAluno);
        admin = (RadioButton) findViewById(R.id.radioUsuarioTipoAdmin);
        confSenha = (TextView) findViewById(R.id.textView27);
        textId = (TextView) findViewById(R.id.textViewIdUsuario);
    }

    protected void initUser(){
        usuario = new Usuario();
        usuario.setId(textId.getText().toString());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            if(botaoSalvar.isEnabled()) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(EditarUsuario.this);
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

    public void voltarTelaAdministracao(){
        Intent intent = new Intent(EditarUsuario.this, AdministracaoUsuarios.class);
        startActivity(intent);
        closeDialog();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        modoEditar();
    }
}
