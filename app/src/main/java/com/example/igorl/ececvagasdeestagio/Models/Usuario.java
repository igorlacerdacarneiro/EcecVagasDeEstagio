package com.example.igorl.ececvagasdeestagio.Models;

import android.content.Context;
import android.util.Log;

import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Helper.Preferencias;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by igorl on 19/03/2018.
 */

public class Usuario {

    public static String PROVIDER = "com.exemple.igorl.ececvagasdeestagio.domain.User.PROVIDER";

    private String id;
    private int tipo;
    private String nome;
    private String matricula;
    private String email;
    private String senha;

    public Usuario() {
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> hashMapUsuario = new HashMap<>();
        hashMapUsuario.put("id", getId());
        hashMapUsuario.put("tipo", getTipo());
        hashMapUsuario.put("nome", getNome());
        hashMapUsuario.put("matricula", getMatricula());
        hashMapUsuario.put("email", getEmail());
        hashMapUsuario.put("senha", getSenha());

        return hashMapUsuario;
    }

    public Usuario(String id, int tipo, String nome, String matricula, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.matricula = matricula;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    private void setNomeInMap(Map<String, Object> map){
        if(getNome() != null){
            map.put("nome", getNome());
        }
    }

    private void setSenhaInMap(Map<String, Object> map){
        if(getSenha() != null){
            map.put("senha", getSenha());
        }
    }

    public void salvarDB(DatabaseReference.CompletionListener... completionListener){
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebase();
        if(getTipo() == 1){
            referenciaFirebase = referenciaFirebase.child("usuarios").child("alunos");
        }else{
           referenciaFirebase =  referenciaFirebase.child("usuarios").child("administradores");
        }

        if( completionListener.length == 0 ){
            referenciaFirebase.child(String.valueOf(getId())).setValue(this);
        }
        else{
            referenciaFirebase.child(String.valueOf(getId())).setValue(this, completionListener[0]);
        }
    }

    public void updateDB(){
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebase();

        if(getTipo() == 1){
            referenciaFirebase = referenciaFirebase.child("usuarios").child("alunos").child(getId());
        }else{
            referenciaFirebase = referenciaFirebase.child("usuarios").child("administradores").child(getId());
        }

        HashMap<String, Object> map = new HashMap<>();
        setNomeInMap(map);
        setSenhaInMap(map);
        if(map.isEmpty()){
            return;
        }

        referenciaFirebase.updateChildren(map);
    }
}
