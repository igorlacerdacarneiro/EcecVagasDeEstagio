package com.example.igorl.ececvagasdeestagio.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by igorl on 03/04/2018.
 */

public class Preferencias {
    private Context context;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "projetoFirebase.preferencias";
    private int MODE = 0;
    private SharedPreferences.Editor editor;

    public static String PREF = "com.exemple.igorl.ececvagasdeestagio.PREF";

    private final String CHAVE_IDENTIFICADOR = "identificarUsuarioLogado";
    private final String CHAVE_NOME = "nomeUsuarioLogado";

    public Preferencias(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(NOME_ARQUIVO, MODE);

        editor = preferences.edit();
    }

    public void salvarUsuarioPreferencias(String identificadorUsuario, String nomeUsuario){
        editor.putString(CHAVE_IDENTIFICADOR, identificadorUsuario);
        editor.putString(CHAVE_NOME, nomeUsuario);
        editor.commit();
    }

    public  String getIdentificador(){
        return preferences.getString(CHAVE_IDENTIFICADOR, null);
    }

    public  String getNome(){
        return preferences.getString(CHAVE_NOME, null);
    }

    static public void saveSP(Context context, String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    static public String getSP(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String token = sp.getString(key, "");
        return( token );
    }
}