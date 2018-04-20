package com.example.igorl.ececvagasdeestagio.Fragmentos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.Adapters.UsuarioAdapter;
import com.example.igorl.ececvagasdeestagio.Adapters.VagaAdapter;
import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.Models.Vaga;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.Views.CadastroUsuario;
import com.example.igorl.ececvagasdeestagio.Views.CadastroVaga;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class TabUsuarios extends Fragment {

    private ListView listViewUsuarioAluno; //listView
    private ArrayAdapter<Usuario> adapterUsuarios; //adapter
    private ArrayList<Usuario> listUsuarios; //produtos
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerUsuarios;
    private AlertDialog alerta;
    private Usuario usuarioExcluir;

    Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.activity_tab_usuarios, container,false);

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
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerUsuarios);
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerUsuarios);
    }

}