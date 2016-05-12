package net.netne.kitlfre.takeawat;

import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Bruno Martins
 */

public class inicial extends BaseNavegationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicial);

        mNavigationView.getMenu().getItem(0).setChecked(true);

        TextView bem_vindo = (TextView) findViewById(R.id.bem_vindo);
        bem_vindo.setText("Bem-Vindo\n" + userLocalStore.getUser().getNome());
    }
}
