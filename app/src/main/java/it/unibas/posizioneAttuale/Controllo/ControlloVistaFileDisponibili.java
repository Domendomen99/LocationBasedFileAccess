package it.unibas.posizioneAttuale.Controllo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unibas.posizioneAttuale.Applicazione;
import it.unibas.posizioneAttuale.R;
import it.unibas.posizioneAttuale.activity.ActivityMostraFileDisponibili;
import it.unibas.posizioneAttuale.modello.Costanti;
import it.unibas.posizioneAttuale.vista.VistaFileDisponibili;

public class ControlloVistaFileDisponibili {

    private String TAG = ControlloVistaFileDisponibili.class.getSimpleName();

    private AdapterView.OnItemClickListener azioneSelezioneFile = new AzioneSelezioneFile();

    public AdapterView.OnItemClickListener getAzioneSelezioneFile() {
        return azioneSelezioneFile;
    }

    private class AzioneSelezioneFile implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ActivityMostraFileDisponibili activityMostraFileDisponibili = (ActivityMostraFileDisponibili) Applicazione.getInstance().getCurrentActivity();
            List<String> listaUrifileDisponibili = (List<String>) Applicazione.getInstance().getModello().getBean(Costanti.LISTA_URI_FILE);
            List<String>listaFileDisponibili = (List<String>) Applicazione.getInstance().getModello().getBean(Costanti.LISTA_NOMI_FILE_DISPONIBILI);
            String uriFileSelezionato = "null";
            for (String uriFile:listaUrifileDisponibili) {
                if(uriFile.contains(listaFileDisponibili.get(i))){
                    Log.d(TAG,"-- trovata corrispondenza");
                    uriFileSelezionato = uriFile;
                }
            }
            if (uriFileSelezionato.equals("null")){
                Log.d(TAG,"-- errore controllo");
                return;
            }
            Applicazione.getInstance().getModello().putBean(Costanti.URI_FILE_SELEZIONATO,uriFileSelezionato);
            Intent intent = new Intent();
            intent.setType(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(uriFileSelezionato));
            activityMostraFileDisponibili.startActivity(intent);
        }
    }


    /*private class AzioneSelezioneFile implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ActivityMostraFileDisponibili activityMostraFileDisponibili = (ActivityMostraFileDisponibili) Applicazione.getInstance().getCurrentActivity();
            VistaFileDisponibili vistaFileDisponibili = activityMostraFileDisponibili.getVistaFileDisponibili();
            ListView listViewFileDisponibili = vistaFileDisponibili.getView().findViewById(R.id.listaFileDisponibili);
            int nFileselezionato = i;
            List<String> urifileDisponibili = (List<String>) Applicazione.getInstance().getModello().getBean(Costanti.LISTA_URI_FILE);
            String uriFileSelezionato = urifileDisponibili.get(i);
            Applicazione.getInstance().getModello().putBean(Costanti.URI_FILE_SELEZIONATO,uriFileSelezionato);
            Intent intent = new Intent();
            intent.setType(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(uriFileSelezionato));
            activityMostraFileDisponibili.startActivity(intent);
        }
    }

     */
}
