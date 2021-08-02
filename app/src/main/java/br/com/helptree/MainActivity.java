package br.com.helptree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    EditText txtLogin, txtSenha;   //CRIA DUAS VARIAVEIS DO TIPO EditText



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // PASSA DADOS PRO FIREBASE
        //FirebaseDatabase banco = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = banco.getReference(new Date().toString());
        //myRef.child("nome").setValue("Oneris");
        //myRef.child("sobrenome").setValue("Grunewald");
        //myRef.child("email").setValue("onerisgrunewald@gmail.com");


        txtLogin = findViewById(R.id.txtLogin);  //ATRIBUI O COMPONENTE A VARIAVEL ATRAVÉS DO ID
        txtSenha = findViewById(R.id.txtSenha);



    }

    public void login(View v){

        String senha = txtSenha.getText().toString();
        String login = txtLogin.getText().toString();

        if(senha.equals(login)){

            Toast.makeText(this, "Bem Vindo!", Toast.LENGTH_SHORT).show();

            //VARIAVEL QUE RECEBE SEGUNDA TELA

            Intent worldMapa =  new Intent(this, MapActivity.class);

            //PASSANDO PARAMETROS PARA SEGUNDA TELA

            Bundle parametros = new Bundle();

            parametros.putString("login", login);  //PRIMEIRO PAREMETRO PASSADO
            worldMapa.putExtras(parametros);  //PASSA PARA A TELA 2 OS PARAMETROS.

            //Chama segunda tela

            startActivity(worldMapa);


        }else{
            finish(); //ENCERRA O APP
        }

    }

    public void cadastrar(View view){

        //VARIAVEL QUE RECEBE SEGUNDA TELA

        Intent Home =  new Intent(this, AccountActivity.class);

        //Chama segunda tela

        startActivity(Home);

    }




}