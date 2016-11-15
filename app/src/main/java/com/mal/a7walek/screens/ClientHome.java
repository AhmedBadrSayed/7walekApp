package com.mal.a7walek.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mal.a7walek.R;
import com.mal.a7walek.adapters.ClientRequestsAdapter;
import com.mal.a7walek.bus.BusProvider;
import com.mal.a7walek.bus.GetUserJobsEvent;
import com.mal.a7walek.data.PrefManager;
import com.mal.a7walek.models.Job;
import com.mal.a7walek.utility.FirebaseManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private ClientRequestsAdapter clientRequestsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Job> requestsList=new ArrayList<>();
    private static String LOG_TAG = "CardViewActivity";

    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.fab_client_add)FloatingActionButton fab;
    @BindView(R.id.drawer_layout)DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    //@BindView(R.id.avl_loading)AVLoadingIndicatorView avl_loading;
    TextView client_profile_name;
    ImageView client_profile_image;
    TextView client_profile_address;

    Bus mBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ButterKnife.bind(this);

        //avl_loading.show();

        mBus = BusProvider.getInstance();

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        View hView =  navigationView.getHeaderView(0);
        client_profile_name = (TextView)hView.findViewById(R.id.client_profile_name);
        client_profile_image = (ImageView)hView.findViewById(R.id.client_profile_image);
        client_profile_address = (TextView)hView.findViewById(R.id.client_profile_adress);

        setupNavHeaderValues();

        loadMyPostedJobs();

        setupRecycleView();


        clientRequestsAdapter.setOnItemClickListener(new ClientRequestsAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                startActivity(new Intent(getApplication(),ClientRequestDetails.class)
                        .putExtra(getString(R.string.pref_extra_job_key),requestsList.get(position).getKey()));

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication() , ClientAddRequest.class));
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }


    /***********************************************************************************************************/


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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


    /**************************************************************************************************************/

    public void setupNavHeaderValues(){
        String clientName = PrefManager.getStringValue(this,getString(R.string.pref_my_name),"");
        client_profile_name.setText(clientName);

        String userImage = PrefManager.getStringValue(this,getString(R.string.pref_my_photo),"");
        Picasso.with(this).load(userImage).into(client_profile_image);

        String userAddress = PrefManager.getStringValue(this,getString(R.string.pref_my_address),"");
        client_profile_address.setText(userAddress);
    }


    public void setupRecycleView(){
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        clientRequestsAdapter = new ClientRequestsAdapter(this,requestsList);
        mRecyclerView.setAdapter(clientRequestsAdapter);
    }


    /**
     * get user posted jobs from firebase by his unique id which is his phone that
     *
     * is stored in shared preference
     *
     */
    private void loadMyPostedJobs() {
        FirebaseManager mFirebaseManager = new FirebaseManager();
        String userKey = getString(R.string.pref_my_phone);
        mFirebaseManager.getUserJobs(PrefManager.getStringValue(this,userKey,""));
    }


    /**
     *
     * @param jobsEvent
     */
    @Subscribe
    public void OnGetUserJobs(GetUserJobsEvent jobsEvent){

        //avl_loading.hide();

        if(jobsEvent.getJobs()!=null && jobsEvent.getJobs().size()>0){
            requestsList.addAll(jobsEvent.getJobs());
            clientRequestsAdapter.notifyDataSetChanged();
        }
    }

}
