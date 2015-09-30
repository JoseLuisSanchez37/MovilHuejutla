package com.movilhuejutla.keofertas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.view.MaterialListView;
import com.movilhuejutla.R;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.OnRefreshListener;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.software.shell.fab.ActionButton;

public class Inicio extends ActionBarActivity implements OnRefreshListener {

    private SwipyRefreshLayout mSwipyRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private MaterialListView mListView;
    private ActionButton actionButton;
    private Context contexto;

    private static final String TAG_ACTIVITY = "Inicio";

    //contador de items
    private int contadorAnuncios = 0;
    //el limite total de items que se visualizaran en el ListView
    private int limiteTotalAnuncios = 200;
    //la cantidad de items que se descarguen del servidor
    private int limiteAnunciosPorCarga = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_layout_inicio);

        mListView = (MaterialListView) findViewById(R.id.k_material_listview);
        mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        actionButton = (ActionButton) findViewById(R.id.action_button_nuevoAnuncio);
        mLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mLayoutManager);
        mSwipyRefreshLayout.setOnRefreshListener(this);
        contexto = this;

        //Agregamos los primero 10 items al layout
        cargarAnuncios();

        //Agregamos el escuchadores
        mListView.setOnScrollListener(new AsistenteScrollListener(mLayoutManager) {
            @Override
            public void cargarMas() {
                cargarAnuncios();
            }

            @Override
            public void mostrarActionButton(){
                actionButton.setShowAnimation(ActionButton.Animations.JUMP_FROM_DOWN);
                actionButton.show();
            }

            @Override
            public void ocultarActionButton(){
                actionButton.setHideAnimation(ActionButton.Animations.JUMP_TO_RIGHT);
                actionButton.hide();
            }
        });

    }

    private void cargarAnuncios() {
        //Establecer direccion de actualizacion y mostrar animacion
        mSwipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        mSwipyRefreshLayout.setRefreshing(true);
        refresh();

        for (int i = 0; contadorAnuncios <= limiteTotalAnuncios; i++) {
            if( i < limiteAnunciosPorCarga) {
                SmallImageCard card = new SmallImageCard(this);
                card.setDescription("Item:::" +contadorAnuncios);
                card.setTitle("Bienvenido Item Numero--> " + contadorAnuncios);
                card.setDrawable(R.drawable.ic_launcher);
                mListView.add(card);
                contadorAnuncios++;
            }else{
                break;
            }
        }
    }

    private void refresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Diseminar actualizacion despues de 2 segundos
                Inicio.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwipyRefreshLayout.setRefreshing(false);
                        //Asignamos nuevamente la direccion de actualizacion hacia arriba TOP
                        mSwipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);
                    }
                });
            }
        }, 2000);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        //Pull hacia arriba
        refresh();

        //Aqui mandamos a traer a los ultimos registros insertados.....
        //...
    }

    public void nuevoAnuncio(View v){
        Intent nuevo = new Intent(this, NuevoAnuncio.class);
        startActivity(nuevo);
    }


    @Override
    protected void onStart(){
        super.onStart();
        Log.v(TAG_ACTIVITY, "onStart()");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.v(TAG_ACTIVITY, "onResume()");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.v(TAG_ACTIVITY, "onPause()");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.v(TAG_ACTIVITY, "onStop()");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.v(TAG_ACTIVITY, "onRestart()");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.v(TAG_ACTIVITY, "onDestroy()");
    }

}
