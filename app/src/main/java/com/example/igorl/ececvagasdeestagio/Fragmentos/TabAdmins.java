package com.example.igorl.ececvagasdeestagio.Fragmentos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.example.igorl.ececvagasdeestagio.Adapters.UserAdapter;
import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.Utils.RecyclerTouchListener;
import com.example.igorl.ececvagasdeestagio.Views.EditarUsuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TabAdmins extends Fragment {

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
        View view = inflater.inflate(R.layout.activity_tab_admins, container,false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_tab_admins);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewUsuariosAdmins);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mListUsers = new ArrayList<>();

        mUserAdapter = new UserAdapter(getActivity(), mListUsers);

        mRecyclerView.setAdapter(mUserAdapter);

        mFirebaseDatabase = ConfiguracaoFirebase.getFirebase().child("usuarios").child("administradores");
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
                mUsuario = mListUsers.get(position);
                Intent intent = new Intent(getActivity(), EditarUsuario.class);
                intent.putExtra("usuario",gson.toJson(mUsuario));
                startActivity(intent);
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
                        removeUserToAutentication(mUsuario);
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

        return view;
    }

    private void removeUserToAutentication(Usuario user){
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:655248022019:android:ede0ecba58f106ad")
                .setApiKey("AIzaSyB94IF2jwcG9IDlJELHZC2gz8KVrV4LxkI")
                .setDatabaseUrl("https://ececvagasdeestagio.firebaseio.com/")
                .build();

        FirebaseApp app;

        try{
            FirebaseApp.initializeApp(getContext(),options,"Secundary");
            app = FirebaseApp.getInstance("Secundary");
        }catch (IllegalStateException e){
            app = FirebaseApp.getInstance("Secundary");
        }

        final FirebaseAuth firebaseAuth2 = FirebaseAuth.getInstance(app);
        firebaseAuth2.signInWithEmailAndPassword(
                user.getEmail(),
                user.getSenha()
        ).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseAuth2.getCurrentUser().delete();
                    firebaseAuth2.signOut();
                }else{
                    Toast.makeText(getActivity(), "Erro ao excluir usuário", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
