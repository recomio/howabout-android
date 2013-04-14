package io.recom.howabout.category.music.fragment;

import io.recom.howabout.R;
import io.recom.howabout.category.adult.fragment.ImageListFragment;
import io.recom.howabout.category.adult.fragment.TestFragment;

import com.viewpagerindicator.TabPageIndicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import roboguice.fragment.RoboFragment;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.fragment_category_wrap)
public class MusicCategoryWrapFragment extends RoboFragment {
	
	private static final String[] TAB_TITLES = new String[] { "SEARCH", "RECOMMEND", "PLAYLIST", "PLAYING" };
	
	@InjectView(R.id.pager)
	private ViewPager pager;
	
	@InjectView(R.id.indicator)
	private TabPageIndicator indicator;
	
	// Avoid non-default constructors in fragments: use a default constructor plus Fragment#setArguments(Bundle) instead.
	protected String category;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		FragmentPagerAdapter adapter = new TabFragmentPagerAdapter(this, category);
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);
		
		Log.i("MusicCagegoryWrapFragment", "onActivityCreated()");
	}
	
	@Override
	public void onStart() {
        super.onStart();
        Log.i("MusicCagegoryWrapFragment", "onStart()");
    }

    @Override
	public void onStop() {
        super.onStop();
        Log.i("MusicCagegoryWrapFragment", "onStop()");
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		this.category = bundle.getString("category");
		
		return inflater.inflate(R.layout.fragment_category_wrap, container, false);
	}
	
	
	class TabFragmentPagerAdapter extends FragmentPagerAdapter {
		private String category;
		
		public TabFragmentPagerAdapter(Fragment fragment, String category) {
			super(fragment.getChildFragmentManager());
			this.category = category;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment;
			
			if (position == 0) {
				fragment =  new ImageListFragment();
			} else {
				fragment = new TestFragment();
			}
			
			Bundle bundle = new Bundle();
			bundle.putString("category", this.category);
			bundle.putString("tab", "random");
			fragment.setArguments(bundle);
			
			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TAB_TITLES[position % TAB_TITLES.length];
		}

		@Override
		public int getCount() {
			return TAB_TITLES.length;
		}
	}

}
