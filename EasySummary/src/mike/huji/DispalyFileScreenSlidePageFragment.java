package mike.huji;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DispalyFileScreenSlidePageFragment extends Fragment {
	/**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    
    private Button showButton;
    

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static DispalyFileScreenSlidePageFragment create(int pageNumber) {
    	DispalyFileScreenSlidePageFragment fragment = new DispalyFileScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public DispalyFileScreenSlidePageFragment() {
    
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
    	ViewGroup rootView = null;
    	rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_display_file, container, false);
    	final TextView textShow = (TextView) rootView.findViewById(R.id.text_show);
    	textShow.setText(DisplayActivity.textShowMap.get(this.mPageNumber));
    	final TextView textNumberShow = (TextView) rootView.findViewById(R.id.text_show_number);
    	textNumberShow.setText("Quiz "+(mPageNumber+1)+" From "+DisplayActivity.NUM_PAGES);
    	showButton = (Button) rootView.findViewById(R.id.show_answer);
    	showButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				textShow.setText(Html.fromHtml(DisplayActivity.textHideMap.get(mPageNumber)));
	        }
	    });
        return rootView;
    }
    
    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}