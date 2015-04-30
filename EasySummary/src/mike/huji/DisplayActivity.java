package mike.huji;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.viewpagerindicator.CirclePageIndicator;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayActivity extends FragmentActivity {

	/**
     * The number of pages (wizard steps) to show in this demo.
     */
    public static int NUM_PAGES = 0;

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
    private final double SENSATIVITY_PARAM = 1;  //means detect all
    private CirclePageIndicator titleIndicator;
    private SharedPreferences sharedPref;
    private List<String> resultList = new ArrayList<String>();
    public static Map<Integer, String> textShowMap = new HashMap<Integer, String>();
    public static Map<Integer, String> textHideMap = new HashMap<Integer, String>();
        
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String uriPath = sharedPref.getString("file_to_open", null);
        resultList.clear();
        resultList = getCardsArray(uriPath, SENSATIVITY_PARAM, false);
        NUM_PAGES = resultList.size()/2;  //Always x*2...
        for (int i=0; i<resultList.size(); i+=2) {
        	textShowMap.put(i/2, resultList.get(i));
        	textHideMap.put(i/2, resultList.get(i+1));
        }
        //Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        //Instantiate a page indicators
        titleIndicator = (CirclePageIndicator)findViewById(R.id.circles);
        titleIndicator.setViewPager(mPager);
	}
	
    private boolean cheakHebrew(String inTemp) {//TODO
    	Pattern pattern= Pattern.compile("[א-ת]"); // dot/(:at start) patren
		Matcher matcher= pattern.matcher(inTemp);;
		return matcher.find();
	}

	//TODO get cards method first one is with blank and second without.
    private List<String> getCardsArray(String uriPath, double hidePrecent, boolean isTextRTL)  {
    	int inDotsMod=0;
    	Random rand = new Random();
    	Pattern pattern; // dot/(:at start) patren
		Matcher matcher;
    	String header=""; //= last line
    	List<String> listOut = new ArrayList<String>();
    	String lineHide="", lineFull="";
    	
    	Uri myUri = Uri.parse(uriPath);
    	File myFile = new File(myUri.getPath());
    	
    	BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(myFile));
		
    	 
    	String line = null;
    	while ((line = br.readLine()) != null) {
    		//Test for dots.
    		pattern = Pattern.compile("^\\s*[\\wא-ת]*\\s*[\\.\\-\\*:=?]+\\s*"); // dot/(:at start) patren
    		matcher = pattern.matcher(line);
    		if(matcher.find()){
    			if(inDotsMod==0){
    				inDotsMod=1;
    				lineHide = header+"\n";
    				lineFull =  header+"\n";
    			}
    			lineFull = lineFull+"<br>"+line.substring(matcher.start(), matcher.end())+"<b>" +line.substring(matcher.end(),line.length())+"</b>";
    			if(rand.nextDouble()<hidePrecent){
    				lineHide=lineHide+"\n"+line.substring(matcher.start(), matcher.end())+ "_______________";
    			} else {
    				lineHide=lineHide+"\n"+line;
    			}

    		} else {
    			if(inDotsMod==1){ //mode change
    				inDotsMod=0;
    				listOut.add(lineHide);
    	    		listOut.add(lineFull);
    			}
    		}
    		
    		if(inDotsMod==0){
    			pattern = Pattern.compile("(\\d+[\\d\\.:=\\-\\_\\*]*)"); // a number or date
    			matcher = pattern.matcher(line);
        		if(matcher.find() && rand.nextDouble()<hidePrecent){ //TODO add precent
        			listOut.add(matcher.replaceAll("_____"));
    	    		listOut.add(matcher.replaceAll("<b>$1</b>"));
        		}
    		}
    		
    		
    		header = line;
    	}
    	br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
		return listOut;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
    /**
     * A simple pager adapter that represents NUM_PAGES {@link DispalyFileScreenSlidePageFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return DispalyFileScreenSlidePageFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
