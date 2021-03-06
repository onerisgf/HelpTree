package br.com.helptree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static double latitude, longitude;

    private  StorageReference myStorageRef;
    private DatabaseReference myDataBaseRef;
    private FirebaseFirestore myFireStoreRef;

    List<Map<String,Object>> dados = new ArrayList<Map<String,Object>>();

    MapView mapa;

    EditText txtBuscaTree;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        txtBuscaTree = findViewById(R.id.txtIDTree);

        myStorageRef = FirebaseStorage.getInstance().getReference("arvores");
        myDataBaseRef = FirebaseDatabase.getInstance().getReference("arvores");
        myFireStoreRef = FirebaseFirestore.getInstance();

        getTress();


        mapa = (MapView) findViewById(R.id.mapWorld);


        Bundle mapaBundle = null;


        mapa.onCreate(mapaBundle);


        //CAPTURA OS DADOS OBTIDOS DA TELA DE LOGIN

        Intent pagina = getIntent();
        Bundle params = pagina.getExtras();

        String usuarioLogado = params.getString("login");
        String alter = params.getString("alter");

        if(alter == null) {


            Toast.makeText(this, "Usu??rio Logado: " + usuarioLogado, Toast.LENGTH_LONG).show();

        }


    }

    //CAPTURO OS DADOS DE TODAS AS ARVORES

    public void getTress(){

        myFireStoreRef.collection("arvores")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            dados.add(new HashMap<String,Object>());

                            //PASSO OS DADOS PARA DENTRO DA LISTA

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                dados.add(document.getData());

                            }

                            for(int i = 1; i < dados.size(); i++){

                                System.out.println(dados.get(i));

                            }


                            mapa.getMapAsync(MapActivity.this);


                        } else {

                            Toast.makeText(MapActivity.this, "Erro ao tentar dar getting nos documentos.", Toast.LENGTH_LONG).show();


                        }
                    }
                });

    }

    //CLICK BUSCA ARVORE

    public void btnBuscaTree(View v){

        String id = txtBuscaTree.getText().toString();

        //VARIAVEL QUE RECEBE SEGUNDA TELA

        Intent Cadastro =  new Intent(this, HomeActivity.class);

        //PASSANDO PARAMETROS PARA SEGUNDA TELA

        Bundle parametros = new Bundle();

        parametros.putString("id", id);  //PRIMEIRO PAREMETRO PASSADO
        Cadastro.putExtras(parametros);  //PASSA PARA A TELA 2 OS PARAMETROS.

        //Chama segunda tela

        startActivity(Cadastro);

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

    //CINFIGURA????OES MAPA

    @Override
    public void onMapReady(@NonNull GoogleMap map) {

        map.clear();

        for(int i = 1; i < dados.size(); i++) {

            latitude = Double.parseDouble(dados.get(i).get("Latitude").toString());
            longitude = Double.parseDouble(dados.get(i).get("Longitude").toString());

            LatLng local = new LatLng(latitude, longitude);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 13f));


            int height = 80;
            int width = 80;
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.logo_tree);
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

            map.addMarker(
                    new MarkerOptions()
                            .position(local)
                            .title(dados.get(i).get("Nome").toString())
                            .snippet(dados.get(i).get("ID").toString())
                            .icon(smallMarkerIcon)


            );


            map.setOnMarkerClickListener(

            new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    txtBuscaTree.setText(marker.getSnippet());

                    System.out.println(marker.getTitle());
                    System.out.println(marker.getSnippet());

                    return false;
                }
            });

        }


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
