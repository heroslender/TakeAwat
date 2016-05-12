package net.netne.kitlfre.takeawat;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bruno Martins
 */
public class pedir_info extends AppCompatActivity {

    EditText hora;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedir_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        hora = (EditText) findViewById(R.id.HoraEntrega);

        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            ListView list_pedido = (ListView) findViewById(R.id.list_pedido_info);

            dataBaseLocal bdLocal = new dataBaseLocal(pedir_info.this, null, null, 1);

            List<Map<String, String>> opcoes = new ArrayList<>();
            List<pedido> pedidos = bdLocal.procuraPedidos();
            Log.d("Pedidos ", "Tamanho array: " + pedidos.size());
            for (pedido p : pedidos) {
                Map<String, String> data = new HashMap<>(2);
                data.put("primeira", p.getP().getDesignacao());
                data.put("segunda", "Quantidade: " + p.getQuant());
                opcoes.add(data);
            }

            SimpleAdapter adapter = new SimpleAdapter(this, opcoes,
                    android.R.layout.simple_list_item_2,
                    new String[] {"primeira", "segunda" },
                    new int[] {android.R.id.text1, android.R.id.text2 });

            list_pedido.setAdapter(adapter);
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("Exception", "Erro[" + e.getMessage() + "] ");
        }
    }

    public void horaEntrega(View view){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(pedir_info.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hora.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);
        mTimePicker.setTitle("Selecione a hora");
        mTimePicker.show();
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
