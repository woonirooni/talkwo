package firebase.wooni.talkwo.views;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import firebase.wooni.talkwo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends android.support.v4.app.Fragment {


    public FriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View friendview = inflater.inflate(R.layout.fragment_friend,container,false);
        return friendview;
    }

}
