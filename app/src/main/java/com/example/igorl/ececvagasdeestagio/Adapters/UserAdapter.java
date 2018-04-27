package com.example.igorl.ececvagasdeestagio.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by igorl on 24/04/2018.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context mContext;
    private List<Usuario> mListUsuarios;

    public UserAdapter(Context context, List<Usuario> listUsuarios) {
        this.mContext = context;
        this.mListUsuarios = listUsuarios;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_linha_users, parent, false);
        UserViewHolder holder = new UserViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        Usuario usuario = mListUsuarios.get(position);
        holder.textMatricula.setText(String.valueOf(usuario.getMatricula()));
        holder.textSenha.setText(usuario.getSenha());
        holder.textNome.setText(usuario.getNome());
        holder.textEmail.setText(usuario.getEmail());
    }

    @Override
    public int getItemCount() {
        return mListUsuarios.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder{

        public TextView textMatricula;
        public TextView textSenha;
        public TextView textNome;
        public TextView textEmail;

        public UserViewHolder(View itemView) {
            super(itemView);
            textMatricula = itemView.findViewById(R.id.textViewMatriculaUser);
            textSenha = itemView.findViewById(R.id.textViewSenhaUser);
            textNome = itemView.findViewById(R.id.textViewNomeUser);
            textEmail = itemView.findViewById(R.id.textViewEmailUser);
        }

    }
}
