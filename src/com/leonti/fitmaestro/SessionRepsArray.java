package com.leonti.fitmaestro;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class SessionRepsArray {

	private final Context mCtx;
	private ExcercisesDbAdapter mDbHelper;

	private Cursor mRepsForSessionCursor;

	// free - without sets detail predefined
	private Cursor mFreeRepsForSessionCursor;

	private Long mSessionId;
	private Long mExerciseId;
	private Long mSessionsConnectorId;

	ArrayList<HashMap<String, String>> mSessionRepsList = new ArrayList<HashMap<String, String>>();

	public SessionRepsArray(Context ctx, Long sessionId, Long exerciseId,
			Long sessionsConnectorId) {
		this.mCtx = ctx;
		mSessionId = sessionId;
		mExerciseId = exerciseId;
		mSessionsConnectorId = sessionsConnectorId;

		mDbHelper = new ExcercisesDbAdapter(mCtx);
		mDbHelper.open();
	}

	public ArrayList<HashMap<String, String>> getRepsArray() {

		// converting cursor(s) data to array list
		mSessionRepsList.clear();
		Log.i("SESSIONS CONNECTOR ID: ", mSessionsConnectorId.toString());
		Log.i("EXERCISE ID: ", mExerciseId.toString());
		Log.i("SESSION ID: ", mSessionId.toString());
		// if it's not 0 - session was created from set so we can get planned
		// reps for this exercise
		if (mSessionsConnectorId != Long.valueOf(0)) {
			Cursor sessionReps = mDbHelper
					.fetchRepsForSessionConnector(mSessionsConnectorId);
			Log.i("DYG: ", "MYG");
			sessionReps.moveToFirst();
			for (int i = 0; i < sessionReps.getCount(); i++) {

				Long sessionDetailId = sessionReps.getLong(sessionReps
						.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID));

				Long planned_reps = sessionReps.getLong(sessionReps
						.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_REPS));

				Float planned_percentage = sessionReps
						.getFloat(sessionReps
								.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_PERCENTAGE));

				HashMap<String, String> item = new HashMap<String, String>();

				// fetch done from log table and fill it

				Cursor sessionDoneReps = mDbHelper.fetchDoneSessionReps(
						mSessionId, sessionDetailId);
				if (sessionDoneReps.getCount() > 0) {
					String id = sessionDoneReps
							.getString(sessionDoneReps
									.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID));

					String reps = sessionDoneReps
							.getString(sessionDoneReps
									.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_REPS));

					String weight = sessionDoneReps
							.getString(sessionDoneReps
									.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_WEIGHT));

					item.put("id", id);
					item.put("reps", reps);
					item.put("weight", weight);

				} else {
					item.put("id", null);
					item.put("reps", "not_done");
					item.put("weight", "not_done");
				}

				item.put("session_detail_id", sessionDetailId.toString());
				item.put("planned_reps", planned_reps.toString());
				item.put("planned_weight", planned_percentage.toString());
				mSessionRepsList.add(item);

				sessionReps.moveToNext();
			}
		}

		mFreeRepsForSessionCursor = mDbHelper.fetchFreeSessionReps(mSessionId,
				mExerciseId);
		mFreeRepsForSessionCursor.moveToFirst();
		for (int i = 0; i < mFreeRepsForSessionCursor.getCount(); i++) {

			Long repsId = mFreeRepsForSessionCursor
					.getLong(mFreeRepsForSessionCursor
							.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_ROWID));

			Long reps = mFreeRepsForSessionCursor
					.getLong(mFreeRepsForSessionCursor
							.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_REPS));

			Float weight = mFreeRepsForSessionCursor
					.getFloat(mFreeRepsForSessionCursor
							.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_WEIGHT));

			HashMap<String, String> item = new HashMap<String, String>();

			item.put("id", repsId.toString());
			item.put("reps", reps.toString());
			item.put("weight", weight.toString());
			item.put("planned_reps", "extra");
			item.put("planned_weight", "extra");
			mSessionRepsList.add(item);

			mFreeRepsForSessionCursor.moveToNext();
		}

		return mSessionRepsList;
	}
	
	public void drawTable(Activity activity, View view, ArrayList<HashMap<String, String>> sessionRepsList, Long exType){
		TableLayout repsTable = (TableLayout) view
		.findViewById(R.id.reps_table);

		repsTable.removeViews(1, repsTable.getChildCount() - 1);

		// if 0 - own weight - don't show percentage values
		if (exType == Long.valueOf(0)) {
			repsTable.findViewById(R.id.x_col).setVisibility(View.GONE);
			repsTable.findViewById(R.id.planned_weight_col).setVisibility(
					View.GONE);
			repsTable.findViewById(R.id.x_done_col)
			.setVisibility(View.GONE);
			repsTable.findViewById(R.id.weight_col)
			.setVisibility(View.GONE);
		} else {
			repsTable.findViewById(R.id.x_col).setVisibility(View.VISIBLE);
			repsTable.findViewById(R.id.planned_weight_col).setVisibility(
					View.VISIBLE);
			repsTable.findViewById(R.id.x_done_col).setVisibility(
					View.VISIBLE);
			repsTable.findViewById(R.id.weight_col).setVisibility(
					View.VISIBLE);
		}

		for (int i = 0; i < sessionRepsList.size(); i++) {

			// Create a new row to be added.
			TableRow tr = new TableRow(activity);
			tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));


			LayoutParams plannedRepsTxtLP = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);				
			plannedRepsTxtLP.gravity = Gravity.CENTER;	
			LayoutParams xTxtLP = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);				
			xTxtLP.gravity = Gravity.CENTER;
			LayoutParams plannedWeightTxtLP = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);				
			plannedWeightTxtLP.gravity = Gravity.CENTER;

			LayoutParams repsTxtLP = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);				
			repsTxtLP.gravity = Gravity.CENTER;		
			LayoutParams xTxt2LP = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);				
			xTxt2LP.gravity = Gravity.CENTER;
			LayoutParams weightTxtLP = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);				
			weightTxtLP.gravity = Gravity.CENTER;

			// planned reps
			String plannedReps = sessionRepsList.get(i).get("planned_reps");
			TextView plannedRepsTxt = new TextView(activity);
			plannedRepsTxt.setText(plannedReps);
			plannedRepsTxt.setGravity(Gravity.CENTER);
			tr.addView(plannedRepsTxt, plannedRepsTxtLP);

			TextView xTxt = new TextView(activity);
			xTxt.setText("x");
			xTxt.setGravity(Gravity.CENTER);
			tr.addView(xTxt, xTxtLP);

			String plannedWeight = sessionRepsList.get(i).get(
					"planned_weight");
			TextView plannedWeightTxt = new TextView(activity);
			plannedWeightTxt.setText(plannedWeight);
			plannedWeightTxt.setGravity(Gravity.CENTER);
			tr.addView(plannedWeightTxt, plannedWeightTxtLP);

			// done reps
			String reps = sessionRepsList.get(i).get("reps");
			TextView repsTxt = new TextView(activity);
			repsTxt.setText(reps);
			repsTxt.setGravity(Gravity.CENTER);
			tr.addView(repsTxt, repsTxtLP);

			TextView xTxt2 = new TextView(activity);
			xTxt2.setText("x");
			xTxt2.setGravity(Gravity.CENTER);
			tr.addView(xTxt2, xTxt2LP);

			String weight = sessionRepsList.get(i).get("weight");
			TextView weightTxt = new TextView(activity);
			weightTxt.setText(weight);
			weightTxt.setGravity(Gravity.CENTER);
			tr.addView(weightTxt, weightTxtLP);

			// Add row to TableLayout.

			repsTable.addView(tr, new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			// if 0 - own weight - don't show percentage values
			if (exType == Long.valueOf(0)) {
				xTxt.setVisibility(View.GONE);
				plannedWeightTxt.setVisibility(View.GONE);
				xTxt2.setVisibility(View.GONE);
				weightTxt.setVisibility(View.GONE);
			}

		}
	}

}
