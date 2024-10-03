package org.unibl.etf.osnovasredstvaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.unibl.etf.osnovasredstvaapp.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private SharedPreferences sharedPreferences;
    private static final String LANGUAGE_PREF = "";
    private static final String SELECTED_LANGUAGE = "selectlanguage_prefed_language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(LANGUAGE_PREF, MODE_PRIVATE);
        String language = sharedPreferences.getString(SELECTED_LANGUAGE, null);

        if (language == null) {
            language = "en"; // Postavi defaultni jezik na engleski
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SELECTED_LANGUAGE, language);
            editor.apply();
        }

        setAppLocale(language);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("LOKACIJA_ID")) {
            int lokacijaId = intent.getIntExtra("LOKACIJA_ID", 0);

            // Dobijte NavController i navigirajte na fragment sa prosleđenim podacima
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

            // Kreirajte Bundle sa argumentima
            Bundle args = new Bundle();
            args.putInt("LOKACIJA_ID", lokacijaId);


            // Navigirajte ka `OsnovnoSredstvoFragment` unutar NavHost-a
            navController.navigate(R.id.action_nav_osnovno_sredsvo_to_nav_osnovno_sredsvo_fragment, args);
        }
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                int currentDestinationId = navController.getCurrentDestination().getId();

                if (currentDestinationId == R.id.nav_osnovno_sredsvo) {
                    // Ako je trenutno prikazan fragment za osnovna sredstva, otvori fragment za dodavanje osnovnog sredstva
                    navController.navigate(R.id.action_nav_osnovno_sredsvo_to_add_osnovno_sredstvo_fragment);
                }
                else if(currentDestinationId == R.id.nav_zaposleni){
                    navController.navigate(R.id.action_nav_zaposleni_to_nav_add_zaposleni);

                }
                else if(currentDestinationId == R.id.nav_lokacije)
                {
                    navController.navigate(R.id.action_nav_lokacije_to_nav_add_lokacija);

                }
                else if(currentDestinationId == R.id.nav_popisna_lista){

                    navController.navigate(R.id.action_nav_popisna_lista_to_add_popisna_lista);

                }
                else if(currentDestinationId == R.id.nav_popisna_stavka_list){

                    navController.navigate(R.id.action_nav_popisna_stavka_list_to_add_popisna_stavka);

                }
                 else {
                        Snackbar.make(view, "Nema fragmenta za dodavanje podataka", Snackbar.LENGTH_LONG)
                                .setAction("Action", null)
                                .setAnchorView(R.id.fab).show();

                }

//
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.nav_osnovno_sredsvo, R.id.nav_zaposleni, R.id.nav_lokacije, R.id.nav_popisna_lista)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.add_osnovno_sredstvo_fragment || destination.getId() == R.id.nav_add_zaposleni
                    || destination.getId() == R.id.nav_add_lokacija || destination.getId() == R.id.nav_details
        || destination.getId() == R.id.add_popisna_lista
        || destination.getId() == R.id.nav_popisna_stavka_list) {
                binding.appBarMain.fab.setVisibility(View.GONE);
            } else {
                binding.appBarMain.fab.setVisibility(View.VISIBLE);
            }
        });
    }

    public void changeLanguage() {
        // Prikaz opcija za izbor jezika
        final String[] languages = {"en", "sr"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.izaberite_jezik))
                .setItems(languages, (dialog, which) -> {
                    // Spremi odabrani jezik u SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SELECTED_LANGUAGE, languages[which]);
                    editor.apply();

                    // Promijeni jezik aplikacije
                    setAppLocale(languages[which]);

                    // Restartuj aktivnost da bi se promjene primijenile
                    recreate();
                });
        builder.show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.btnChangeLangView) {
            changeLanguage();
            return true;
        }else{
           return super.onOptionsItemSelected(item);
       }

    }


    private void setAppLocale(String localeCode) {
        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        // Ažuriranje konfiguracije jezika za cijelu aplikaciju
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Ažuriraj trenutni prikaz
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}