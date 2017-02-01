package com.example.han.myapplication001.chatlist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.han.myapplication001.R;

import java.util.ArrayList;

public class custom_list_chat  extends BaseAdapter {
    private ArrayList<chatListClass> listViewItemList = new ArrayList<chatListClass>() ;

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("sadsadsa","GEtView 실행");
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friend_chat_flator, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.Profile_image_2);
        TextView title = (TextView) convertView.findViewById(R.id.title_2);
        TextView title2 = (TextView) convertView.findViewById(R.id.title2_2);
        TextView title3 = (TextView) convertView.findViewById(R.id.title2_3);

        chatListClass temp = listViewItemList.get(position);

        title.setText(temp.names);
        title2.setText(temp.message);
        title3.setText(temp.count.toString());
        imageView.setImageResource(R.drawable.online);

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void adds(ArrayList<chatListClass> temp) {
        listViewItemList = temp;
    }

    public void addItem() {
        chatListClass item = new chatListClass("1",1,"2");

        listViewItemList.add(item);
    }
}