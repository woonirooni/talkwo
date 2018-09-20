package firebase.wooni.talkwo.views;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import firebase.wooni.talkwo.R;
import firebase.wooni.talkwo.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    @BindView(R.id.search_area)
    LinearLayout mSearchArea;

    @BindView(R.id.edtContent)
    EditText edtEmail;

    private FirebaseUser mFirebaseUser;

    private FirebaseAuth mFirebaseAuth;

    private FirebaseDatabase mFirebaseDb;

    private DatabaseReference mFriendsDBRef;
    private DatabaseReference mUserDBRef;

    public FriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View friendview = inflater.inflate(R.layout.fragment_friend, container, false);
        ButterKnife.bind(this, friendview);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDb = FirebaseDatabase.getInstance();

        mFriendsDBRef = mFirebaseDb.getReference("user").child(mFirebaseUser.getUid()).child("friends");
        mUserDBRef = mFirebaseDb.getReference("user");

        return friendview;
    }

    public void toggleSearchbar() {
        mSearchArea.setVisibility(mSearchArea.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.findBtn)
    public void addFriend() {
        //1. 입력된 이메일 가져오기
        final String inputEmail = edtEmail.getText().toString();
        // - 이메일 입력안될시 이메일입력 메세지출력
        if (inputEmail.isEmpty()) {
            Snackbar.make(mSearchArea, "이메일을 입력하세요.", Snackbar.LENGTH_SHORT).show();
            return;
        }
        // - 자기자신을 친구로 등록할 수 없기 때문에 FirebaseUser의 email이 입력한 이메일과 같다면,
        // -자기자신은 등록불가 메세지를 띄워줍니다.
        if (inputEmail.equals(mFirebaseUser.getEmail())) {
            Snackbar.make(mSearchArea, "자기 자신을 등록 할 수 없습니다.", Snackbar.LENGTH_SHORT).show();
            ;
            return;
        }
        // - 이메일 정상이면 나의 정보 조회하여 이미등록된 친구인지 판단하고
        mFriendsDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> friendIterable = dataSnapshot.getChildren();
                Iterator<DataSnapshot> friendIterator = friendIterable.iterator();

                while (friendIterator.hasNext()) {
                    User user = friendIterator.next().getValue(User.class);

                    if (user.getEmail().equals(inputEmail)) {
                        Snackbar.make(mSearchArea, "이미 등록된 친구입니다.", Snackbar.LENGTH_LONG).show();
                        return;
                    }

                }
                // - users db에 존재하지 않은 이메일이라면, 가입하지 않는 친구라는 메세지를 띄워주고
                mUserDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> userIterator = dataSnapshot.getChildren().iterator();
                        int userCount = (int) dataSnapshot.getChildrenCount();
                        int loopCount = 1;

                        while (userIterator.hasNext()) {
                            final User currentUser = userIterator.next().getValue(User.class);

                            if (inputEmail.equals(currentUser.getEmail())) {
                                //일치 하면 친구등록로직
                                // - users/{myuid}/friends/{someone_uid}/firebasePush상대 정보를 등록하고
                                mFriendsDBRef.push().setValue(currentUser, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        // - users/{somenone_uid}/friends/{myuid}/상대 정보를 등록하고
                                        //나의 정보가져오기
                                        mUserDBRef.child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                mUserDBRef.child(currentUser.getUid()).child("friends").push().setValue(user);
                                                Snackbar.make(mSearchArea, "친구 등록 완료", Snackbar.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                    }
                                });

                            }else {
                                if (loopCount++ >= userCount) {
                                    Snackbar.make(mSearchArea, "가입이 안된 친구입니다.", Snackbar.LENGTH_LONG).show();
                                    return;
                                    //총 사용자 수 == loopcount 일치
                                    //등록된 사용자가 없다는 메세지 출력
                                }
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Snackbar.make(mSearchArea, "오류1", Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(mSearchArea, "오류2", Snackbar.LENGTH_LONG).show();

            }
        });

        // - users/{somenone_uid}/friends/{myuid}/상대 정보를 등록하고
        // 승낙 로직 추가해야함
    }
}


