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
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.Adapters.VagaAdapter;
import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.Models.Vaga;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.Views.CadastroVaga;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class TabEncerradas extends Fragment {

    private ListView listViewVagasEnc; //listView
    private ArrayAdapter<Vaga> adapterVagas; //adapter
    private ArrayList<Vaga> listVagas; //produtos
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerVagas;
    private AlertDialog alerta;
    private Vaga vagaExcluir;

    Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.activity_tab_encerradas, container,false);

        listVagas = new ArrayList<>();

        listViewVagasEnc = (ListView) view.findViewById(R.id.listViewVagaEncerradas);

        adapterVagas = new VagaAdapter(getActivity(), listVagas);

        listViewVagasEnc.setAdapter(adapterVagas);

        firebase = ConfiguracaoFirebase.getFirebase().child("vagas").child("encerradas");
        valueEventListenerVagas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listVagas.clear();
                for(DataSnapshot dados : dataSnapshot.getChildren()){

                    Vaga vagaNova = dados.getValue(Vaga.class);

                    listVagas.add(vagaNova);
                }

                adapterVagas.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        listViewVagasEnc.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                vagaExcluir = adapterVagas.getItem(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirma Exclusão?");
                builder.setMessage("Você deseja excluir: " + vagaExcluir.getTitulo() + " ?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebase = ConfiguracaoFirebase.getFirebase().child("vagas").child("encerradas");
                        firebase.child(String.valueOf(vagaExcluir.getCodigo())).removeValue();
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

        listViewVagasEnc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                vagaExcluir = adapterVagas.getItem(i);

                Intent intent = new Intent(getActivity(), CadastroVaga.class);

                intent.putExtra("vaga",gson.toJson(vagaExcluir));
                startActivityForResult(intent, 2);

            }
        });

        return view;

    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerVagas);
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerVagas);
    }
}
