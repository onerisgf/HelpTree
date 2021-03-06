package br.com.helptree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity  implements OnMapReadyCallback  {

    //VARIAVERIS

    private int GPS_REQUEST_CODE = 9001;
    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1 ;
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey" ;
    public static MapView mapa;

    private Uri uriImage;

    private  StorageReference myStorageRef;
    private  DatabaseReference myDataBaseRef;
    private  FirebaseFirestore myFireStoreRef;

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

        txtNome = findViewById(R.id.txtNome);  //ATRIBUI O COMPONENTE A VARIAVEL ATRAV??S DO ID
        txtCientifico = findViewById(R.id.txtCientifico);
        txtFamilia = findViewById(R.id.txtFamilia);
        txtData = findViewById(R.id.txtData);
        txtCEP = findViewById(R.id.txtCEP);
        txtEndereco = findViewById(R.id.txtEndereco);
        txtNumero = findViewById(R.id.txtNumero);
        txtID = findViewById(R.id.txtID);

        txtID.setKeyListener(null);  //DEIXA INATIVO PARA EDI????O
        txtData.setKeyListener(null);


        imageTree = findViewById(R.id.imageTree);

        myStorageRef = FirebaseStorage.getInstance().getReference("arvores");
        myDataBaseRef = FirebaseDatabase.getInstance().getReference("arvores");
        myFireStoreRef = FirebaseFirestore.getInstance();


        //CAPTURA OS DADOS OBTIDOS DA TELA DE DO WORD MAP

        Intent pagina = getIntent();
        Bundle params = pagina.getExtras();

        String idTree = "wUGLCjRveUA42OvYsipn";  //ARVORE DE INICIO PADR??O

        //CASO HAJA PARAMETROS

        if (params != null) {

            idTree = params.getString("id");

        }


        //CHAMA A FUN????O QUE TRAS OS DADOS DA ARVORE


        getDadosArvore(idTree);

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

            //CRIA O MAPA
            mapa.onCreate(mapaBundle);

        }


    }

    //BUSCA OS DADOS NO BD

    public void getDadosArvore(String idTree){

        DocumentReference docRef = myFireStoreRef.collection("arvores").document(idTree);

        //CAPTURA OS DADOS DA ARVORE NO FIREBASE E PASSA PARA OS CAMPOS

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
                        latitude = Double.parseDouble(dados.get("Latitude").toString());
                        longitude = Double.parseDouble(dados.get("Longitude").toString());

                        //BUSCA A IMAGEM DO BD

                        getIMGArvore(dados.get("IMG").toString());

                        //CHAMA O MAPA

                        mapa.getMapAsync(HomeActivity.this);


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
                Toast.makeText(this, "Falha na Permiss??o", Toast.LENGTH_SHORT).show();
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

    //METODOS PARA ADICIONAR IMAGEM

    public void addTreeClick(View v){

       openSeletorArquivos();

    }

    //METODO PARA ATUALIZAR ARVORE

    public void editTreeClick(View v){

        Map<String,Object> dadosTree = new HashMap<>();

        dadosTree.put("Nome", txtNome.getText().toString());
        dadosTree.put("Cientifico", txtCientifico.getText().toString());
        dadosTree.put("Familia", txtFamilia.getText().toString());
        dadosTree.put("CEP", txtCEP.getText().toString());

        myFireStoreRef.collection("arvores").document(txtID.getText().toString()).update(dadosTree)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(HomeActivity.this,"Atualizado com Sucesso!", Toast.LENGTH_SHORT).show();
                    }
                });



    }

    //METODO DELETA ARVORE

    public void deleteTreeClick(View v){

        final Boolean[] verifica = {false};

        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);

        msgBox.setTitle("Voc?? Tem certeza que deseja deletar esta arvore?");

        msgBox.setPositiveButton("Deletar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                verifica[0] = true;

            }
        });

        msgBox.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {


            }
        });

        msgBox.show();


        if(verifica[0] == true) {

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
                            Toast.makeText(HomeActivity.this, "N??o encontrou a arvore", Toast.LENGTH_LONG).show();
                        }
                    });

        }
    }

    //METODO PARA BUSCAR ARVORES

    public void pesquisaTreeClick(View v){

        showModalBusca();

    }

    //ABRE A MODAL PARA INSERIR O ID DE BUSCA

    public void showModalBusca(){

        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);

        final EditText txtBuscaTree = new EditText(HomeActivity.this);

        msgBox.setMessage("Insira o ID:");
        msgBox.setTitle("Buscar Arvore");

        msgBox.setView(txtBuscaTree);

        msgBox.setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String txtBusca = txtBuscaTree.getText().toString();

                getDadosArvore(txtBusca);

            }
        });

        msgBox.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        msgBox.show();
    }


    //METODO DE SELETOR DE ARQUIVOS

    private void openSeletorArquivos(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //AP??S SELECIONAR A IMAGEM CAI NESTE M??TODO PARA RECUPERAR DADOS E MOSTRAR NO CAMPO

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

                    txtID.getText().clear(); //zera as variaveis
                    txtNome.getText().clear();
                    txtCientifico.getText().clear();
                    txtFamilia.getText().clear();
                    txtData.getText().clear();
                    txtCEP.getText().clear();
                    txtEndereco.getText().clear();
                    txtNumero.getText().clear();


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

                                    //CHAMO A FUN????O PARA SALVAR OS DADOS DA ARVORE E PASSO A URL

                                    savedDadosArvore(imageUrl);

                                }
                            });
                        }
                    }

                }

            }).addOnFailureListener(new OnFailureListener() {

                @Override

                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(HomeActivity.this,"Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();

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


        txtNome = findViewById(R.id.txtNome);  //ATRIBUI O COMPONENTE A VARIAVEL ATRAV??S DO ID
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

        String endereco = txtEndereco.getText().toString() + ", " + txtNumero.getText().toString();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {

            List<Address> addressList = geocoder.getFromLocationName(endereco, 1);

            if(addressList.size() > 0){
                Address address = addressList.get(0);

                Double getLatitude = address.getLatitude();

                arvore.put("Latitude", getLatitude.toString());

                Double getLongitude = address.getLongitude();

                arvore.put("Longitude", getLongitude.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


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



    //CINFIGURA????OES MAPA

    @Override
    public void onMapReady(@NonNull GoogleMap map) {

        map.clear();  //LIMPA MARCADORES QUE JA EXISTEM PARA PODER ATUALIZAR O GPS A CADA 2 SEGUNDOS

        local = new LatLng(latitude, longitude);

        String nomeTree = txtNome.getText().toString();

        int height = 80;
        int width = 80;
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.logo_tree);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        map.addMarker(new MarkerOptions().position(local).title(nomeTree).icon(smallMarkerIcon));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 15f));
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
    public void onLowMemory() {
        super.onLowMemory();
        mapa.onLowMemory();
    }

}
