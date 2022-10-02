package it.unibas.posizioneAttuale.vista;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.PolyUtil;

import java.io.InputStream;
import java.util.List;

import it.unibas.posizioneAttuale.Applicazione;
import it.unibas.posizioneAttuale.Controllo.ControlloVistaPrincipale;
import it.unibas.posizioneAttuale.R;
import it.unibas.posizioneAttuale.activity.ActivityPrincipale;
import it.unibas.posizioneAttuale.modello.Costanti;
import it.unibas.posizioneAttuale.modello.Poligono;

public class VistaPrincipale extends Fragment implements OnMapReadyCallback {
    // -- logging
    public static final String TAG = ActivityPrincipale.class.getSimpleName();
    // -- label
    private TextView labelPosizione;
    // -- oggetto google map
    private GoogleMap gMap;
    // -- gestore posizione
    private LocationManager locationManager = null;
    // -- notifica cambio posizione
    private LocationListener locationListener = null;
    // -- classe coordinate
    private LatLng latLng;
    // -- puntatore su mappa
    private Marker marker;
    //-- oggetto mapView
    private MapView mapView;
    // -- tempo minimo aggiornamento posizione
    private final long MIN_TIME = 10000;
    // -- minima differenza di posizione per aggiornamento posizione
    private final long MIN_DST = 10;
    // -- dichiarazione bottone
    private Button bottoneMostraActivityFileDisponibili;
    private Button bottoneCaricaFile, bottoneSelezionaFile;
    private LocationRequest locationRequest;

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public LocationListener getLocationListener() {
        return locationListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "-- onResume");
        avviaRicercaPosizione();
    }

    @SuppressLint("MissingPermission")
    private void avviaRicercaPosizione() {
        if (locationManager != null) {
            Log.d(TAG, "-- locationManager Riattivato");
            gMap.setMyLocationEnabled(true);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DST, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DST, locationListener);
            return;
        }
        Log.d(TAG, "-- locationManager non ancora inizializzato");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "-- onPause");
        //this.locationManager.removeUpdates(this.locationListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.vista_principale, container, false);
        //assegnazione componenti
        this.mapView = vista.findViewById(R.id.map);
        this.bottoneMostraActivityFileDisponibili = vista.findViewById(R.id.bottoneMostraActivityFileDisponibili);
        bottoneMostraActivityFileDisponibili.setEnabled(false);
        this.bottoneMostraActivityFileDisponibili.setOnClickListener(Applicazione.getInstance().getControlloVistaPrincipale().getAzioneAccediAFileDisponibili());
        this.labelPosizione = vista.findViewById(R.id.labelPosizione);
        Log.d(TAG, "-- app Tutti i componenti assegnati ");
        //call onCreate
        mapView.onCreate(savedInstanceState);
        //call onResume
        mapView.onResume();
        //inizializza automaticamente la mappa e la vista
        mapView.getMapAsync(this);
        Log.d(TAG, "-- app Fine OnCreate ");
        return vista;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //prova
        controlloPermessi();
        Log.d(TAG, "-- app ingresso in onMapReady");
        // -- servizio aggiornamento posizione
        locationManager = (LocationManager) this.getActivity().getSystemService(LOCATION_SERVICE);
        // -- carico punti poligoni da file JSON
        final List<Poligono> listaPoligoni = caricaPoligoniDaFile();
        Log.d(TAG, "-- app poligoni caricati da file ");
        // -- creazione locationListner per gestire cambiamenti di posizione utente
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d(TAG, "-- app Richiedo posizione attuale");
                Log.d(TAG, "-- app Posizione Attuale : " + location.getLatitude() + " . " + location.getLongitude());


                // -- aggiornamento marker posizione attuale
                latLng = new LatLng(location.getLatitude(), location.getLongitude());

                // -- movimento camera
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                doveMiTrovo(location, listaPoligoni);
            }
        };
        // -- richiesta posizione a condizione di aver concesso i permessi
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "-- app secondo controllo permessi concessi ");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DST, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DST, locationListener);
            Log.d(TAG, "-- app ho richiesto una posizione aggiornata");
        }

        // -- assegna la mappa
        gMap = googleMap;

        // -- setting mappa, alcune impostazioni disattivate per snellire interfaccia
        Log.d(TAG, "-- app Sto attivando i pulsanti sulla mappa");
        // -- attiva pulsante posizione attuale
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
            Log.d(TAG, "-- app pulsante posizione attuale attivato");
        }
        // -- puntatore luogo iniziale : rimosso, rimane solo movimento telecamera su centro Potenza
        LatLng luogoIniziale = new LatLng(40.6404, 15.8056);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(luogoIniziale));
        // -- disegna mappa
        disegnaMappa(gMap, listaPoligoni);
        Log.d(TAG, " -app mappa disegnata");
    }

    private void controlloPermessi() {
        ActivityPrincipale activityPrincipale = (ActivityPrincipale) Applicazione.getInstance().getCurrentActivity();
        boolean gpsAttivo = isGpsEnabled();
        if (!gpsAttivo) {
            activityPrincipale.mostraMessaggio("Attiva GPS");
        }
        // funzionante -- ActivityCompat.requestPermissions(activityPrincipale, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        ActivityCompat.requestPermissions(activityPrincipale, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(activityPrincipale, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permessi posizione approssimativa concessi");
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permessi posizione precisa concessi");
        }
    }


    private boolean isGpsEnabled() {
        ActivityPrincipale activityPrincipale = (ActivityPrincipale) Applicazione.getInstance().getCurrentActivity();
        locationManager = null;
        boolean attivato = false;
        if (locationManager == null) {
            locationManager = (LocationManager) activityPrincipale.getSystemService(LOCATION_SERVICE);
        }
        attivato = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return attivato;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "-- entro in onRequestPrmissionResult");

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permessi posizione concessi");
            }
        }
    }


    private void doveMiTrovo(Location location, List<Poligono> listaPoligoni) {
        Log.d(TAG, "--app Esecuzione fn doveMiTrovo");
        ActivityPrincipale activityPrincipale = (ActivityPrincipale) Applicazione.getInstance().getCurrentActivity();
        for (Poligono poligono : listaPoligoni) {
            if (PolyUtil.containsLocation(new LatLng(location.getLatitude(), location.getLongitude()), poligono.getListaPunti(), false)) {
                //activityPrincipale.mostraMessaggio("Sono nell'aula : " + poligono.getNomeAula());
                labelPosizione.setText("Sono nell'aula : " + poligono.getNomeAula());
                activityPrincipale = (ActivityPrincipale) Applicazione.getInstance().getCurrentActivity();
                activityPrincipale.findViewById(R.id.bottoneMostraActivityFileDisponibili).setEnabled(true);
                Applicazione.getInstance().getModello().putBean(Costanti.AULA_ATTUALE, poligono.getNomeAula());
                Log.d(TAG, "-- aula attuale salvata in memoria");
                return;
            }
        }
        activityPrincipale.findViewById(R.id.bottoneMostraActivityFileDisponibili).setEnabled(false);
        //activityPrincipale.mostraMessaggio("Sono fuori dall'Università");
        labelPosizione.setText("Sono fuori dall'Università");
        Applicazione.getInstance().getModello().putBean(Costanti.AULA_ATTUALE, null);

    }


    private void disegnaMappa(GoogleMap gMap, List<Poligono> listaPoligoni) {

        for (Poligono poligono : listaPoligoni) {
            Log.d(TAG, "Poligono attuale : " + poligono.getNomeAula());
            gMap.addPolygon(new PolygonOptions().addAll(poligono.getListaPunti()).geodesic(true).strokeColor(Color.BLACK).strokeWidth(20));
        }
    }

    private List<Poligono> caricaPoligoniDaFile() {
        Log.d(TAG, "-- Eseguo carica poligoni da file");
        String stringaFile = leggiDatiDaFileJSON();
        return convertiJsonInOggetti(stringaFile);
    }

    private List<Poligono> convertiJsonInOggetti(String testoFile) {
        List<Poligono> listaPoligoni = new Gson().fromJson(testoFile, new TypeToken<List<Poligono>>() {
        }.getType());
        for (Poligono poligono : listaPoligoni) {
            Log.d(TAG, "Lista Poligoni : " + poligono.toString());
        }
        return listaPoligoni;
    }

    private String leggiDatiDaFileJSON() {
        Log.d(TAG, "-- eseguo leggiDatiDaFileJson");
        // -- per cambiare mappa modificare nomeFile.json
        InputStream is = getResources().openRawResource(R.raw.mappaunibasnuova1);
        String testo = null;
        try {
            int dimensionefile = is.available();
            byte[] buffer = new byte[dimensionefile];
            is.read(buffer);
            is.close();
            testo = new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "testo letto : " + testo.toString());
        return testo;
    }
}
