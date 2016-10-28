package com.mal.a7walek.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mal.a7walek.DataObjects.ClientRequest;
import com.mal.a7walek.R;
import com.mal.a7walek.adapters.ClientRequestsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ClientHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private ClientRequestsAdapter clientRequestsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ClientRequest> requestsList;
    private static String LOG_TAG = "CardViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication() , ClientAddRequest.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initializeData();
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        clientRequestsAdapter = new ClientRequestsAdapter(requestsList);
        mRecyclerView.setAdapter(clientRequestsAdapter);

        clientRequestsAdapter.setOnItemClickListener(new ClientRequestsAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                startActivity(new Intent(getApplication(),ClientRequestDetails.class));

            }
        });
    }

    private void initializeData() {
        requestsList = new ArrayList<>();
        requestsList.add(0,new ClientRequest(getString(R.string.request_desc),R.mipmap.ic_launcher));
        requestsList.add(1,new ClientRequest(getString(R.string.request_desc),R.mipmap.ic_launcher));
        requestsList.add(2,new ClientRequest(getString(R.string.request_desc),R.mipmap.ic_launcher));
        requestsList.add(3,new ClientRequest(getString(R.string.request_desc),R.mipmap.ic_launcher));
        requestsList.add(4,new ClientRequest(getString(R.string.request_desc),R.mipmap.ic_launcher));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_log_out) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
