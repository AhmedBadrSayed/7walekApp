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
import com.mal.a7walek.adapters.WorkerHomeAdapter;
import com.mal.a7walek.bus.BusProvider;
import com.mal.a7walek.bus.GetAllJobsEvent;
import com.mal.a7walek.data.PrefManager;
import com.mal.a7walek.models.Job;
import com.mal.a7walek.utility.FirebaseManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
//import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkerHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private WorkerHomeAdapter workerHomeAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Job> requestsList = new ArrayList<>();
    private static String LOG_TAG = "CardViewActivity";

    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.fab)FloatingActionButton fab;
    @BindView(R.id.drawer_layout)DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    //@BindView(R.id.avl_loading)AVLoadingIndicatorView avl_loading;
    TextView worker_profile_name;
    ImageView worker_profile_image;
    TextView worker_profile_address;
    TextView worker_profile_preofession;

    Bus mBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_home);

        ButterKnife.bind(this);

        //avl_loading.show();

        mBus = BusProvider.getInstance();

        getNearByJobs();

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
        worker_profile_name = (TextView)hView.findViewById(R.id.worker_profile_name);
        worker_profile_image = (ImageView)hView.findViewById(R.id.worker_profile_image);
        worker_profile_address = (TextView)hView.findViewById(R.id.worker_profile_adress);
        worker_profile_preofession = (TextView)hView.findViewById(R.id.worker_profile_profession);

        setupNavHeaderValues();

        setupRecycleView();

        workerHomeAdapter.setOnItemClickListener(new WorkerHomeAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                Intent intent = new Intent(getApplication(),WorkerRequestDetails.class);
                Bundle extras = new Bundle();
                extras.putString(getString(R.string.pref_client_token),requestsList.get(position).getUser_token());
                extras.putString("desc",requestsList.get(position).getDescription());
                extras.putString("iv",requestsList.get(position).getImage_url());
                extras.putString(getString(R.string.pref_extra_job_key),requestsList.get(position).getKey());
                intent.putExtras(extras);

                startActivity(intent);

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplication() , WorkerOfferHelp.class));
                //TODO map activity display requests on map
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

    /**********************************************************************************************/

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

        if (id == R.id.nav_worker_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_worker_settings) {

        } else if (id == R.id.nav_wrker_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**********************************************************************************************************/

    public void setupNavHeaderValues(){
        String clientName = PrefManager.getStringValue(this,getString(R.string.pref_my_name),"");
        worker_profile_name.setText(clientName);

        String userImage = PrefManager.getStringValue(this,getString(R.string.pref_my_photo),"");
        Picasso.with(this).load(userImage).into(worker_profile_image);

        String userAddress = PrefManager.getStringValue(this,getString(R.string.pref_my_address),"");
        worker_profile_address.setText(userAddress);

        String profession = PrefManager.getStringValue(this,getString(R.string.pref_my_profession),"");
        worker_profile_preofession.setText(profession);
    }


    public void setupRecycleView(){
        mRecyclerView = (RecyclerView)findViewById(R.id.worker_home_cv);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        workerHomeAdapter = new WorkerHomeAdapter(this,requestsList);
        mRecyclerView.setAdapter(workerHomeAdapter);
    }


    public void getNearByJobs(){
        FirebaseManager mFirebaseManager = new FirebaseManager();
        mFirebaseManager.getWorkerNearByJobs();
    }


    @Subscribe
    public void OnGetNearByJobs(GetAllJobsEvent allJobsEvent){
        ArrayList<Job>jobsArray = allJobsEvent.getJobs();
        ArrayList<Job>nearByJobs = new ArrayList<>();

    /*    String profession = PrefManager.getStringValue(this,getString(R.string.pref_my_profession),"");

        double lat = PrefManager.getFloatValue(this,getString(R.string.pref_my_lat),0);
        double lng = PrefManager.getFloatValue(this,getString(R.string.pref_my_lng),0);

        for(int i=0;i<jobsArray.size();i++){
            Job job = new Job();
            job = jobsArray.get(i);

            if(job.getCategory().equals(profession))
                if(CalculateDistance(job.getLat() , job.getLng() , lat , lng , 0 , 0) <= 5000){
                    nearByJobs.add(job);
                }
        }*/

        // add nearby jobs to the adapter and view it to worker
        //avl_loading.hide();

        requestsList.addAll(jobsArray);
        workerHomeAdapter.notifyDataSetChanged();
    }


    public static double CalculateDistance(double lat1, double lat2, double lon1,
                                           double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }
}
