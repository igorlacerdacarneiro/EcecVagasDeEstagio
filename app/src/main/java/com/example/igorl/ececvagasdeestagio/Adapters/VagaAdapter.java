package com.example.igorl.ececvagasdeestagio.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Vaga;
import com.example.igorl.ececvagasdeestagio.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by igorl on 25/04/2018.
 */

public class VagaAdapter extends RecyclerView.Adapter<VagaAdapter.VagaViewHolder> {
    private Context mContext;
    private List<Vaga> mListVagas;

    public VagaAdapter(Context context, List<Vaga> listVagas) {
        this.mContext = context;
        this.mListVagas = listVagas;
    }

    @Override
    public VagaAdapter.VagaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_linha_vagas, parent, false);
        VagaViewHolder holder = new VagaAdapter.VagaViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(VagaAdapter.VagaViewHolder holder, int position) {
        Vaga vagasCurrent = mListVagas.get(position);
        Picasso.get()
                .load(vagasCurrent.getImagem())
                .fit()
                .centerCrop()
                .into(holder.imageDivulgacao);
        holder.textTitulo.setText(vagasCurrent.getTitulo());
        holder.textLocal.setText(vagasCurrent.getLocal());
        holder.textEmpresa.setText(vagasCurrent.getEmpresa());
        holder.textHorario.setText(vagasCurrent.getHorario());
    }

    @Override
    public int getItemCount() {
        return mListVagas.size();
    }


    public class VagaViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageDivulgacao;
        public TextView textTitulo;
        public TextView textLocal;
        public TextView textEmpresa;
        public TextView textHorario;

        public VagaViewHolder(View itemView) {
            super(itemView);
            imageDivulgacao = itemView.findViewById(R.id.imagemViewDivulgAdminVagas);
            textTitulo = itemView.findViewById(R.id.textViewTituloVaga);
            textLocal = itemView.findViewById(R.id.textViewLocalVaga);
            textEmpresa = itemView.findViewById(R.id.textViewEmpresaVaga);
            textHorario = itemView.findViewById(R.id.textViewHorarioVaga);
        }

    }

}