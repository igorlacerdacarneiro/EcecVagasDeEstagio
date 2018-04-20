package com.example.igorl.ececvagasdeestagio.Utils;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by igorl on 11/04/2018.
 */

public abstract class CommonActivity extends AppCompatActivity {

    protected ProgressBar progressBar;

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
}
