package com.mal.a7walek.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mal.a7walek.R;
import com.mal.a7walek.models.Job;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ahmed Badr on 28/10/2016.
 */
public class WorkerHomeAdapter extends RecyclerView.Adapter<WorkerHomeAdapter.requestsViewHolder> {

    List<Job> workerRequests;
    Context ctx;
    private static MyClickListener myClickListener;

    public WorkerHomeAdapter(Context ctx,List<Job> workerRequests){
        this.workerRequests = workerRequests;
        this.ctx=ctx;
    }

    @Override
    public requestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_worker_home, parent, false);
        requestsViewHolder pvh = new requestsViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(requestsViewHolder holder, int position) {
        holder.clientNametv.setText(workerRequests.get(position).getUser_token());
        holder.clientDescriptiontv.setText(workerRequests.get(position).getDescription());
        Picasso.with(ctx).load(workerRequests.get(position).getImage_url()).into(holder.clientPhoto);
    }

    @Override
    public int getItemCount() {
        return workerRequests.size();
    }

    public void addItem(Job workerRequest, int index) {
        workerRequests.add(index, workerRequest);
        notifyItemInserted(index);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class requestsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cv;
        TextView clientDescriptiontv;
        TextView clientNametv;
        ImageView clientPhoto;


        public requestsViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.worker_home_cv);
            clientDescriptiontv = (TextView)itemView.findViewById(R.id.client_description);
            clientNametv = (TextView)itemView.findViewById(R.id.client_name);
            clientPhoto = (ImageView)itemView.findViewById(R.id.client_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
