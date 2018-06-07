package com.example.igorl.ececvagasdeestagio.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.igorl.ececvagasdeestagio.Adapters.VancancyAdapter;
import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.Models.Vaga;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.Utils.AESCrypt;
import com.example.igorl.ececvagasdeestagio.Utils.RecyclerTouchListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Principal extends AppCompatActivity implements DialogChangePassword.DialogChangePasswordListener{

    private Toolbar mToobar;
    private ProgressBar mProgressBar;
    private Vaga mVaga;
    private Usuario mUsers;
    private RecyclerView mRecyclerView;
    private VancancyAdapter mVagaAdapter;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private List<Vaga> mListVagas;
    private TextView textTextoVazio;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setSharedElementExitTransition(new ChangeBounds());
            getWindow().setSharedElementEnterTransition(new ChangeBounds());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        mFirebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        mFirebaseDatabase = ConfiguracaoFirebase.getFirebase()
                .child("usuarios")
                .child("administradores")
                .child(mFirebaseAuth.getCurrentUser().getUid());
        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers = dataSnapshot.getValue(Usuario.class);
                if(mUsers.isChangePassword()){
                    openDialogChangePassword();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Principal.this, "Erro ao Recuperar Usuário", Toast.LENGTH_LONG).show();
                logoutUserApp();
            }
        });

        mToobar = (Toolbar) findViewById(R.id.toolbar_principal);
        mToobar.setTitle(R.string.app_name);
        mToobar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToobar);

        textTextoVazio = (TextView) findViewById(R.id.textViewTextoVazio) ;

        mProgressBar = (ProgressBar) findViewById(R.id.progress_principal_load);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewAllVagas);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mListVagas = new ArrayList<>();

        mVagaAdapter = new VancancyAdapter(Principal.this, mListVagas);

        mRecyclerView.setAdapter(mVagaAdapter);

        mFirebaseDatabase = ConfiguracaoFirebase.getFirebase()
                .child("vagas")
                .child("disponiveis");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListVagas.removeAll(mListVagas);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Vaga vaga = snapshot.getValue(Vaga.class);
                    mListVagas.add(vaga);
                }

                if(mListVagas.isEmpty()){
                    textTextoVazio.setVisibility(View.VISIBLE);
                }

                mVagaAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(Principal.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mVaga = mListVagas.get(position);
                Intent intent = new Intent(Principal.this, VagaActivity.class);
                intent.putExtra("vaga",gson.toJson(mVaga));

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                    View imagem = view.findViewById(R.id.imagemViewDivulgacaoAllVaga);
                    View titulo = view.findViewById(R.id.textViewTituloAllVaga);
                    View empresa = view.findViewById(R.id.textViewEmpresaAllVaga);
                    View local = view.findViewById(R.id.textViewLocalAllVaga);
                    View horario = view.findViewById(R.id.textViewHorarioAllVaga);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Principal.this,
                            Pair.create(imagem, "element1"),
                            Pair.create(titulo, "element2"),
                            Pair.create(local, "element3"),
                            Pair.create(empresa, "element4"),
                            Pair.create(horario, "element5"));
                    startActivity(intent, options.toBundle());
                }else{
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void openDialogChangePassword() {
        DialogChangePassword dialogChangePassword = new DialogChangePassword();
        dialogChangePassword.show(getSupportFragmentManager(), "dialog");
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
            case R.id.action_usuarios_solicitados:

                Intent intent_solicitados = new Intent(Principal.this, UsuariosSolicitados.class);
                startActivity(intent_solicitados);

                break;
            case R.id.action_perfil:

                Intent intent_perfil = new Intent(Principal.this, Perfil.class);
                intent_perfil.putExtra("usuario",gson.toJson(mUsers));
                startActivity(intent_perfil);

                break;
            case R.id.action_sair:

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(Principal.this);
                builder.setTitle("Sair")
                        .setMessage("Você deseja sair?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                logoutUserApp();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUserApp(){
        mFirebaseAuth.signOut();
        Intent intent_sair = new Intent(Principal.this, Login.class);
        startActivity(intent_sair);
        finish();
    }

    @Override
    public void returnPassword(String password) {
        try{
            mFirebaseAuth.getCurrentUser().updatePassword(password);
            mUsers.updateUserFBDatabaseChangePassword(AESCrypt.encrypt(password), false);
            mUsers.setSenha(AESCrypt.encrypt(password));
            Toast.makeText(Principal.this, "Senha atualizada com sucesso", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
