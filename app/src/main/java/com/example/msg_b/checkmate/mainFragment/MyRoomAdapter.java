package com.example.msg_b.checkmate.mainFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.msg_b.checkmate.R;
import com.example.msg_b.checkmate.util.Room;
import com.example.msg_b.checkmate.util.Util;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyRoomAdapter extends RecyclerView.Adapter<MyRoomAdapter.MyViewHolder> {


    Context context;
    ArrayList<Room> items;


    private OnMyItemClickListener mListener;
    public interface OnMyItemClickListener {
        void onItemClick(int position);
    }

    public void setOnMyItemClickListener(OnMyItemClickListener listener) {
        mListener = listener;
    }


    public MyRoomAdapter(Context context, ArrayList<Room> items) {
        this.context = context;
        this.items = items;
    }

    void setData(ArrayList<Room> items) {
        this.items = items;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatroom, parent, false);

        return new MyViewHolder(view, this.mListener);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        Room tmpItem = items.get(position); // 현재 방
        String currentTime = Util.getCurrentTime(); // 현재 시간
        String currentYMD = Util.parseTimeYMD(currentTime); // 현재 연월일
        String itemYMD = Util.parseTimeYMD(tmpItem.getLastTime()); // 마지막메시지 연월일
        String itemHM = Util.parseTimeHM(tmpItem.getLastTime()); // 마지막메시지 시분초

        Glide.with(context).load(tmpItem.getTo_profile()).into(holder.civ_profile);
        holder.tv_nickname.setText(tmpItem.getTo_nickname());

        // 메시지의 길이가 14자가 넘어가면 뒤에는 ...으로 처리한다.
        if(tmpItem.getLastMsg().length()>14) {
            String tmpText = tmpItem.getLastMsg().substring(0, 14)+"...";
            holder.tv_msg.setText(tmpText);
        } else {
            holder.tv_msg.setText(tmpItem.getLastMsg());
        }

        // 읽지 않은 메시지가 있으면 갯수를 표시해준다.
        if(!tmpItem.getStatus().equals("0")) {
            holder.tv_count.setText(tmpItem.getStatus());
            holder.tv_count.setPadding(10, 1, 10, 1);
        }

        // 오늘 받은 메시지는 시간을 표시하고, 오늘 받은 메시지가 아니면 연월일로 표시 (!!!! 현재는 2019년이라 월일만 표시)
        if(currentYMD.equals(itemYMD)) {
            holder.tv_time.setText(itemHM);
        } else {
            if(itemYMD.length()>4)
            holder.tv_time.setText(itemYMD.substring(5));
        }

    }



    @Override
    public int getItemCount() {
        return items.size();
    }




    class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView civ_profile;
        TextView tv_nickname;
        TextView tv_msg;
        TextView tv_count;
        TextView tv_time;

        public MyViewHolder(View itemView, final OnMyItemClickListener listener) {
            super(itemView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener !=null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            civ_profile = itemView.findViewById(R.id.Civ_profile);
            tv_nickname = itemView.findViewById(R.id.Tv_nickname);
            tv_msg = itemView.findViewById(R.id.Tv_msg);
            tv_count = itemView.findViewById(R.id.Tv_count);
            tv_time = itemView.findViewById(R.id.Tv_time);

        }
    }


}
