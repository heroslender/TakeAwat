package net.netne.kitlfre.takeawat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bruno Martins
 */
public class ementa extends BaseNavegationActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerDias;
    ListView listEmenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ementa);
        try {
            mNavigationView.getMenu().getItem(1).setChecked(true);

            spinnerDias = (Spinner) findViewById(R.id.spinner_dias_ementa);

            // Spinner click listener
            spinnerDias.setOnItemSelectedListener(this);

            dataBaseLocal bdLocal = new dataBaseLocal(this, null, null, 1);
            List<String> dias = bdLocal.procuraTodosProdutosDias();
            String[] dia = new String[dias.size()];
            dias.toArray(dia);
            Log.e("Dias", dias.size()+" "+dia);
            // Creating adapter for spinner
            ArrayAdapter dataAdapter = new ArrayAdapter(this, R.layout.spinner_ementa, dia);
            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            spinnerDias.setAdapter(dataAdapter);

            listEmenta = (ListView) findViewById(R.id.list_ementa);

        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("Exception", "Erro[" + e.getMessage() + "] ");
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String dia = spinnerDias.getSelectedItem().toString();
        try {
            dataBaseLocal bdLocal = new dataBaseLocal(this, null, null, 1);

            List<Map<String, String>> opcoes = new ArrayList<>();
            List<Produto> produtos = bdLocal.procuraTodosProdutos(dia);
            Log.d("Produtos: ", "Tamanho array: " + produtos.size());
            for (Produto pr : produtos) {
                Map<String, String> data = new HashMap<>(2);
                data.put("primeira", pr.getDesignacao());
                data.put("segunda", pr.getPreco() + " €");
                opcoes.add(data);
            }

            SimpleAdapter adapter = new SimpleAdapter(this, opcoes,
                    android.R.layout.simple_list_item_2,
                    new String[] {"primeira", "segunda" },
                    new int[] {android.R.id.text1, android.R.id.text2 });

            listEmenta.setAdapter(adapter);
        } catch (Exception erro){
            erro.printStackTrace();
            Log.e("Exception", "Erro[" + erro.getMessage() + "] ");
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}