package com.movilhuejutla;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AdapterViewCategory extends FragmentPagerAdapter{

	public AdapterViewCategory(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {
		switch(arg0){
		case 0:
			FragmentCategory categorias = new FragmentCategory();
			return categorias;
	
		case 1:
			FragmentAll todos = new FragmentAll();
			return todos;
		}
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}

}
