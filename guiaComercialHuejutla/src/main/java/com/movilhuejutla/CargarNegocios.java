package com.movilhuejutla;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.os.AsyncTask;

public class CargarNegocios extends AsyncTask<Context, Void, ArrayList<Negs>>{

	public InterfaceAsyncTask delegate = null;
		
	protected void onPostExecute(ArrayList<Negs> array){
		delegate.AsyncTaskDatosCargados(array);
	}

	@Override
	protected ArrayList<Negs> doInBackground(Context... context) {
		ArrayList<Negs> array = null;
		while(true){
			if(!FlagsCopy.getFlagCopy(context[0])){
				FlagsCopy.setFlagDatabaseInUse(context[0], true);
				array = DB_MH.getInstance(context[0]).getAllNegs();
				array.addAll(DB_F.getInstance(context[0]).getAllNegs());
				Collections.sort(array, new Comparator<Negs>() {
				    @Override
					public int compare(Negs result1, Negs result2) {
				    	return result1.nombre.compareTo(result2.nombre);
				    }
				});
				FlagsCopy.setFlagDatabaseInUse(context[0], false);
				break;
			}	
		}
		return array;
	}

}
