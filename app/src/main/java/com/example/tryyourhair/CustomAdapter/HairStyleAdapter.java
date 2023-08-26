package com.example.tryyourhair.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tryyourhair.Interface.RecyclerViewInterFace;
import com.example.tryyourhair.R;

import java.util.List;

import com.example.tryyourhair.Models.HairStyle;

public class HairStyleAdapter  extends RecyclerView.Adapter<HairStyleAdapter.HairStyleViewHolder>{
   private final RecyclerViewInterFace recyclerViewInterFace;
    private AdapterView.OnItemClickListener listener;

    private static final String TAG = "HairStyleAdapter";
    private List<HairStyle> listHairStyle;
    private Context context;
    private LayoutInflater layoutInflater;

    public HairStyleAdapter(Context context, List<HairStyle> lHairStyle, RecyclerViewInterFace recyclerViewInterFace) {
        this.context = context;
        this.listHairStyle = lHairStyle;
        this.layoutInflater = LayoutInflater.from(context);
        this.recyclerViewInterFace = recyclerViewInterFace;
    }

    @NonNull
    @Override
    public HairStyleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate view from Google Card View
        View itemView = layoutInflater.inflate(R.layout.activity_card_view_hairstyle, parent, false);
        return new HairStyleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HairStyleViewHolder holder, int position) {
        // get hairstyle in listHairStyle via position
        HairStyle hairStyle = listHairStyle.get(position);

        // bind data to view holder
//        holder.url.setText(hairStyle.getUrl());
        holder.des.setText(hairStyle.getDes());
        holder.name.setText(hairStyle.getName());
        Glide.with(this.context).load(hairStyle.getUrl()).into(holder.img_hairstyle);

    }

    @Override
    public int getItemCount() {
        return listHairStyle.size();
    }


    // Create an inner class HairStyleViewHolder
    class HairStyleViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView url;
        private TextView des;
        private ImageView img_hairstyle;

        public HairStyleViewHolder(View itemView) {
            super(itemView);
            img_hairstyle = (ImageView) itemView.findViewById(R.id.img_hairstyle);
            url = (TextView) itemView.findViewById(R.id.txt_url);
            des = (TextView) itemView.findViewById(R.id.txt_hairdes);
            name = (TextView) itemView.findViewById(R.id.txt_hairstyle_name) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterFace != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterFace.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
