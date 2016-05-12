package net.netne.kitlfre.takeawat;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Bruno Martins
 */
public class defenicoes extends BaseNavegationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defenicoes);


        getFragmentManager().beginTransaction()
                .replace(R.id.conteudo_defenicoes,
                        new MainSettingsFragment()).commit();
    }

    public static class MainSettingsFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
