package com.example.bobby.hotseat.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bobby.hotseat.Data.HSUser;
import com.example.bobby.hotseat.Data.Strings;
import com.example.bobby.hotseat.R;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity
        extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener
               //  LoaderCallbacks<Cursor>,    FOR EMAIL LOG IN
{

    private static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // EMAIL LOG IN
    // private UserLoginTask mAuthTask = null;

    // UI references.

    protected AutoCompleteTextView mUsernameView;
    protected EditText mPasswordView;
    protected Button mSignUpButton;

    protected TextView mSignUpTextView;
    private View mProgressView;
    private View mLoginFormView;

    // Firebase
    Firebase mRef;
    private DatabaseReference mDatabase;
    static GoogleSignInAccount acct;


    // Google Sign In
    SignInButton mGoogleSignInButton;
    Button mGoogleSignOutButton;
    GoogleApiClient mGoogleApiClient;
    TextView googleStatusTextView;
    private static final int RC_GOOGLE_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private HSUser databaseHotSeatUser = null;
    private boolean newUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);

        // Google Sign in
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
// basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

// Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleStatusTextView = (TextView) findViewById(R.id.googleStatusTextView);
        mGoogleSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        mGoogleSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Attempt Log In
                setProgressBarIndeterminateVisibility(true);
                googleSignIn();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + firebaseUser.getUid());
                    boolean isNewUser = isNewUser(firebaseUser);
                    if (isNewUser) { // Create and save new User object to database

                        Log.d(TAG, "CREATING AND WRITING NEW USER");
                        HSUser hSUser = createNewUser(firebaseUser);
                        writeNewUser(hSUser);

                    } else {
                        Log.d(TAG, "USER EXISTS");
                    }

                    navigateToMainActivity();

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mGoogleSignOutButton = (Button) findViewById(R.id.googleSignOutButton);
        mGoogleSignOutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignOut();
            }
        });

        // Set up the login form.

        /*
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.usernameField);
        populateAutoComplete();
        mUsernameView.setSingleLine();
        mUsernameView.setImeOptions(EditorInfo.IME_ACTION_NEXT); // TODO: WON'T WORK!

        mPasswordView = (EditText) findViewById(R.id.passwordField);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    EmailLogIn.attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mLogInButton = (Button) findViewById(R.id.LogInButton);
        mLogInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mSignUpTextView = (TextView) findViewById(R.id.signUpTextView);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        */
    }

    private void navigateToMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean isNewUser(FirebaseUser firebaseUser){

        final String currentId = firebaseUser.getUid();
        Log.d(TAG, "CURRENT ID:    " + currentId);

        mDatabase.child(Strings.KEY_USERS).child(currentId).addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        databaseHotSeatUser = dataSnapshot.getValue(HSUser.class);
                        Log.d(TAG, "DATABASE ID:    XX" + databaseHotSeatUser);
                        if (databaseHotSeatUser == null) {
                            newUser = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // TODO
                        Log.d(TAG, "IS NEW USER METHOD: CHILD EVENT LISTENER: ON CANCELLED");
                    }
                });

        return newUser;
    }

    private HSUser createNewUser(FirebaseUser firebaseUser) {
        HSUser hSUser = new HSUser(firebaseUser.getUid(),
                                                  firebaseUser.getDisplayName(),
                                                  firebaseUser.getEmail());
        hSUser.addFriend(firebaseUser.getUid(), firebaseUser.getDisplayName());
        return hSUser;
    }

    private void writeNewUser(HSUser hSUser) {

        mDatabase.child(Strings.KEY_USERS).child(hSUser.getIdToken()).setValue(hSUser);

    }

    private void googleSignIn() {
        Intent googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(googleSignInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult: " + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
            googleStatusTextView.setText("Hello, " + acct.getDisplayName());

            // updateUI(true);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(R.string.login_error_message)
                    .setTitle(R.string.login_error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();

            // updateUI(false);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        setProgressBarIndeterminateVisibility(false);
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);
    }

    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                googleStatusTextView.setText(R.string.google_signed_out);
            }
        });
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com");
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}

