package io.recom.howabout.category.adult.fragment;

import io.recom.howabout.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import roboguice.fragment.RoboFragment;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.fragment_test)
public class TestFragment extends RoboFragment {
	private String category;
	private String tab;
	
	@InjectView(R.id.textTest) TextView textTest;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		category = bundle.getString("category");
		tab = bundle.getString("tab");
		
		return inflater.inflate(R.layout.fragment_test, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		textTest.setText( category + ", " + tab );
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
}
