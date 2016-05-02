package com.android.bhuwan.wishper.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.bhuwan.wishper.utils.ParseConstants;
import com.android.bhuwan.wishper.R;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

/**
 * Created by bhuwan on 10/21/2015.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {

    private static final String TAG = MessageAdapter.class.getSimpleName();
    private Context mContext;
    private List<ParseObject> mInboxMessages;

    public MessageAdapter(Context context, List<ParseObject> messages) {
        super(context, R.layout.message_item, messages);
        mContext = context;
        mInboxMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImage = (ImageView)convertView.findViewById(R.id.image_icon);
            holder.senderLabel = (TextView)convertView.findViewById(R.id.sender_name);
            holder.relativeTime = (TextView)convertView.findViewById(R.id.relativeTimeDiff);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        ParseObject message = mInboxMessages.get(position);
        Date createdAt = message.getCreatedAt();
        long now = new Date().getTime();

        String timeDiff = DateUtils.getRelativeTimeSpanString(createdAt.getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();
        holder.relativeTime.setText(timeDiff);

        if(message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)){
                holder.iconImage.setImageResource(R.drawable.ic_picture);
        }else if(message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_VIDEO)){
                holder.iconImage.setImageResource(R.drawable.ic_video);
        }
        holder.senderLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
        return convertView;
    }

    @Override
    public int getCount() {
        return mInboxMessages.size();
    }

    private static class ViewHolder{
        TextView senderLabel;
        ImageView iconImage;
        TextView relativeTime;
    }

    public void refill(List<ParseObject> messages){
        mInboxMessages.clear();
        mInboxMessages.addAll(messages);
        notifyDataSetChanged();
    }
}
