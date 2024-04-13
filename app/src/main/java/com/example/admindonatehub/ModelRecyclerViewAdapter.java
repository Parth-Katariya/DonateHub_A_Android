package com.example.admindonatehub;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ModelRecyclerViewAdapter extends RecyclerView.Adapter<ModelRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Model> arrayList;

    public ModelRecyclerViewAdapter(Context context, ArrayList<Model> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Model model = arrayList.get(position);

        // Load image from the image path using Picasso
        Picasso.get().load(model.getImagePath()).into(holder.imageView);

        holder.textView1.setText(model.getName());
        holder.textView2.setText(model.getDntdetail());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, managerecords.class); // Replace with your destination activity
                intent.putExtra("donate", model.getImagePath());
                intent.putExtra("name", model.getName());
                intent.putExtra("dntdetail", model.getDntdetail());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView1, textView2;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img1);
            textView1 = itemView.findViewById(R.id.txt1);
            textView2 = itemView.findViewById(R.id.txt2);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }
}
