package com.example.igorl.ececvagasdeestagio.Views;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.igorl.ececvagasdeestagio.Adapters.FragmentPageAdapterVagas;
import com.example.igorl.ececvagasdeestagio.R;

@SuppressWarnings("deprecation")
public class AdministracaoVagas extends AppCompatActivity {

    private TabLayout mTabLayoutVagas;
    private ViewPager mViewPagerVagas;
    private FloatingActionButton addVagas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administracao_vagas);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mTabLayoutVagas = (TabLayout) findViewById(R.id.tabLayoutVagas);
        mViewPagerVagas = (ViewPager) findViewById(R.id.viewPagervagas);

        mViewPagerVagas.setAdapter(new FragmentPageAdapterVagas(getSupportFragmentManager(), getResources().getStringArray(R.array.titulo_tab)));

        mTabLayoutVagas.setupWithViewPager(mViewPagerVagas);

        addVagas = (FloatingActionButton) findViewById(R.id.floatingActionButtonVagas);
        addVagas.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v){
                Intent intent = new Intent(AdministracaoVagas.this, CadastroVaga.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home)
            finish();
        return  super.onOptionsItemSelected(item);
    }
}
