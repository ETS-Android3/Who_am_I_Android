package com.lhs.project.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

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

public class MyPostingAdapter extends RecyclerView.Adapter<MyPostingAdapter.ViewHolder>{
    Context context;
    List<Posting> postingList;

    public MyPostingAdapter(Context context, List<Posting> postingList) {
        this.context = context;
        this.postingList = postingList;
    }

    public interface OnItemClickListener {
        void onItemClick(MyPostingAdapter.ViewHolder holder, View view, int position);
        void onDeleteClick(int index);
    }
    MyPostingAdapter.OnItemClickListener listener;
    public void setOnItemClickListener(MyPostingAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override

    public MyPostingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photoboard_row,parent,false);
        return new MyPostingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostingAdapter.ViewHolder holder, int position) {
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
        private ImageView deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView_photoboard);
            imgPhoto = itemView.findViewById(R.id.photo);
            txtContent = itemView.findViewById(R.id.txtContent);
            username = itemView.findViewById(R.id.username);
            deleteBtn = itemView.findViewById(R.id.deletebtn);
            deleteBtn.setVisibility(View.VISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener!=null) {
                        listener.onItemClick(MyPostingAdapter.ViewHolder.this, view, position);
                    }
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getAdapterPosition();
                    if(listener!=null) {
                        listener.onDeleteClick(index);
                    }
                }
            });
        }

    }
}
