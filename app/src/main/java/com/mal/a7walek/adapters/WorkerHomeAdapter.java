package com.mal.a7walek.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mal.a7walek.DataObjects.ClientRequest;
import com.mal.a7walek.DataObjects.WorkerRequest;
import com.mal.a7walek.R;

import java.util.List;

/**
 * Created by Ahmed Badr on 28/10/2016.
 */
public class WorkerHomeAdapter extends RecyclerView.Adapter<WorkerHomeAdapter.requestsViewHolder> {

    List<WorkerRequest> workerRequests;
    private static MyClickListener myClickListener;

    public WorkerHomeAdapter(List<WorkerRequest> workerRequests){
        this.workerRequests = workerRequests;
    }

    @Override
    public requestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_client_request, parent, false);
        requestsViewHolder pvh = new requestsViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(requestsViewHolder holder, int position) {
        holder.clientNametv.setText(workerRequests.get(position).clientName);
        holder.clientDescriptiontv.setText(workerRequests.get(position).clientDescription);
        holder.clientPhoto.setImageResource(workerRequests.get(position).clientPhoto);
    }

    @Override
    public int getItemCount() {
        return workerRequests.size();
    }

    public void addItem(WorkerRequest workerRequest, int index) {
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
