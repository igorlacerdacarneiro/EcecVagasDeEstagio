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

public class UsuarioAdapter extends ArrayAdapter<Usuario> {
    private final Context context;
    private final ArrayList<Usuario> users;

    public UsuarioAdapter(Context context, ArrayList<Usuario> users){
        super(context, R.layout.activity_linha_users, users);
        this.context = context;
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (users != null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_linha_users, parent, false);

            TextView status = (TextView) view.findViewById(R.id.textViewStatusUser);
            TextView matricula = (TextView) view.findViewById(R.id.textViewMatriculaUser);
            TextView senha = (TextView) view.findViewById(R.id.textViewSenhaUser);

            Usuario users2 = users.get(position);

            if(users2.getTipo() == 1 ){
                //status.setText(String.valueOf(users2.getTipo()));
                status.setText("Aluno");
                matricula.setText(users2.getMatricula());
                senha.setText(users2.getSenha());
            }else{
                status.setText("Administrador");
                matricula.setText(users2.getMatricula());
                senha.setText(users2.getSenha());
            }

        }

        return view;
    }
}
