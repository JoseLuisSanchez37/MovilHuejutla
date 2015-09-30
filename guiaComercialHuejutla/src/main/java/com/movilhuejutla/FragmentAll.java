package com.movilhuejutla;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView.OnHeaderClickListener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentAll extends Fragment implements OnHeaderClickListener, OnItemClickListener, InterfaceAsyncTask{

	public static final String TAG = "com.movilhuejutla.FragmentAll";
	public StickyListHeadersListView stickyList;	
	public AdapterAllNegs adapter;
	CargarNegocios asyncTask = new CargarNegocios();
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		asyncTask.delegate = this;
		asyncTask.execute(activity.getApplicationContext());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.layout_fragment_all, container,false);
		stickyList = (StickyListHeadersListView) view.findViewById(R.id.list_sticky);
		stickyList.setOnHeaderClickListener(this);	
		stickyList.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onHeaderClick(StickyListHeadersListView l, View header,
			int itemPosition, long headerId, boolean currentlySticky) { }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Negs neg = new Negs();
		neg = (Negs) parent.getItemAtPosition(position);
		if(neg.pagado == 1){
			Intent intent = new Intent(getActivity().getApplicationContext(), ActivityDescripcionNegocio.class);
			intent.putExtra("id_negocio", id);
			intent.putExtra("navegacion", ActivityCategorias.FROM_ORDENADOS);
			startActivity(intent);
		}else{
			String str = DB_F.getInstance(getActivity().getApplicationContext()).getPhone(neg.id);
			if(str != null && !str.isEmpty()){
				Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+str));
				startActivity(call);
			}
		}	
	}

	@Override
	public void AsyncTaskDatosCargados(ArrayList<Negs> array) {
		adapter = new AdapterAllNegs(getActivity().getApplicationContext(), array);
		stickyList.setAdapter(adapter);
	}
	
}
