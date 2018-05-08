package com.example.igorl.ececvagasdeestagio.Views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.igorl.ececvagasdeestagio.Adapters.FragmentPageAdapterUsers;
import com.example.igorl.ececvagasdeestagio.R;

@SuppressWarnings("deprecation")
public class AdministracaoUsuarios extends AppCompatActivity {

    private TabLayout mTabLayoutUsuarios;
    private ViewPager mViewPagerUsuarios;
    private FloatingActionButton addUsuarios;
    private Toolbar mToobar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administracao_usuarios);

        mToobar = (Toolbar) findViewById(R.id.toolbar_admin_usuarios);
        mToobar.setTitle(R.string.adminUser);
        mToobar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTabLayoutUsuarios = (TabLayout) findViewById(R.id.tabLayoutUsuarios);
        mViewPagerUsuarios = (ViewPager) findViewById(R.id.viewPagerUsuarios);

        mViewPagerUsuarios.setAdapter(new FragmentPageAdapterUsers(getSupportFragmentManager(), getResources().getStringArray(R.array.titulo_tab2)));

        mTabLayoutUsuarios.setupWithViewPager(mViewPagerUsuarios);

        addUsuarios = (FloatingActionButton) findViewById(R.id.floatingActionButtonUsuarios);

        addUsuarios.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v){
                Intent intent = new Intent(AdministracaoUsuarios.this, CadastroUsuario.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
