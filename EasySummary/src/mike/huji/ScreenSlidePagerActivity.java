package mike.huji;

import com.viewpagerindicator.CirclePageIndicator;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ScreenSlidePagerActivity extends FragmentActivity {
    
	/**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    
    /**
     * Other members
     */
    private CirclePageIndicator titleIndicator;
    private Button okButton;
    private SharedPreferences sharedPref;
        
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide_pager);
        //Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        //Instantiate a page indicators
        titleIndicator = (CirclePageIndicator)findViewById(R.id.circles);
        titleIndicator.setViewPager(mPager);
        okButton = (Button) findViewById(R.id.ok_btn);
        okButton.setVisibility(View.GONE);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        titleIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            	if (position == 0 || position == 1) okButton.setVisibility(View.GONE);
            	else okButton.setVisibility(View.VISIBLE);
            }
        });
                
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//Never show again
            	SharedPreferences.Editor editor = sharedPref.edit();
            	editor.putBoolean("isShowWelcome", false);
            	editor.commit();
            	//Log.e("TAG","###$$$: "+sharedPref.getBoolean("isShowWelcome", true));
            	//Back pressed 
            	onBackPressed();
            }
        });
        
	}
	
	
    /**
     * A simple pager adapter that represents NUM_PAGES {@link ScreenSlidePageFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ScreenSlidePageFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
    
}