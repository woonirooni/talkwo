package firebase.wooni.talkwo.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import firebase.wooni.talkwo.R;

public class ChatFragment extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View chatview = inflater.inflate(R.layout.fragment_chat,container,false);
        return chatview;
    }


}
