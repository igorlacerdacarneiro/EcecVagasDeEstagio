package com.example.igorl.ececvagasdeestagio.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.Models.Vaga;
import com.example.igorl.ececvagasdeestagio.R;

import java.util.ArrayList;

/**
 * Created by igorl on 20/03/2018.
 */

public class VagaAdapter extends ArrayAdapter<Vaga> {

    private final Context context;
    private final ArrayList<Vaga> vagas;

    public VagaAdapter(Context context, ArrayList<Vaga> vagas){
        super(context, R.layout.activity_linha_vagas, vagas);
        this.context = context;
        this.vagas = vagas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View view = null;

        if(vagas != null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_linha_vagas, parent, false);

            TextView status = (TextView) view.findViewById(R.id.textViewStatusVaga);
            TextView titulo = (TextView) view.findViewById(R.id.textViewTituloVaga);
            TextView local = (TextView) view.findViewById(R.id.textViewLocalVaga);
            TextView empresa = (TextView) view.findViewById(R.id.textViewEmpresaVaga);
            TextView horario = (TextView) view.findViewById(R.id.textViewHorarioVaga);

            Vaga vagas2 = vagas.get(position);
            if(vagas2.getStatus() == 1){
                status.setText("Disponível");
                titulo.setText(vagas2.getTitulo());
                local.setText(vagas2.getLocal());
                empresa.setText(vagas2.getEmpresa());
                horario.setText(vagas2.getHorario());
            }else{
                status.setText("Encerrada");
                titulo.setText(vagas2.getTitulo());
                local.setText(vagas2.getLocal());
                empresa.setText(vagas2.getEmpresa());
                horario.setText(vagas2.getHorario());
            }
        }

        return view;

    }
}
