package br.com.helptree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity  implements OnMapReadyCallback  {

    //VARIAVERIS

    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1 ;
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey" ;
    public static MapView mapa;

    private Uri uriImage;

    private StorageReference myStorageRef;
    private DatabaseReference myDataBaseRef;
    private FirebaseFirestore myFireStoreRef;

    private ImageView imageTree;

    SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");
    Date data = new Date();

    EditText txtID, txtNome, txtCientifico, txtFamilia, txtData,txtCEP, txtEndereco, txtNumero;

    public static double latitude, longitude;

    public String imageUrl = null;

    byte[] dadosImagem;

    public static LatLng local = new LatLng(latitude, longitude);


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        txtNome = findViewById(R.id.txtNome);  //ATRIBUI O COMPONENTE A VARIAVEL ATRAVÉS DO ID
        txtCientifico = findViewById(R.id.txtCientifico);
        txtFamilia = findViewById(R.id.txtFamilia);
        txtData = findViewById(R.id.txtData);
        txtCEP = findViewById(R.id.txtCEP);
        txtEndereco = findViewById(R.id.txtEndereco);
        txtNumero = findViewById(R.id.txtNumero);
        txtID = findViewById(R.id.txtID);


        imageTree = findViewById(R.id.imageTree);

        myStorageRef = FirebaseStorage.getInstance().getReference("arvores");
        myDataBaseRef = FirebaseDatabase.getInstance().getReference("arvores");
        myFireStoreRef = FirebaseFirestore.getInstance();

        //CHAMA A FUNÇÃO QUE TRAS OS DADOS DA ARVORE
        getDadosArvore();

        //INICIALIZA MAPA

        Bundle mapaBundle = null;

        if (savedInstanceState != null) {
            mapaBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapa = (MapView) findViewById(R.id.mapView);

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );
        } else {

            mapa.onCreate(mapaBundle);

            mapa.getMapAsync(this);

        }
    }

    //BUSCA OS DADOS NO BD

    public void getDadosArvore(){

        //CAPTURA OS DADOS DA ARVORE NO FIREBASE E PASSA PARA OS CAMPOS

        DocumentReference docRef = myFireStoreRef.collection("arvores").document("IqSLo3zsySex6tKmebNn");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Map<String, Object> dados;

                        dados = document.getData();

                        txtID.setText(dados.get("ID").toString());
                        txtNome.setText(dados.get("Nome").toString());
                        txtCientifico.setText(dados.get("Cientifico").toString());
                        txtFamilia.setText(dados.get("Familia").toString());
                        txtData.setText(dados.get("Data").toString());
                        txtCEP.setText(dados.get("CEP").toString());
                        txtEndereco.setText(dados.get("Endereco").toString());
                        txtNumero.setText(dados.get("Numero").toString());

                        //BUSCA A IMAGEM DO BD

                        getIMGArvore(dados.get("IMG").toString());


                    } else {
                        Toast.makeText(HomeActivity.this, "No such document", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "get failed with ", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //BUSCA IMAGEM NO BD
    public void getIMGArvore(String url){

        String teste = url;

        myDataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Picasso.get().load(teste).into(imageTree);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // we are showing that error message in toast
                Toast.makeText(HomeActivity.this, "Error Loading Image", Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mapa = findViewById(R.id.mapView);

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapa.getMapAsync(this);
            } else {
                Toast.makeText(this, "Falha na Permissão", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //CLICK QUE LEVA AO MAPA MUNDI

    public void worldMap(View v){

        //VARIAVEL QUE RECEBE SEGUNDA TELA

        Intent worldMapa =  new Intent(this, MapActivity.class);

        //PASSANDO PARAMETROS PARA SEGUNDA TELA

        Bundle parametros = new Bundle();

        parametros.putString("alter", "alter");  //PRIMEIRO PAREMETRO PASSADO
        worldMapa.putExtras(parametros);  //PASSA PARA A TELA 2 OS PARAMETROS.

        //Chama segunda tela

        startActivity(worldMapa);

    }

    //CLICK QUE LEVA AO PERFIL

    public void profileClick(View v){

        Intent  profile = new Intent(this, AccountActivity.class );

        startActivity(profile);

    }

    //CLICK PERFIL ARVORE

    public void btnTreeClick(View v){


    }

    //METODOS PARA SELECIONAR IMAGEM DA GALERIA

    public void addTreeClick(View v){

       openSeletorArquivos();

    }

    public void deleteTreeClick(View v){

        myFireStoreRef.collection("arvores").document(txtID.getText().toString())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(HomeActivity.this, "Arvore deletada", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "Não encontrou a arvore", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void openSeletorArquivos(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //APÓS SELECIONAR A IMAGEM CAI NESTE MÉTODO PARA RECUPERAR DADOS E MOSTRAR NO CAMPO

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            Bitmap imagem = null;

            uriImage = data.getData();
            //imageTree.setImageURI(uriImage);


            try {
                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImage );

                if ( imagem != null ) {

                    //Mostra a imagem na tela
                    imageTree.setImageBitmap(imagem);

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    dadosImagem = baos.toByteArray();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //METODOS PARA SALVAR NO BANCO DE DADOS


    public void btnSalvarClick(View v){

        uploadImgArvore();

    }

    private void uploadImgArvore(){

        if(uriImage != null){

            //salvar imagem firebase

            StorageReference imagemRef = myStorageRef.child( System.currentTimeMillis() + "." + getFileExtension(uriImage));

            UploadTask uploadTask = imagemRef.putBytes( dadosImagem );

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override

                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //CAPTURO O URL DA IMAGEM AO FAZER O UPLOAD
                                    imageUrl = uri.toString();

                                    //CHAMO A FUNÇÃO PARA SALVAR OS DADOS DA ARVORE E PASSO A URL

                                    savedDadosArvore(imageUrl);

                                }
                            });
                        }
                    }

                }

            }).addOnFailureListener(new OnFailureListener() {

                @Override

                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(HomeActivity.this,

                            "Erro ao fazer upload da imagem",

                            Toast.LENGTH_SHORT).show();

                }

            });

        }else{
            Toast.makeText(this,"Nenhuma Imagem Selecionada", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //SALVA OS DADOS DAS ARVORES

    private void savedDadosArvore(String urlImage){

        String url = urlImage;

        System.out.println(url);

        String dataFormatada = formataData.format(data);


        txtNome = findViewById(R.id.txtNome);  //ATRIBUI O COMPONENTE A VARIAVEL ATRAVÉS DO ID
        txtCientifico = findViewById(R.id.txtCientifico);
        txtFamilia = findViewById(R.id.txtFamilia);
        txtData = findViewById(R.id.txtData);
        txtCEP = findViewById(R.id.txtCEP);
        txtEndereco = findViewById(R.id.txtEndereco);
        txtNumero = findViewById(R.id.txtNumero);


        // PASSA DADOS PRO FIREBASE

        // Create a new tree with a first, middle, and last name
        Map<String, Object> arvore = new HashMap<>();
        arvore.put("Nome", txtNome.getText().toString());
        arvore.put("Cientifico", txtCientifico.getText().toString());
        arvore.put("Familia", txtFamilia.getText().toString());
        arvore.put("Data", dataFormatada);
        arvore.put("CEP", txtCEP.getText().toString());
        arvore.put("Endereco", txtEndereco.getText().toString());
        arvore.put("Numero", txtNumero.getText().toString());
        arvore.put("IMG", url);


        // Adiciona um novo documento com um ID gerado
        myFireStoreRef.collection("arvores")
                .add(arvore)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(HomeActivity.this, "Arvore salva com ID: " + documentReference.getId(), Toast.LENGTH_LONG).show();

                        //PASSA O ID PARA A TABELA DA ARVORE

                        Map<String, Object> id = new HashMap<>();

                        id.put("ID", documentReference.getId());

                        myFireStoreRef.collection("arvores").document(documentReference.getId()).update(id);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "Erro ao tentar salvar.", Toast.LENGTH_LONG).show();
                    }
                });


    }


    //CINFIGURAÇÃOES MAPA

    @Override
    public void onMapReady(@NonNull GoogleMap map) {

        map.clear();  //LIMPA MARCADORES QUE JA EXISTEM PARA PODER ATUALIZAR O GPS A CADA 2 SEGUNDOS

        local = new LatLng(-25.513994, -49.286515);

        map.addMarker(new MarkerOptions().position(local).title("Você"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 15f));
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

        Bundle mapaBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if(mapaBundle == null){
            mapaBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapaBundle);
        }
        mapa.onSaveInstanceState(mapaBundle);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapa.onLowMemory();
    }

}
