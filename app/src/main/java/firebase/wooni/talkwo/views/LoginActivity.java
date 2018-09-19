package firebase.wooni.talkwo.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import firebase.wooni.talkwo.R;
import firebase.wooni.talkwo.models.User;
import lombok.NonNull;

public class LoginActivity extends AppCompatActivity {

    private View mProgressView;

    private SignInButton mSignbtn;

    private GoogleSignInClient mGoogleSignInClient;

    private GoogleSignInOptions mGoogleSignlnoptions;

    private static int GOOGLE_LOGIN_OPEN = 100;

    private FirebaseAuth mAuth;

    private FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseDatabase mDatebase;

    private DatabaseReference mUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressView = (ProgressBar) findViewById(R.id.login_progress);
        mSignbtn = (SignInButton) findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();
        mDatebase =FirebaseDatabase.getInstance();
        mUserRef = mDatebase.getReference("user");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Configure Google Sign In
        GoogleSignInOptions mGoogleSignlnoptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignlnoptions);

        mSignbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    // 값을 넘겨주는부분
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_LOGIN_OPEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_LOGIN_OPEN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseuser =  mAuth.getCurrentUser();
                            final User user = new User();
                            user.setEmail(firebaseuser.getEmail());
                            user.setName(firebaseuser.getDisplayName());
                            user.setUid(firebaseuser.getUid());
                            if (firebaseuser.getPhotoUrl() !=null)
                                user.setProfileUrl(firebaseuser.getPhotoUrl().toString());
                            mUserRef.child(user.getUid()).setValue(user, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @android.support.annotation.NonNull DatabaseReference databaseReference) {

                                    if (databaseError == null) {
                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                        finish();
                                        Bundle eventBundle = new Bundle();
                                        eventBundle.putString("email", user.getEmail());
                                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, eventBundle);
                                    }
                                }
                            });
                            Snackbar.make(mProgressView, "로그인성공.", Snackbar.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(mProgressView, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
    }
}



