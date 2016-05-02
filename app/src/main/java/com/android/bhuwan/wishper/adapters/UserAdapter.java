package com.android.bhuwan.wishper.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.bhuwan.wishper.R;
import com.android.bhuwan.wishper.utils.MD5Util;
import com.android.bhuwan.wishper.utils.ParseConstants;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by bhuwan on 10/21/2015.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {

    private static final String TAG = UserAdapter.class.getSimpleName();
    private Context mContext;
    private List<ParseUser> mUsers;

    public UserAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.user_item, users);
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            holder.userImage = (ImageView)convertView.findViewById(R.id.userImage);
            holder.userLabel = (TextView)convertView.findViewById(R.id.friend_label);
            holder.checkImage = (ImageView)convertView.findViewById(R.id.checkImage);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();

        if(email.equals(" ")){
            holder.userImage.setImageResource(R.drawable.avatar_empty);
        }else{
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
            Picasso.with(mContext).load(gravatarUrl).placeholder(R.drawable.avatar_empty).into(holder.userImage);
        }
        holder.userLabel.setText(user.getUsername());

        GridView gridView = (GridView)parent;
        if(gridView.isItemChecked(position)){
            holder.checkImage.setVisibility(View.VISIBLE);
        }else{
            holder.checkImage.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    private static class ViewHolder{
        TextView userLabel;
        ImageView userImage;
        ImageView checkImage;
    }

    public void refill(List<ParseUser> users){
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }
}
