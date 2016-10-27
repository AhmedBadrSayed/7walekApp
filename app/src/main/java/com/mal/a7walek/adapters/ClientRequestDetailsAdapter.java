package com.mal.a7walek.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mal.a7walek.DataObjects.ClientRequest;
import com.mal.a7walek.R;

import java.util.List;

/**
 * Created by Ahmed Badr on 27/10/2016.
 */
public class ClientRequestDetailsAdapter extends RecyclerView.Adapter<ClientRequestDetailsAdapter.requestsViewHolder> {

    List<ClientRequest> clientRequests;
    private static MyClickListener myClickListener;

    public ClientRequestDetailsAdapter(List<ClientRequest> clientRequests){
        this.clientRequests = clientRequests;
    }

    @Override
    public requestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_client_request, parent, false);
        requestsViewHolder pvh = new requestsViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(requestsViewHolder holder, int position) {
        holder.clientName.setText(clientRequests.get(position).clientName);
        holder.requestDescription.setText(clientRequests.get(position).requestDescription);
        holder.requestPhoto.setImageResource(clientRequests.get(position).requestPhoto);
    }

    @Override
    public int getItemCount() {
        return clientRequests.size();
    }

    public void addItem(ClientRequest clientRequest, int index) {
        clientRequests.add(index, clientRequest);
        notifyItemInserted(index);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class requestsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cv;
        TextView clientName;
        TextView requestDescription;
        ImageView requestPhoto;


        public requestsViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            clientName = (TextView)itemView.findViewById(R.id.clientName);
            requestDescription = (TextView)itemView.findViewById(R.id.descreption);
            requestPhoto = (ImageView)itemView.findViewById(R.id.requestImage);
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
