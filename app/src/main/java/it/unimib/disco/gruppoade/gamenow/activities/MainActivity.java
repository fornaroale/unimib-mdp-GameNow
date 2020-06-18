package it.unimib.disco.gruppoade.gamenow.activities;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import it.unimib.disco.gruppoade.gamenow.database.FbDatabase;

import it.unimib.disco.gruppoade.gamenow.MobileNavigationDirections;
import it.unimib.disco.gruppoade.gamenow.R;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "MainActivity";
    private AppCompatActivity appCompatActivity;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // User identification
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        appCompatActivity = this;
        if (user == null) {
            createSignInIntent();
        } else {
            createFeed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        final MenuItem item = menu.findItem(R.id.search_action);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Cerca Gioco...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MobileNavigationDirections.SearchAction action = MobileNavigationDirections.searchAction(query);
                navController.navigate(action);
                searchView.setQuery("", false);
                searchView.clearFocus();
                item.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.app_logo)
                        .build(),
                RC_SIGN_IN);

        // [END auth_fui_create_intent]
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "Nome utente: " + user.getDisplayName());

                String usernameDb = user.getUid();
                Log.d(TAG, "usernameDb: " + usernameDb);

                createFeed();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                finish();
            }
        }
    }

    private void createFeed(){
        FbDatabase.setUserReference();

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(appCompatActivity, R.id.nav_host_fragment);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_feed, R.id.navigation_discover, R.id.navigation_comingsoon, R.id.navigation_profile)
                .build();
        NavigationUI.setupActionBarWithNavController(appCompatActivity, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        invalidateOptionsMenu();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }
}