package com.movilhuejutla;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AdapterTabsPager extends FragmentPagerAdapter{

	private Bundle bundle;
	private int num_pages = 0;
	
	public AdapterTabsPager(FragmentManager fm, long id_neg, long id_cat, String nom_cat, int mod_nav, int pages) {
		super(fm);
		bundle = new Bundle();
		bundle.putLong("id_negocio", id_neg);
		bundle.putLong("id_categoria", id_cat);
		bundle.putString("nombre_categoria", nom_cat);
		bundle.putInt("navegacion", mod_nav);
		num_pages = pages;
	}

	@Override
	public Fragment getItem(int index) {
		switch (index){
		case 0:
			FragmentDescription fragment = new FragmentDescription();
			fragment.setArguments(bundle);
			return fragment;
		
		case 1:
			FragmentNews fragment1 = new FragmentNews();
			fragment1.setArguments(bundle);
			return fragment1;
			
		case 2:
			FragmentFacebook fragment2 = new FragmentFacebook();
			fragment2.setArguments(bundle);
			return fragment2;
		}
		
		return null;
	}

	@Override
	public int getCount() {
		return num_pages;
	}

}
