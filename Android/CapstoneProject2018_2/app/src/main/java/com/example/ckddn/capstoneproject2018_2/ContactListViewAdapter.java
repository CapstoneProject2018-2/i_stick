package com.example.ckddn.capstoneproject2018_2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

//필터 기능을 추가한 ParentActivity의 연락처 리스트뷰를 control 하는 Adapter class.
//implemented by 양인수

public class ContactListViewAdapter extends BaseAdapter implements Filterable {

    //전체 contactlist 자료구조=> 서버에 등록된 관리 여기에 저장한다.
    private ArrayList<ContactListViewItem> clistViewItemList = new ArrayList<ContactListViewItem>();

    //검색어에 따라 filter된 리스트. (처음엔 clistViewItemList와 동일하다)
    private ArrayList<ContactListViewItem> filteredItemList = clistViewItemList;


    Filter listFilter;

    //Default Constructor

    public ContactListViewAdapter(){}

    //Adapter에서 사용되는 데이터 개수 반환
    @Override
    public int getCount() {
        return filteredItemList.size();
    }

    //position에 해당되는 데이터를 View로 반환
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.clistview_item,parent,false);
        }

        ImageView iconImageView = (ImageView)convertView.findViewById(R.id.person_icon);
        TextView titleTextView = (TextView)convertView.findViewById(R.id.person_name);
        TextView descTextView = (TextView)convertView.findViewById(R.id.phone_number);

        ContactListViewItem clistViewItem = filteredItemList.get(position);

        iconImageView.setImageDrawable(clistViewItem.getIcon());
        titleTextView.setText(clistViewItem.getTitle());
        descTextView.setText(clistViewItem.getDesc());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position);
    }

    public void addItem(Drawable icon, String uno, String title, String desc){
        ContactListViewItem item = new ContactListViewItem();

        item.setIcon(icon);
        item.setUno(uno);
        item.setTitle(title);
        item.setDesc(desc);
        clistViewItemList.add(item);
    }

    public void deleteItem(String uno) {
        for (int i = 0; i < clistViewItemList.size(); i++) {
            ContactListViewItem item = clistViewItemList.get(i);
            if (item.getUno().equals(uno)) {
                clistViewItemList.remove(i);
                break;
            }
        }
    }

    @Override
    public Filter getFilter() {
        if(listFilter == null){
            listFilter = new ListFilter();
        }
        return listFilter;
    }

    private class ListFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if(constraint == null || constraint.length() == 0){
                results.values = clistViewItemList;
                results.count = clistViewItemList.size();
            }else{
                ArrayList<ContactListViewItem> itemList= new ArrayList<ContactListViewItem>();
                for(ContactListViewItem item : clistViewItemList){
                    if(item.getTitle().toUpperCase().contains(constraint.toString().toUpperCase())||
                            item.getDesc().toUpperCase().contains(constraint.toString().toUpperCase())){
                        itemList.add(item);
                    }
                }

                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredItemList = (ArrayList<ContactListViewItem>)results.values;

            if(results.count>0){
                notifyDataSetChanged();;
            }else {
                notifyDataSetInvalidated();
            }

        }
    }
}
