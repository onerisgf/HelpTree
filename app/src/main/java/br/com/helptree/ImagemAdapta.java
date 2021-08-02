package br.com.helptree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ImagemAdapta extends RecyclerView.Adapter<ImagemAdapta.ImageViewHolder> {

    private Context myContext;
    private List<Upload> myUploads;

    public ImagemAdapta(Context context, List<Upload> uploads){
        myContext = context;
        myUploads = uploads;
    }


    @NonNull
    @NotNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(myContext).inflate(R.layout.activity_form, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ImagemAdapta.ImageViewHolder holder, int position) {
        Upload uploadCurrent = myUploads.get(position);
        holder.txtNomeImagem.setText(uploadCurrent.getName());

    }

    @Override
    public int getItemCount() {
        return myUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public TextView txtNomeImagem;
        public ImageView imageView;

        public ImageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            txtNomeImagem = itemView.findViewById(R.id.txtNome);
            itemView.findViewById(R.id.imageTree);

        }
    }

}
