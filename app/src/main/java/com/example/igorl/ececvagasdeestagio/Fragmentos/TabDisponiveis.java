package com.example.igorl.ececvagasdeestagio.Fragmentos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.Adapters.VagaAdapter;
import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Vaga;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.Utils.RecyclerTouchListener;
import com.example.igorl.ececvagasdeestagio.Views.CadastroVaga;
import com.example.igorl.ececvagasdeestagio.Views.EditarVaga;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TabDisponiveis extends Fragment {

    private AlertDialog alerta;
    private Vaga mVaga;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private VagaAdapter mVagaAdapter;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private List<Vaga> mListVagas;
    private ValueEventListener mDBListerner;

    Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.activity_tab_disponiveis, container,false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_tab_disponiveis);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewVagaDisponiveis);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mListVagas = new ArrayList<>();

        mVagaAdapter = new VagaAdapter(getActivity(), mListVagas);

        mRecyclerView.setAdapter(mVagaAdapter);

        mFirebaseStorage = FirebaseStorage.getInstance();

        mFirebaseDatabase = ConfiguracaoFirebase.getFirebase().child("vagas").child("disponiveis");
        mDBListerner = mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListVagas.removeAll(mListVagas);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Vaga vaga = snapshot.getValue(Vaga.class);
                    vaga.setKey(snapshot.getKey());
                    mListVagas.add(vaga);
                }
                mVagaAdapter.notifyDataSetChanged();
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

                mVaga = mListVagas.get(position);
                Intent intent = new Intent(getActivity(), EditarVaga.class);
                intent.putExtra("vaga",gson.toJson(mVaga));
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {
                mVaga = mListVagas.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirma Exclusão?");
                builder.setMessage("Você deseja excluir a vaga: " + mVaga.getCodigo() + " ?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StorageReference imageRef = mFirebaseStorage.getReferenceFromUrl(mVaga.getImagem());
                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFirebaseDatabase.child(mVaga.getCodigo()).removeValue();
                                Toast.makeText(getActivity(), "Exclusão efetuada", Toast.LENGTH_LONG).show();
                            }
                        });
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

        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFirebaseDatabase.removeEventListener(mDBListerner);
    }
}
