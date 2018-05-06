package com.example.igorl.ececvagasdeestagio.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.Slide;
import android.support.transition.TransitionManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.igorl.ececvagasdeestagio.Models.Usuario;
import com.example.igorl.ececvagasdeestagio.Models.Vaga;
import com.example.igorl.ececvagasdeestagio.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class VagaActivity extends AppCompatActivity{

    private ViewHolder mViewHolder = new ViewHolder();

    private ViewGroup mLinearVagasTransition;
    private TextView mAtividades;
    private TextView mRequisitos;
    private TextView mNumero;
    private TextView mValor;
    private TextView mInformacoes;
    private Toolbar mToobar;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setSharedElementEnterTransition(new ChangeBounds());
            getWindow().setSharedElementExitTransition(new ChangeBounds());

            Transition transition1 = getWindow().getSharedElementEnterTransition();
            transition1.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {}
                @Override
                public void onTransitionEnd(Transition transition) {
                    TransitionManager.beginDelayedTransition(mLinearVagasTransition, new Slide());
                    mAtividades.setVisibility(View.VISIBLE);
                    mRequisitos.setVisibility(View.VISIBLE);
                    mNumero.setVisibility(View.VISIBLE);
                    mValor.setVisibility(View.VISIBLE);
                    mInformacoes.setVisibility(View.VISIBLE);
                }
                @Override
                public void onTransitionCancel(Transition transition) {}
                @Override
                public void onTransitionPause(Transition transition) {}
                @Override
                public void onTransitionResume(Transition transition) {}
            });
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaga);

        mToobar = (Toolbar) findViewById(R.id.toolbar_vaga_activity);
        mToobar.setTitle("");
        mToobar.setBackgroundColor(0);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.mViewHolder.mProgressBar = (ProgressBar) findViewById(R.id.progress_vaga_selecionada);
        this.mViewHolder.mProgressBar.setVisibility(View.VISIBLE);

        this.mViewHolder.mImagem = (ImageView) findViewById(R.id.imagemViewDivulgacaoDaVaga);
        this.mViewHolder.mTitulo = (TextView) findViewById(R.id.textViewTituloDaVaga);
        this.mViewHolder.mEmpresa = (TextView) findViewById(R.id.textViewEmpresaDaVaga);
        this.mViewHolder.mLocal = (TextView) findViewById(R.id.textViewLocalDaVaga);
        this.mViewHolder.mHorario = (TextView) findViewById(R.id.textViewHorarioDaVaga);

        mLinearVagasTransition = (ViewGroup) findViewById(R.id.linear_layout_vagas_transition);
        mAtividades = (TextView) findViewById(R.id.textViewAtividadesDaVaga);
        mRequisitos = (TextView) findViewById(R.id.textViewRequisitosDaVaga);
        mNumero = (TextView) findViewById(R.id.textViewNumeroDeVaga);
        mValor = (TextView) findViewById(R.id.textViewValorBolsaDaVaga);
        mInformacoes = (TextView) findViewById(R.id.textViewInformacoesDaVaga);

        Bundle b = getIntent().getExtras();
        if(b != null) {

            String result = b.getString("vaga");
            Vaga vaga = gson.fromJson(result, Vaga.class);

            Picasso.get().load(vaga.getImagem()).fit().centerCrop().into(this.mViewHolder.mImagem);
            this.mViewHolder.mTitulo.setText(vaga.getTitulo());
            this.mViewHolder.mEmpresa.setText(vaga.getEmpresa());
            this.mViewHolder.mLocal.setText(vaga.getLocal());
            this.mViewHolder.mHorario.setText(vaga.getHorario());
            mAtividades.setText(vaga.getAtividades());
            mRequisitos.setText(vaga.getRequisitos());
            mNumero.setText(vaga.getNumero());
            mValor.setText(vaga.getBolsa());
            mInformacoes.setText(vaga.getInformacoes());

            this.mViewHolder.mProgressBar.setVisibility(View.INVISIBLE);

        }else{
            this.mViewHolder.mProgressBar.setVisibility(View.INVISIBLE);
            finalizarActivity();
        }
    }

    private static class ViewHolder{
        ImageView mImagem;
        TextView mTitulo;
        TextView mEmpresa;
        TextView mLocal;
        TextView mHorario;
        ProgressBar mProgressBar;
    }

    private void finalizarActivity(){
        Intent intent = new Intent(VagaActivity.this, Principal.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.i("log", "LOG IGOR = onOptionsItemSelected");
        if(item.getItemId() == android.R.id.home){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                TransitionManager.beginDelayedTransition(mLinearVagasTransition, new Slide());
                mAtividades.setVisibility(View.INVISIBLE);
                mRequisitos.setVisibility(View.INVISIBLE);
                mNumero.setVisibility(View.INVISIBLE);
                mValor.setVisibility(View.INVISIBLE);
                mInformacoes.setVisibility(View.INVISIBLE);
            }
            this.onBackPressed();
        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
