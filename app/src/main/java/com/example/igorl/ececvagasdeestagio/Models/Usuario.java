package com.example.igorl.ececvagasdeestagio.Models;

import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
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
    private boolean changePassword;

    public Usuario() {
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

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
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

    private void setTipoInMap(Map<String, Object> map){
            map.put("tipo", getTipo());
    }

    public void salvarUserSolicitadoFBDatabase(DatabaseReference.CompletionListener... completionListener){
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebase();
        referenciaFirebase = referenciaFirebase.child("usuarios").child("solicitados");

        if( completionListener.length == 0 ){
            referenciaFirebase.child(String.valueOf(getId())).setValue(this);
        }
        else{
            referenciaFirebase.child(String.valueOf(getId())).setValue(this, completionListener[0]);
        }
    }

    public void salvarUserAprovadosFBDatabase(DatabaseReference.CompletionListener... completionListener){
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebase();
        referenciaFirebase = referenciaFirebase.child("usuarios").child("aprovados");

        if( completionListener.length == 0 ){
            referenciaFirebase.child(String.valueOf(getId())).setValue(this);
        }
        else{
            referenciaFirebase.child(String.valueOf(getId())).setValue(this, completionListener[0]);
        }
    }

    public void salvarUserFBDatabase(DatabaseReference.CompletionListener... completionListener){
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

    public void updateUserFBDatabase(DatabaseReference.CompletionListener... completionListener){
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

        if( completionListener.length == 0 ){
            referenciaFirebase.updateChildren(map);
        }
        else{
            referenciaFirebase.updateChildren(map, completionListener[0]);
        }
    }

    public void updateUserFBDatabaseEditar(DatabaseReference.CompletionListener... completionListener){
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebase();

        if(getTipo() == 1){
            referenciaFirebase = referenciaFirebase.child("usuarios").child("alunos").child(getId());
        }else{
            referenciaFirebase = referenciaFirebase.child("usuarios").child("administradores").child(getId());
        }

        HashMap<String, Object> map = new HashMap<>();
        setNomeInMap(map);
        setTipoInMap(map);
        if(map.isEmpty()){
            return;
        }

        if( completionListener.length == 0 ){
            referenciaFirebase.updateChildren(map);
        }
        else{
            referenciaFirebase.updateChildren(map, completionListener[0]);
        }
    }

    public void updateUserFBDatabaseChangePassword(String senha, Boolean mudou){
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebase();

        if(getTipo() == 1){
            referenciaFirebase = referenciaFirebase.child("usuarios").child("alunos").child(getId());
        }else{
            referenciaFirebase = referenciaFirebase.child("usuarios").child("administradores").child(getId());
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("senha", senha);
        map.put("changePassword", mudou);
        if(map.isEmpty()){
            return;
        }
        referenciaFirebase.updateChildren(map);
    }

    public boolean isUsuarioAdministrador(){
        if(getTipo() == 2){
            return true;
        }
        return false;
    }
}
