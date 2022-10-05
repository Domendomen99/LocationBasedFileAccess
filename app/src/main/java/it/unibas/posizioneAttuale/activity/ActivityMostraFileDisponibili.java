package it.unibas.posizioneAttuale.activity;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import it.unibas.posizioneAttuale.Applicazione;
import it.unibas.posizioneAttuale.R;
import it.unibas.posizioneAttuale.modello.Costanti;
import it.unibas.posizioneAttuale.vista.VistaFileDisponibili;
import it.unibas.posizioneAttuale.vista.VistaPrincipale;

public class ActivityMostraFileDisponibili extends AppCompatActivity {

    public static final String TAG = ActivityMostraFileDisponibili.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra_file_disponibili);
        String aula = (String) Applicazione.getInstance().getModello().getBean(Costanti.AULA_ATTUALE);
        mostraMessaggio("Ti trovi nell'area : " + aula);

        // -- blocca orientamento schermo in verticale
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);






    }

    public void mostraMessaggio(String messaggio){
        Toast.makeText(Applicazione.getInstance(), messaggio, Toast.LENGTH_LONG).show();
    }

    public VistaFileDisponibili getVistaFileDisponibili(){
        return (VistaFileDisponibili) this.getSupportFragmentManager().findFragmentById(R.id.vistaFileDisponibili);
    }



}
