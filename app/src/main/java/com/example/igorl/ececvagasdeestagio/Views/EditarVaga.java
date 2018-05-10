package com.example.igorl.ececvagasdeestagio.Views;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.igorl.ececvagasdeestagio.DAO.ConfiguracaoFirebase;
import com.example.igorl.ececvagasdeestagio.Models.Vaga;
import com.example.igorl.ececvagasdeestagio.R;
import com.example.igorl.ececvagasdeestagio.Utils.CommonActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditarVaga extends CommonActivity {

    private TextView codigo;
    private Spinner curso;
    private RadioGroup vagaStatus;
    private RadioButton vagaDisponivel;
    private RadioButton vagaEncerrada;
    private EditText empresa;
    private EditText local;
    private EditText titulo;
    private TextView horarioInicio;
    private TextView horarioFim;
    private EditText atividades;
    private EditText requisitos;
    private EditText numero;
    private EditText valor;
    private EditText informacoes;
    private ImageView imagem;
    private TextView data;
    private Button salvar;
    private ImageButton editar;
    private TextView selecionarFoto;
    private TextView horario1;
    private TextView horario2;
    private Vaga vaga;
    private DatabaseReference mFirebaseDatabase;
    private StorageReference mStorageReference;
    private CheckBox segunda;
    private CheckBox terca;
    private CheckBox quarta;
    private CheckBox quinta;
    private CheckBox sexta;
    private CheckBox sabado;
    private CheckBox domingo;
    private Boolean mudouTipoCadastro = false;
    private FirebaseStorage mFirebaseStorage;
    private String referenciaImagem;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private Toolbar mToobar;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_vaga);

        dialog = new ProgressDialog(EditarVaga.this);

        mToobar = (Toolbar) findViewById(R.id.toolbar_cadastro_vaga);
        mToobar.setTitle(R.string.tela_editar_vaga);
        mToobar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToobar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        openDialog("Carregando...");

        mStorageReference = ConfiguracaoFirebase.getFirebaseStorage();
        mFirebaseDatabase = ConfiguracaoFirebase.getFirebase();

        initViews();

        /* SETAR O SPINNER DE CURSOS */
        ArrayAdapter adapterCursos = ArrayAdapter.createFromResource(this, R.array.spinner_cursos, android.R.layout.simple_spinner_dropdown_item);
        curso.setAdapter(adapterCursos);

        Bundle b = getIntent().getExtras();
        if(b != null) {

            String result = b.getString("vaga");
            Vaga vaga = gson.fromJson(result, Vaga.class);

            if(vaga.getStatus() == 1){
                vagaDisponivel.setChecked(true);
            }else{
                vagaEncerrada.setChecked(true);
            }
            codigo.setText(vaga.getCodigo());
            empresa.setText(vaga.getEmpresa());
            local.setText(vaga.getLocal());
            titulo.setText(vaga.getTitulo());
            atividades.setText(vaga.getAtividades());
            requisitos.setText(vaga.getRequisitos());
            numero.setText(vaga.getNumero());
            valor.setText(vaga.getBolsa());
            informacoes.setText(vaga.getInformacoes());
            data.setText(vaga.getData());
            setSelectValueSpinner(curso, vaga.getCurso());

            String[] aux = vaga.getHorario().split(" ");
            String juntarStringDia = aux[0]+" "+aux[1]+" "+aux[2];
            selecionarDiasDaSemana(juntarStringDia);
            horarioInicio.setText(aux[4]);
            horarioFim.setText(aux[6]);

            referenciaImagem = vaga.getImagem();

            Picasso.get().load(vaga.getImagem()).fit().centerInside().into(imagem);

            disableButton(salvar);
            horario1.setVisibility(View.INVISIBLE);
            horario2.setVisibility(View.INVISIBLE);
            vagaDisponivel.setClickable(false);
            vagaEncerrada.setClickable(false);
            disableEditText(empresa);
            disableEditText(local);
            disableEditText(titulo);
            disableEditText(atividades);
            disableEditText(requisitos);
            disableEditText(numero);
            disableEditText(valor);
            disableEditText(informacoes);
            curso.setEnabled(false);
            selecionarFoto.setVisibility(View.INVISIBLE);
            segunda.setClickable(false);
            terca.setClickable(false);
            quarta.setClickable(false);
            quinta.setClickable(false);
            sexta.setClickable(false);
            sabado.setClickable(false);
            domingo.setClickable(false);

            closeDialog();
        }else{
            Toast.makeText(EditarVaga.this, "Erro ao recuperar dados da Vaga", Toast.LENGTH_SHORT).show();
            voltarTelaVagas();
        }

         /* AÇÃO DO BOTÃO SALVAR */
        salvar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(!curso.getSelectedItem().toString().equals("Selecione um curso") && !empresa.getText().toString().trim().equals("") &&
                        !local.getText().toString().trim().equals("") && !titulo.getText().toString().trim().equals("") && !horarioInicio.getText().toString().trim().equals("") &&
                        !horarioFim.getText().toString().trim().equals("") && !atividades.getText().toString().trim().equals("") &&
                        !requisitos.getText().toString().trim().equals("") && !numero.getText().toString().trim().equals("") &&
                        !valor.getText().toString().trim().equals("") && !informacoes.getText().toString().trim().equals(""))
                {
                    try {
                        if(validaValorHoariosVaga(horarioInicio.getText().toString(), horarioFim.getText().toString())) {

                            if (validaValorNumeroVaga(numero.getText().toString())) {

                                if (validaValorBolsaVaga(valor.getText().toString())) {

                                    openDialog("Aguarde...");

                                    Calendar cod = Calendar.getInstance();
                                    data.setText(String.valueOf(cod.get(Calendar.DAY_OF_MONTH) + "/" + cod.get(Calendar.MONTH) + "/" + cod.get(Calendar.YEAR) + " às " + cod.get(Calendar.HOUR_OF_DAY) + ":" + cod.get(Calendar.MINUTE)));

                                    initUser();

                                    if(mudouTipoCadastro){

                                        if(vaga.getStatus() == 1){
                                            mFirebaseDatabase = ConfiguracaoFirebase.getFirebase().child("vagas").child("encerradas");
                                        }else{
                                            mFirebaseDatabase = ConfiguracaoFirebase.getFirebase().child("vagas").child("disponiveis");
                                        }
                                        mFirebaseDatabase.child(vaga.getCodigo()).removeValue();
                                    }

                                    if(mImageUri == null){
                                        vaga.setImagem(referenciaImagem);
                                    }else{
                                        salvarImagemFBStorage();
                                    }

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            vaga.salvarVagaFBDatabase();
                                            voltarTelaVagas();
                                        }
                                    }, 5000);

                                } else {
                                    showDialogMessage("Valor da bolsa não pode ser zero.");
                                }
                            } else {
                                showDialogMessage("Número de vaga não pode ser zero.");
                            }
                        }else{
                            showDialogMessage("Horário de início e fim inválido.");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else{
                    showDialogMessage("Todos os campos são obrigatórios.");
                }
            }
        });

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableButton(salvar);
                horario1.setVisibility(View.VISIBLE);
                horario2.setVisibility(View.VISIBLE);
                vagaDisponivel.setClickable(true);
                vagaEncerrada.setClickable(true);
                enableEditText(empresa);
                enableEditText(local);
                enableEditText(titulo);
                enableEditText(atividades);
                enableEditText(requisitos);
                enableEditText(numero);
                enableEditText(valor);
                enableEditText(informacoes);
                curso.setEnabled(true);
                selecionarFoto.setVisibility(View.VISIBLE);
            }
        });

        selecionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        horario1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditarVaga.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hora, int minuto) {
                        horarioInicio.setText(hora + ":"+ String.format("%02d",minuto));
                    }
                }, hour, minute, true);
                timePickerDialog.setTitle("timePickerDialog");
                timePickerDialog.show();
            }
        });

        horario2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditarVaga.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hora, int minuto) {
                        horarioFim.setText(hora + ":"+ String.format("%02d",minuto));
                    }
                }, hour, minute, true);
                timePickerDialog.setTitle("timePickerDialog");
                timePickerDialog.show();
            }
        });

        vagaStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mudouTipoCadastro = true;
            }
        });
    }

    private void selecionarDiasDaSemana(String dias) {
        if(dias.equals("Seg. à Dom.")){
            segunda.setChecked(true);
            terca.setChecked(true);
            quarta.setChecked(true);
            quinta.setChecked(true);
            sexta.setChecked(true);
            sabado.setChecked(true);
            domingo.setChecked(true);
        }else if(dias.equals("Seg. à Sáb.")){
            segunda.setChecked(true);
            terca.setChecked(true);
            quarta.setChecked(true);
            quinta.setChecked(true);
            sexta.setChecked(true);
            sabado.setChecked(true);
        }else if(dias.equals("Seg. à Sex.")){
            segunda.setChecked(true);
            terca.setChecked(true);
            quarta.setChecked(true);
            quinta.setChecked(true);
            sexta.setChecked(true);
        }else{
            String[] aux = dias.split(" ");
            for(int i = 0; i < aux.length; i++){
                if(aux[i].equals("Dom.")){
                    domingo.setChecked(true);
                }else if(aux[i].equals("Seg.")){
                    segunda.setChecked(true);
                }else if(aux[i].equals("Ter.")){
                    terca.setChecked(true);
                }else if(aux[i].equals("Qua.")){
                    quarta.setChecked(true);
                }else if(aux[i].equals("Qui.")){
                    quinta.setChecked(true);
                }else if(aux[i].equals("Sex.")){
                    sexta.setChecked(true);
                }else if(aux[i].equals("Sáb.")){
                    sabado.setChecked(true);
                }
            }
        }
    }

    protected void initViews(){
        codigo = (TextView) findViewById(R.id.textViewCodigoVaga);
        curso = (Spinner) findViewById(R.id.spinnerVagaCurso) ;
        vagaStatus = (RadioGroup) findViewById(R.id.radioStatusVaga);
        vagaDisponivel = (RadioButton) findViewById(R.id.radioStatusVagaDisponivel);
        vagaEncerrada = (RadioButton) findViewById(R.id.radioStatusVagaEncerrada);
        empresa = (EditText) findViewById(R.id.editVagaEmpresa);
        local = (EditText) findViewById(R.id.editVagaLocal);
        titulo = (EditText) findViewById(R.id.editVagaTitulo);
        horario1 = (TextView) findViewById(R.id.textViewVagaHorario1);
        horario2 = (TextView) findViewById(R.id.textViewVagaHorario2);
        horarioInicio = (TextView) findViewById(R.id.textViewVagaHorarioInicio);
        horarioFim = (TextView) findViewById(R.id.textViewVagaHorarioFim);
        atividades = (EditText) findViewById(R.id.editVagaAtividades);
        requisitos = (EditText) findViewById(R.id.editVagaRequisitos);
        numero = (EditText) findViewById(R.id.editVagaNumero);
        valor = (EditText) findViewById(R.id.editVagaValor);
        informacoes = (EditText) findViewById(R.id.editVagaInformacoes);
        selecionarFoto = (TextView) findViewById(R.id.textViewSelecionarFoto);
        imagem = (ImageView) findViewById(R.id.imagemDivulgacaoVaga);
        data = (TextView) findViewById(R.id.textViewDataVaga);
        salvar = (Button) findViewById(R.id.botaoSalvarVaga);
        editar = (ImageButton) findViewById(R.id.imageButtonEditarVaga);
        segunda = (CheckBox) findViewById(R.id.checkbox_segunda);
        terca = (CheckBox) findViewById(R.id.checkbox_terca);
        quarta = (CheckBox) findViewById(R.id.checkbox_quarta);
        quinta = (CheckBox) findViewById(R.id.checkbox_quinta);
        sexta = (CheckBox) findViewById(R.id.checkbox_sexta);
        sabado = (CheckBox) findViewById(R.id.checkbox_sabado);
        domingo = (CheckBox) findViewById(R.id.checkbox_domingo);
    }

    protected void initUser(){
        vaga = new Vaga();
        vaga.setCodigo(codigo.getText().toString());
        vaga.setCurso(curso.getSelectedItem().toString());
        if(vagaDisponivel.isChecked()){
            vaga.setStatus(1);
        }else{
            vaga.setStatus(2);
        }
        vaga.setEmpresa(empresa.getText().toString());
        vaga.setLocal(local.getText().toString());
        vaga.setTitulo(titulo.getText().toString());
        vaga.setHorario(montarHorarioVaga()+" de "+horarioInicio.getText().toString()+" às "+horarioFim.getText().toString());
        vaga.setAtividades(atividades.getText().toString());
        vaga.setRequisitos(requisitos.getText().toString());
        vaga.setNumero(numero.getText().toString());
        vaga.setBolsa(valor.getText().toString());
        vaga.setInformacoes(informacoes.getText().toString());
        vaga.setData(data.getText().toString());
    }

    private void salvarImagemFBStorage(){
        if(mImageUri != null){
            StorageReference fileReference = mStorageReference.child("uploads/"+System.currentTimeMillis() + "."+getFileExtension(mImageUri));
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            vaga.setImagem(taskSnapshot.getDownloadUrl().toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarVaga.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(EditarVaga.this, "Não tem foto selecionada", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void voltarTelaVagas(){
        Intent intent = new Intent(EditarVaga.this, AdministracaoVagas.class);
        startActivity(intent);
        closeDialog();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            if(salvar.isEnabled()) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(EditarVaga.this);
                builder.setTitle("Sair")
                        .setMessage("Deseja sair sem salvar as alterações?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }else{
                finish();
            }
        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();
            imagem.setImageURI(mImageUri);
        }
    }

    private void setSelectValueSpinner(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private boolean validaValorNumeroVaga(String numero) {
        int num = Integer.valueOf(numero);
        if(num <= 0){
            return false;
        }
        return true;
    }

    private boolean validaValorBolsaVaga (String valor) {
        int val = Integer.valueOf(valor);
        if(val <= 0){
            return false;
        }
        return true;
    }

    private boolean validaValorHoariosVaga(String hora1, String hora2) throws ParseException {
        SimpleDateFormat sdfConvert = new SimpleDateFormat("HH:mm");
        Date um = null;
        Date dois = null;

        um = sdfConvert.parse(hora1);
        dois = sdfConvert.parse(hora2);

        if(dois.getTime() <= um.getTime()){
            return false;
        }
        return true;
    }

    private String montarHorarioVaga(){
        String seg = "", ter = "", quar = "", qui = "", sex = "", sab = "", dom = "", msg = "";
        int cont = 0;

        if(segunda.isChecked()){
            seg = "Seg. ";
            cont++;
        }
        if(terca.isChecked()){
            ter = "Ter. ";
            cont++;
        }
        if(quarta.isChecked()){
            quar = "Qua. ";
            cont++;
        }
        if(quinta.isChecked()){
            qui = "Qui. ";
            cont++;
        }
        if(sexta.isChecked()){
            sex = "Sex. ";
            cont++;
        }
        if(sabado.isChecked()){
            sab = "Sab. ";
            cont++;
        }
        if(domingo.isChecked()){
            dom = "Dom. ";
            cont++;
        }

        if(cont == 7){
            msg = "Seg. à Dom.";
        }else{

            if(cont == 6 && (!seg.equals("") && !ter.equals("") &&!quar.equals("") && !qui.equals("") && !sex.equals("") && !sab.equals(""))){
                msg = "Seg. à Sáb.";
            }else if(cont == 5 && (!seg.equals("") && !ter.equals("") &&!quar.equals("") && !qui.equals("") && !sex.equals(""))){
                msg = "Seg. à Sex.";
            }else{
                if(!seg.isEmpty())
                    msg = msg + seg;
                if(!ter.isEmpty())
                    msg = msg+ ter;
                if(!quar.isEmpty())
                    msg = msg + quar;
                if(!qui.isEmpty())
                    msg = msg + qui;
                if(!sex.isEmpty())
                    msg = msg + sex;
                if(!sab.isEmpty())
                    msg = msg + sab;
            }
        }

        return msg;
    }
}
