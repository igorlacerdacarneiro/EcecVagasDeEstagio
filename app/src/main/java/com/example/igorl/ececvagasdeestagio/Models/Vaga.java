package com.example.igorl.ececvagasdeestagio.Models;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Views.CadastroVaga;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by igorl on 19/03/2018.
 */

public class Vaga {
    private String codigo;
    private int status;
    private String curso;
    private String empresa;
    private String local;
    private String titulo;
    private String horario;
    private String atividades;
    private String requisitos;
    private String numero;
    private String bolsa;
    private String informacoes;
    private String imagem;
    private String data;
    private String key;

    public Vaga() {
    }

    public Vaga(String codigo, int status, String curso, String empresa, String local, String titulo, String horario, String atividades, String requisitos, String numero, String bolsa, String informacoes, String imagem, String data) {
        this.codigo = codigo;
        this.status = status;
        this.curso = curso;
        this.empresa = empresa;
        this.local = local;
        this.titulo = titulo;
        this.horario = horario;
        this.atividades = atividades;
        this.requisitos = requisitos;
        this.numero = numero;
        this.bolsa = bolsa;
        this.informacoes = informacoes;
        this.imagem = imagem;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getAtividades() {
        return atividades;
    }

    public void setAtividades(String atividades) {
        this.atividades = atividades;
    }

    public String getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBolsa() {
        return bolsa;
    }

    public void setBolsa(String bolsa) {
        this.bolsa = bolsa;
    }

    public String getInformacoes() {
        return informacoes;
    }

    public void setInformacoes(String informacoes) {
        this.informacoes = informacoes;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public void salvarVagaFBDatabase(){
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebase();
        if(getStatus() == 1){
            referenciaFirebase.child("vagas").child("disponiveis").child(String.valueOf(getCodigo())).setValue(this);
        }else{
            referenciaFirebase.child("vagas").child("encerradas").child(String.valueOf(getCodigo())).setValue(this);
        }
    }

    public boolean isVagaDisponivel(int tipo){
        if(tipo == 1){
            return true;
        }
        return false;
    }

    public boolean isVagaEncerrada(int tipo){
        if(tipo == 2){
            return true;
        }
        return false;
    }

}
