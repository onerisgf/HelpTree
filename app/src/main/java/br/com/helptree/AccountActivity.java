package br.com.helptree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

    }


    public void btnTreeClick(View v){

        //VARIAVEL QUE RECEBE SEGUNDA TELA

        Intent homeTree =  new Intent(this, HomeActivity.class);

        //Chama segunda tela

        this.startActivity(homeTree);

    }

    //CLICK QUE LEVA AO PERFIL

    public void profileClick(View v){



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

}
