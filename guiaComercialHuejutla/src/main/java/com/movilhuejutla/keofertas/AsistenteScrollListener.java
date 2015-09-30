package com.movilhuejutla.keofertas;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;

public abstract class AsistenteScrollListener extends OnScrollListener {

    private int totalAnterior = 0;
    private boolean cargando = true;
    private int visibleThreshold = 5;
    int primerItemVisible, itemsVisibles, totalItems;

    private LinearLayoutManager mLinearLayoutManager;

    public AsistenteScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dy < 0){ mostrarActionButton(); } else {  ocultarActionButton(); }

        itemsVisibles = recyclerView.getChildCount();
        totalItems = mLinearLayoutManager.getItemCount();
        primerItemVisible = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (cargando) {
            if (totalItems > totalAnterior) {
                cargando = false;
                totalAnterior = totalItems;
            }
        }
        if (!cargando && (totalItems - itemsVisibles)
                <= (primerItemVisible + visibleThreshold)) {
            cargarMas();
            cargando = true;
        }
    }

    public abstract void cargarMas();
    public abstract void ocultarActionButton();
    public abstract void mostrarActionButton();
}