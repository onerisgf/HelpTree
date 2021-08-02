package br.com.helptree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {



    MapView mapa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapa = (MapView) findViewById(R.id.mapWorld);


        Bundle mapaBundle = null;


        mapa.onCreate(mapaBundle);

        mapa.getMapAsync(this);

        //CAPTURA OS DADOS OBTIDOS DA TELA DE LOGIN



        Intent pagina = getIntent();
        Bundle params = pagina.getExtras();

        String usuarioLogado = params.getString("login");
        String alter = params.getString("alter");

        if(alter == null) {


            Toast.makeText(this, "Usuário Logado: " + usuarioLogado, Toast.LENGTH_LONG).show();

        }


    }

    //CLICK PERFIL ARVORE

    public void btnTreeClick(View v){

        //VARIAVEL QUE RECEBE SEGUNDA TELA

        Intent homeTree =  new Intent(this, HomeActivity.class);

        //Chama segunda tela

        startActivity(homeTree);

    }

    //CLICK QUE LEVA AO PERFIL

    public void profileClick(View v){

        Intent  profile = new Intent(this, AccountActivity.class );

        startActivity(profile);

    }

    public void worldMap(View v){



    }

    //CINFIGURAÇÃOES MAPA

    @Override
    public void onMapReady(@NonNull GoogleMap map) {



        map.clear();  //LIMPA MARCADORES QUE JA EXISTEM PARA PODER ATUALIZAR O GPS A CADA 2 SEGUNDOS


        map.addMarker(new MarkerOptions().position(HomeActivity.local).title("Você"));

        HomeActivity.local = new LatLng(-25.513945, -49.290528);

        map.addMarker(new MarkerOptions().position(HomeActivity.local).title("Você"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(HomeActivity.local, 15f));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapa.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapa.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapa.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapa.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapa.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState);

        Bundle mapaBundle = outState.getBundle(HomeActivity.MAPVIEW_BUNDLE_KEY);
        if(mapaBundle == null){
            mapaBundle = new Bundle();
            outState.putBundle(HomeActivity.MAPVIEW_BUNDLE_KEY, mapaBundle);
        }
        mapa.onSaveInstanceState(mapaBundle);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapa.onLowMemory();
    }
}
