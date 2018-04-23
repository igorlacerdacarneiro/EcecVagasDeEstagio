package com.example.igorl.ececvagasdeestagio.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.Models.Vaga;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.Utils.CommonActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.w3c.dom.Text;

public class CadastroUsuario extends CommonActivity {

    private EditText editNome;
    private EditText editMatricula;
    private EditText editEmail;
    private EditText editSenha;
    private EditText editConfirmaSenha;
    private Button salvar;
    private Button editar;
    private RadioGroup tipoCadastro;
    private RadioButton aluno;
    private RadioButton admin;
    private Usuario usuario;
    private TextView confirmaSenha;
    private FirebaseAuth firebaseAuth;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();

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
            editSenha.setText(usuario.getSenha());
            editConfirmaSenha.setText(usuario.getSenha());
        }

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableEditText(editNome);
                enableEditText(editMatricula);
                enableEditText(editEmail);
                enableEditText(editSenha);
                disableButton(editar);
                enableButton(salvar);
                aluno.setClickable(true);
                admin.setClickable(true);
                editConfirmaSenha.setVisibility(View.VISIBLE);
                confirmaSenha.setVisibility(View.VISIBLE);
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
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
    }

    private void modoEditar() {
        disableEditText(editNome);
        disableEditText(editMatricula);
        disableEditText(editEmail);
        disableEditText(editSenha);
        disableButton(salvar);
        aluno.setClickable(false);
        admin.setClickable(false);
        editConfirmaSenha.setVisibility(View.INVISIBLE);
        confirmaSenha.setVisibility(View.INVISIBLE);
    }

    protected void initViews(){
        editNome = (EditText) findViewById(R.id.textNomeCadastroUser);
        editMatricula = (EditText) findViewById(R.id.textMatriculaCadastroUser);
        editEmail = (EditText) findViewById(R.id.textEmailCadastroUser);
        editSenha = (EditText) findViewById(R.id.textSenhaCadastroUser);
        editConfirmaSenha = (EditText) findViewById(R.id.textConfirmarSenhaCadastroUser);
        salvar = (Button) findViewById(R.id.botaoSalvarCadastroUser);
        editar = (Button) findViewById(R.id.botaoEditarCadastroUser);
        tipoCadastro = (RadioGroup) findViewById(R.id.radioButtonCadastroUserTipo);
        aluno = (RadioButton) findViewById(R.id.radioUsuarioCadastroUserTipoAluno);
        admin = (RadioButton) findViewById(R.id.radioUsuarioCadastroUserTipoAdmin);
        progressBar = (ProgressBar) findViewById(R.id.cadastro_usuario_progress);
        confirmaSenha = (TextView) findViewById(R.id.textView27);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            if(salvar.isEnabled()) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(CadastroUsuario.this);
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

    public void salvarDadosUsuario(){
        Intent intent = new Intent(CadastroUsuario.this, AdministracaoUsuarios.class);
        startActivity(intent);
        closeProgressBar();
        finish();
    }
}
