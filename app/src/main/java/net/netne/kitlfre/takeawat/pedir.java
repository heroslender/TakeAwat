package net.netne.kitlfre.takeawat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Bruno Martins
 */
public class pedir extends BaseNavegationActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private Spinner spinnerDias;
    private ProdutoListAdapter adapter;
    EditText quant;
    List<pedido> pedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedir);
        mNavigationView.getMenu().getItem(2).setChecked(true);
        quant =(EditText) findViewById(R.id.txt_pedir_quant);
        quant.setFilters(new InputFilter[]{ new InputFilterMinMax(0.5, 5.0)});
        quant.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String si = s.toString();
                Log.e("string", si);
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Validate input to make sure that it fits your requirements
            }

            public double round(double value, int places) {
                if (places < 0) throw new IllegalArgumentException();

                BigDecimal bd = new BigDecimal(value);
                bd = bd.setScale(places, RoundingMode.HALF_UP);
                //return bd.doubleValue();
                return (Math.ceil(bd.doubleValue() * 2) / 2);
            }
        });

        // Spinner element
        spinner = (Spinner) findViewById(R.id.spinner_pedir_produtos);
        spinnerDias = (Spinner) findViewById(R.id.spinner_dias);

        // Spinner click listener
        spinnerDias.setOnItemSelectedListener(this);
        dataBaseLocal bdLocal = new dataBaseLocal(pedir.this, null, null, 1);

        List<String> dias = bdLocal.procuraTodosProdutosDias();
        String[] dia = new String[dias.size()];
        dias.toArray(dia);
        // Creating adapter for spinner
        ArrayAdapter dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, dia);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerDias.setAdapter(dataAdapter);

        setupListViewAdapter();

        pedidos = new ArrayList<>();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
//        String dia = (String)spinner.getSelectedItem();

        String data = spinnerDias.getSelectedItem().toString();
        Log.e("Data", "[" + data + "]");
        if (data != null && data.trim() != "") {
            dataBaseLocal bdLocal = new dataBaseLocal(pedir.this, null, null, 1);

            List<Produto> produtos = bdLocal.procuraTodosProdutos(data);
            Log.e("produto", "[" + produtos.get(0) + "]");
            Produto[] produto = new Produto[produtos.size()];
            produtos.toArray(produto);
            // Creating adapter for spinner
            ArrayAdapter dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, produto);
            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            spinner.setAdapter(dataAdapter);

            setupListViewAdapter();
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    public void removeOnClickHandler(View v) {
        // Vais buscar a classe
        pedido itemToRemove = (pedido)v.getTag();
        pedidos.remove(itemToRemove);
        adapter.remove(itemToRemove);
    }

    private void setupListViewAdapter() {
        adapter = new ProdutoListAdapter(pedir.this, R.layout.list_pedidos, new ArrayList<pedido>());
        ListView atomPaysListView = (ListView)findViewById(R.id.EnterPays_atomPaysList);
        atomPaysListView.setAdapter(adapter);
    }

    public void add_pedido(View v){
        try{
            if (quant.getText().toString().trim().length() == 0){
                throw new Exception("Tem de preencher a quantidade!");
            }
            Double quantidade = Double.valueOf(quant.getText().toString());
            if(quantidade<0.5 || quantidade>5.0){
                throw new Exception("A quantidade tem de ser no minimo 0.5(meia dose) e no maximo 5 - "+quant.getText().toString()+" - " + quantidade);
            }
            // Check if no view has focus:
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            pedidos.add(0, new pedido((Produto)spinner.getSelectedItem(), quantidade));
            adapter.insert(pedidos.get(0), 0);
        }catch (Exception erro){
            showError(erro);
        }
    }

    public void btn_avanca(View view){
        dataBaseLocal bdLocal = new dataBaseLocal(pedir.this, null, null, 1);
        bdLocal.apagaTodosPedidos();
        for (pedido p : pedidos) {
            bdLocal.adicionaPedido(p);
        }
        startActivity(new Intent(this, pedir_info.class));
    }

//    public void txt_data(View view){
//        Calendar mcurrentDate = Calendar.getInstance();
//        int mYear = mcurrentDate.get(Calendar.YEAR);
//        int mMonth = mcurrentDate.get(Calendar.MONTH);
//        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog mDatePicker;
//        mDatePicker = new DatePickerDialog(pedir.this, new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
//                selectedmonth = selectedmonth + 1;
//                ((EditText)findViewById(R.id.txt_data)).setText(selectedyear + "-" + selectedmonth + "-" + selectedday);
//            }
//        }, mYear, mMonth, mDay);
//        mDatePicker.setTitle("Selecione a data");
//        mDatePicker.show();
//    }

    private void showError(Exception ero){
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Ocorreu um erro:\n" + ero.getMessage());
        dialogBuilder.setPositiveButton("ok", null);
        dialogBuilder.show();
    }
}
