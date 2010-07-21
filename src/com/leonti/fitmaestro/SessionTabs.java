package com.leonti.fitmaestro;

import com.leonti.fitmaestro.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class SessionTabs extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final TabHost tabHost = getTabHost();
		Resources res = getResources(); // Resource object to get Drawables

		Intent i_inprogress = new Intent(this, SessionsList.class);
		i_inprogress.putExtra("filter", "INPROGRESS");
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(
				getString(R.string.in_progress), res.getDrawable(R.drawable.tab_current)).setContent(
				i_inprogress.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		Intent i_done = new Intent(this, SessionsList.class);
		i_done.putExtra("filter", "DONE");
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(
				getString(R.string.done), res.getDrawable(R.drawable.tab_done)).setContent(
				i_done.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
	}
}
