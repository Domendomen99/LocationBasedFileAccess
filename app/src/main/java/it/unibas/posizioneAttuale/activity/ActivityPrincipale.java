package it.unibas.posizioneAttuale.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import it.unibas.posizioneAttuale.Applicazione;
import it.unibas.posizioneAttuale.R;
import it.unibas.posizioneAttuale.modello.Costanti;
import it.unibas.posizioneAttuale.vista.VistaPrincipale;

public class ActivityPrincipale extends AppCompatActivity {

    public static final String TAG = ActivityPrincipale.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principale);

        //TODO modificare intent apertura file

        // -- blocca orientamento schermo in verticale
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void mostraMessaggio(String messaggio){
        Log.d(TAG," -app chiamata a mostraMessaggio : " + messaggio.toString());
        Toast.makeText(Applicazione.getInstance(), messaggio, Toast.LENGTH_LONG).show();
    }

    public VistaPrincipale getVistaPrincipale(){
        return (VistaPrincipale) getSupportFragmentManager().findFragmentById(R.id.vistaPrincipale);
    }

    public void mostraActivityFileDisponibili() {
        Intent intent = new Intent(this,ActivityMostraFileDisponibili.class);

        VistaPrincipale vistaPrincipale=this.getVistaPrincipale();
        LocationManager locationManager = vistaPrincipale.getLocationManager();
        LocationListener locationListener = vistaPrincipale.getLocationListener();
        locationManager.removeUpdates(locationListener);
        Log.d(TAG,"-- -- -- -- -- aggiornamento posizione DISATTIVATO");

        //Log.d(TAG,"-- -- -- -- -- aggiornamento posizione ATTIVO");

        this.startActivity(intent);
    }


}
