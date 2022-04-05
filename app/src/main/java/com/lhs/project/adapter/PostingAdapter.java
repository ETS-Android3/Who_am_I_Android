package com.lhs.project.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lhs.project.R;
import com.lhs.project.model.Posting;

import java.util.List;

public class PostingAdapter extends RecyclerView.Adapter<PostingAdapter.ViewHolder>{
    Context context;
    List<Posting> postingList;

    public PostingAdapter(Context context, List<Posting> postingList) {
        this.context = context;
        this.postingList = postingList;
    }

    public interface OnItemClickListener {
        void onItemClick(PostingAdapter.ViewHolder holder, View view, int position);
    }
    PostingAdapter.OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photoboard_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posting posting = postingList.get(position);

        // 가져온 uri를 bitmap으로 캐시에 저장 후 이미지뷰에 뿌리기
        if(posting.getImg_url()!=null) {
            Glide.with(context).asBitmap().load(posting.getImg_url()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    holder.imgPhoto.setImageBitmap(resource);
                }
            });
        }
        holder.txtContent.setText(posting.getContent());
        holder.username.setText(posting.getUsername());
    }

    @Override
    public int getItemCount() {
        return postingList.size();
    }

    public void addItem(Posting item) {
        postingList.add(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView imgPhoto;
        private TextView txtContent;
        private  TextView username;
        private ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView_photoboard);
            imgPhoto = itemView.findViewById(R.id.photo);
            txtContent = itemView.findViewById(R.id.txtContent);
            username = itemView.findViewById(R.id.username);
            deleteBtn = itemView.findViewById(R.id.deletebtn);
            deleteBtn.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener!=null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

    }
}
