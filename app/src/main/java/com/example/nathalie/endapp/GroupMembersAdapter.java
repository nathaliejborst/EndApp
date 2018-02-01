package com.example.nathalie.endapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nathalie Borst on 29/01/2018.
 * Adapter that takes a list with User.class objects as input and shows user's name and e-mail
 * address
 */

public class GroupMembersAdapter extends BaseAdapter {
    private Context mContext;
    private List<User> mMemberslist;

    public GroupMembersAdapter(Context mContext, List<User> mMemberslist) {
        this.mContext = mContext;
        this.mMemberslist = mMemberslist;
    }

    @Override
    public int getCount() {
        return mMemberslist.size();
    }

    @Override
    public Object getItem(int position) {
        return mMemberslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(mContext, R.layout.item_members_list, null);
        TextView member = (TextView)v.findViewById(R.id.membername_tv);
        TextView email = (TextView)v.findViewById(R.id.email_tv);

        // Change textviews to according values
        member.setText(mMemberslist.get(i).username);
        email.setText(mMemberslist.get(i).email);

        return v;
    }
}


