package mike.huji;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScreenSlidePageFragment extends Fragment {
	
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber) {
    	ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
    	ViewGroup rootView = null;
    	switch (this.mPageNumber)  //Counting from 0 to NUM_PAGES-1 (like arrays)...
    	{
    		case 0:
    			rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page_1, container, false);
    			break;
    		case 1:
    			rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page_2, container, false);
    			break;
    		case 2:
    			rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page_3, container, false);
    			break;
    		default:
    			rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page_1, container, false);
    			break;	
    	}
        return rootView;
    }
    
    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}