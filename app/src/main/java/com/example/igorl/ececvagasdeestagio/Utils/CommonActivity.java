package com.example.igorl.ececvagasdeestagio.Utils;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.igorl.ececvagasdeestagio.Views.CadastroVaga;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by igorl on 11/04/2018.
 */

public abstract class CommonActivity extends AppCompatActivity {

    protected ProgressBar progressBar;
    protected ProgressDialog dialog;

    protected void openProgressBar(){
        progressBar.setVisibility( View.VISIBLE );
    }

    protected void closeProgressBar(){
        progressBar.setVisibility( View.GONE );
    }

    abstract protected void initViews();

    abstract protected void initUser();

    protected static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    protected void disableEditText(EditText editText) {
        editText.setEnabled(false);
    }

    protected void enableEditText(EditText editText) {
        editText.setEnabled(true);
    }

    protected void disableButton(Button button) {
        button.setVisibility(View.INVISIBLE);
        button.setEnabled(false);
    }

    protected void enableButton(Button button) {
        button.setVisibility(View.VISIBLE);
        button.setEnabled(true);
    }

    protected void showDialogMessage(String mensagem) {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage(mensagem);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.setCancelable(false);
        //builder.show();
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    protected void openDialog(String msg){
        dialog.setMessage(msg);
        dialog.setIndeterminate(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    protected void closeDialog(){
        dialog.dismiss();
    }
}
