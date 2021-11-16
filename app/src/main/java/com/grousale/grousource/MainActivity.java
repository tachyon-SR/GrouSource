package com.grousale.grousource;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.grousale.grousource.activity.AdminActivity;
import com.grousale.grousource.activity.BarCodeScannerActivity;
import com.grousale.grousource.customDialog.AdminSignIn;
import com.grousale.grousource.customDialog.SyncProducts;
import com.grousale.grousource.ui.main.SectionsPagerAdapter;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newScan:
                AdminSignIn popup = new AdminSignIn(MainActivity.this, "AddNew");
                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popup.show();
                return true;

            case R.id.editItem:
                AdminSignIn popupp = new AdminSignIn(MainActivity.this, "EditExisting");
                popupp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupp.show();
                return  true;

            case R.id.sync:
                SyncProducts popupsync = new SyncProducts(MainActivity.this);
                popupsync.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupsync.show();
                return  true;
                

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkCertificates();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

    }

    public void checkCertificates() {
        try {
            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(this);
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }


}