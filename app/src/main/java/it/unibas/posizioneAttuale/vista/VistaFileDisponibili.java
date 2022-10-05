package it.unibas.posizioneAttuale.vista;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import it.unibas.posizioneAttuale.Applicazione;
import it.unibas.posizioneAttuale.R;
import it.unibas.posizioneAttuale.activity.ActivityMostraFileDisponibili;
import it.unibas.posizioneAttuale.activity.ActivityPrincipale;
import it.unibas.posizioneAttuale.modello.Costanti;

public class VistaFileDisponibili extends Fragment {

    // -- logging
    public static final String TAG = ActivityPrincipale.class.getSimpleName();

    private StorageReference riferimentoStorage;
    private StorageReference riferimentoCartella;

    private ListView listaFileDisponibili; //implementazione con listView


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.vista_file_disponibili_listview, container, false); // x implementazione listView
        String aulaAttuale = (String) Applicazione.getInstance().getModello().getBean(Costanti.AULA_ATTUALE);
        //assegnazione componenti
        listaFileDisponibili = vista.findViewById(R.id.listaFileDisponibili);
        Log.d(TAG, "-- Aula attuale : " + aulaAttuale);
        //inizializzazione listaFileDisponibili
        AdapterListViewFileDisponibili adapterListViewFileDisponibili = new AdapterListViewFileDisponibili(new ArrayList<>(), new ArrayList<>());
        listaFileDisponibili.setAdapter(adapterListViewFileDisponibili);
        listaFileDisponibili.setOnItemClickListener(Applicazione.getInstance().getControlloVistaFileDisponibili().getAzioneSelezioneFile());
        visualizzaDati(aulaAttuale);
        return vista;


    }

    private void visualizzaDati(String aula) {
        Log.d(TAG, "-- visualizzaDati");
        riferimentoStorage = FirebaseStorage.getInstance().getReference();
        riferimentoCartella = riferimentoStorage.child(aula + "/");
        Log.d(TAG, "-- STORAGE : " + riferimentoStorage.getPath());
        Log.d(TAG, "-- CARTELLA : " + riferimentoCartella.getPath());
        List<String> listaNomiFile = new ArrayList<>();
        List<String> listaUriFile = new ArrayList<>();
        try {
            riferimentoCartella.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference storageReference : listResult.getItems()) {
                            listaNomiFile.add(storageReference.getName());
                        Log.d(TAG,"-- trovato file : " + storageReference.getName());
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG,"-- richiesta uri file : " + storageReference.getName());
                                listaUriFile.add(uri.toString());
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Applicazione.getInstance().getModello().putBean(Costanti.LISTA_URI_FILE,listaUriFile);
                                Log.d(TAG,"-- lista uri File in memoria : " + Applicazione.getInstance().getModello().getBean(Costanti.LISTA_URI_FILE));
                            }
                        });
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<ListResult>() {
                @Override
                public void onComplete(@NonNull Task<ListResult> task) {
                    Applicazione.getInstance().getModello().putBean(Costanti.LISTA_NOMI_FILE_DISPONIBILI, listaNomiFile);
                    ActivityMostraFileDisponibili activityMostraFileDisponibili = (ActivityMostraFileDisponibili) Applicazione.getInstance().getCurrentActivity();
                    VistaFileDisponibili vistaFileDisponibili = activityMostraFileDisponibili.getVistaFileDisponibili();
                    AdapterListViewFileDisponibili adapterListViewFileDisponibili = new AdapterListViewFileDisponibili(listaNomiFile, listaUriFile);
                    ListView listaFileDisponibili = vistaFileDisponibili.getView().findViewById(R.id.listaFileDisponibili);
                    listaFileDisponibili.setAdapter(adapterListViewFileDisponibili);
                    Log.d(TAG,"-- lista nomi File in memoria : " + Applicazione.getInstance().getModello().getBean(Costanti.LISTA_NOMI_FILE_DISPONIBILI));
                }
            });
            /*riferimentoCartella.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    Log.d(TAG, "-- listResult.getItems(); " + listResult.getItems());
                    for (StorageReference storageReference : listResult.getItems()) {
                        Log.d(TAG, "-- storageReference.getName(); " + storageReference.getName());
                        listaNomiFile.add(storageReference.getName());
                        Log.d(TAG, "-- listaNomiFile :  " + listaNomiFile.toString());
                    }
                    Applicazione.getInstance().getModello().putBean(Costanti.LISTA_NOMI_FILE_DISPONIBILI, listaNomiFile);
                    ActivityMostraFileDisponibili activityMostraFileDisponibili = (ActivityMostraFileDisponibili) Applicazione.getInstance().getCurrentActivity();
                    VistaFileDisponibili vistaFileDisponibili = activityMostraFileDisponibili.getVistaFileDisponibili();
                    AdapterListViewFileDisponibili adapterListViewFileDisponibili = new AdapterListViewFileDisponibili(listaNomiFile, new ArrayList<>());
                    ListView listaFileDisponibili = vistaFileDisponibili.getView().findViewById(R.id.listaFileDisponibili);
                    listaFileDisponibili.setAdapter(adapterListViewFileDisponibili);
                }
            });
            riferimentoCartella.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference storageReference:listResult.getItems()) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "-- uriFile :  " + uri.toString());
                                listaUriFile.add(uri.toString());
                            }
                        });
                    }
                    Applicazione.getInstance().getModello().putBean(Costanti.LISTA_URI_FILE,listaUriFile);
                    Log.d(TAG,"-- lista nomi File in memoria : " + Applicazione.getInstance().getModello().getBean(Costanti.LISTA_NOMI_FILE_DISPONIBILI));
                    Log.d(TAG,"-- lista uri File in memoria : " + Applicazione.getInstance().getModello().getBean(Costanti.LISTA_URI_FILE));
                }
            });
            /*riferimentoCartella.list(5).addOnCompleteListener(new OnCompleteListener<ListResult>() {
                @Override
                public void onComplete(@NonNull Task<ListResult> task) {
                    Log.d(TAG,"-- getResult.getItemsToString : "+task.getResult().getItems().toString());
                    StorageReference rifFile = task.getResult().getItems().get(0);
                    Log.d(TAG,"-- prova riferimentoFile : "+rifFile.toString());
                    rifFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String nomeFile=getFileName(uri);
                            Log.d(TAG,"-- prova converisone uri : "+nomeFile);
                        }
                    });

                }
            });
             */
            /*riferimentoCartella.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                @Override
                public void onComplete(@NonNull Task<ListResult> task) {
                    Log.d(TAG,"-- getItems : "+task.getResult().getItems().toString());
                    Log.d(TAG,"-- getPrefixes : "+task.getResult().getPrefixes().toString());
                    Log.d(TAG,"-- getResult : "+task.getResult());

                }
            });
            riferimentoCartella.child("img0.jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Log.d(TAG,"-- "+task.getResult().toString());
                }
            });
             */
        } catch (Exception e) {
            Log.d(TAG, "-- ECCEZIONE : " + e.getMessage());
        }


    }



}
