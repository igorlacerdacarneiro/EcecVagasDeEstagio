package com.example.igorl.ececvagasdeestagio.Fragmentos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.Adapters.UserAdapter;
import com.example.igorl.ececvagasdeestagio.Adapters.UsuarioAdapter;
import com.example.igorl.ececvagasdeestagio.Adapters.VagaAdapter;
import com.example.igorl.ececvagasdeestagio.Adapters.VancancyAdapter;
import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.Models.Vaga;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.RecyclerTouchListener;
import com.example.igorl.ececvagasdeestagio.UsuariosSolicitados;
import com.example.igorl.ececvagasdeestagio.Views.CadastroUsuario;
import com.example.igorl.ececvagasdeestagio.Views.CadastroVaga;
import com.example.igorl.ececvagasdeestagio.Views.Principal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TabUsuarios extends Fragment {

    private ListView listViewUsuarioAluno; //listView
    private ArrayAdapter<Usuario> adapterUsuarios; //adapter
    private ArrayList<Usuario> listUsuarios; //produtos
    private ValueEventListener valueEventListenerUsuarios;

    private AlertDialog alerta;
    private Usuario mUsuario;

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;
    private DatabaseReference mFirebaseDatabase;
    private List<Usuario> mListUsers;

    Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.activity_tab_usuarios, container,false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_tab_usuario);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewUsuariosAlunos);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mListUsers = new ArrayList<>();

        mUserAdapter = new UserAdapter(getActivity(), mListUsers);

        mRecyclerView.setAdapter(mUserAdapter);

        mFirebaseDatabase = ConfiguracaoFirebase.getFirebase().child("usuarios").child("alunos");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListUsers.removeAll(mListUsers);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    mListUsers.add(usuario);
                }
                mUserAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                mUsuario = mListUsers.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirma Exclusão?");
                builder.setMessage("Você deseja excluir: " + mUsuario.getNome() + " ?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mFirebaseDatabase.child(mUsuario.getId().toString()).removeValue();
                        Toast.makeText(getActivity(), "Exclusão efetuada", Toast.LENGTH_LONG).show();
                    }
                });

                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Exclusão cancelada", Toast.LENGTH_LONG).show();
                    }
                });

                alerta = builder.create();
                alerta.show();
            }
        }));

        /*
        listUsuarios = new ArrayList<Usuario>();
        listViewUsuarioAluno = (ListView) view.findViewById(R.id.listViewUsuariosAlunos);
        adapterUsuarios = new UsuarioAdapter(getActivity(), listUsuarios);
        listViewUsuarioAluno.setAdapter(adapterUsuarios);
        firebase = ConfiguracaoFirebase.getFirebase().child("usuarios").child("alunos");
        valueEventListenerUsuarios = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listUsuarios.clear();
                for(DataSnapshot dados : dataSnapshot.getChildren()){

                    Usuario usuarioNovo = dados.getValue(Usuario.class);

                    listUsuarios.add(usuarioNovo);
                }

                adapterUsuarios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        listViewUsuarioAluno.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                usuarioExcluir = adapterUsuarios.getItem(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirma Exclusão?");
                builder.setMessage("Você deseja excluir: " + usuarioExcluir.getNome() + " ?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebase = ConfiguracaoFirebase.getFirebase().child("usuarios").child("alunos");
                        firebase.child(usuarioExcluir.getId().toString()).removeValue();
                        Toast.makeText(getActivity(), "Exclusão efetuada", Toast.LENGTH_LONG).show();
                    }
                });

                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Exclusão cancelada", Toast.LENGTH_LONG).show();
                    }
                });

                alerta = builder.create();
                alerta.show();
                return true;
            }
        });

        listViewUsuarioAluno.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                usuarioExcluir = adapterUsuarios.getItem(i);

                Intent intent = new Intent(getActivity(), CadastroUsuario.class);

                intent.putExtra("usuario",gson.toJson(usuarioExcluir));
                startActivityForResult(intent, 2);

            }
        });*/

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        //firebase.removeEventListener(valueEventListenerUsuarios);
    }

    @Override
    public void onStart() {
        super.onStart();
        //firebase.addValueEventListener(valueEventListenerUsuarios);
    }

}