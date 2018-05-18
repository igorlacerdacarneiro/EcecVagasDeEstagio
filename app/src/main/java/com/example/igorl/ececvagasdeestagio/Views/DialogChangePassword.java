package com.example.igorl.ececvagasdeestagio.Views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.igorl.ececvagasdeestagio.R;

/**
 * Created by igorl on 16/05/2018.
 */

public class DialogChangePassword extends AppCompatDialogFragment {

    private EditText editTextPassword;
    private DialogChangePasswordListener dialogChangePasswordListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_change_password, null);

        builder.setView(view)
                .setTitle("Mudar senha")
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(editTextPassword.getText().length() < 6) {

                        }else{
                            String password = editTextPassword.getText().toString();
                            dialogChangePasswordListener.returnPassword(password);
                        }
                    }
                });

        editTextPassword = view.findViewById(R.id.edit_change_password);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            dialogChangePasswordListener = (DialogChangePasswordListener) context;
        }catch (ClassCastException e){
            throw  new ClassCastException(context.toString() + "must implement dialog");
        }
    }

    public interface DialogChangePasswordListener{
        void returnPassword(String password);
    }
}
