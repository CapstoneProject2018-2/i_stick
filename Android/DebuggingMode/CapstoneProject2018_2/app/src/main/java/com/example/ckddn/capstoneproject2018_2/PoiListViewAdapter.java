package com.example.ckddn.capstoneproject2018_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PoiListViewAdapter extends BaseAdapter {

    private ArrayList<PoiListViewItem> poiListViewItemList = new ArrayList<PoiListViewItem>();

    public PoiListViewAdapter(){

    }


    @Override
    public int getCount() {
        return poiListViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return poiListViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.poi_item,parent,false);
        }
        TextView poiNameTextView = (TextView)convertView.findViewById(R.id.poiName_tv);
        TextView addressTextView = (TextView)convertView.findViewById(R.id.address_tv);

        PoiListViewItem poiListViewItem = poiListViewItemList.get(position);

        poiNameTextView.setText(poiListViewItem.getPoiName());

        // null 해결
//        addressTextView.setText(poiListViewItem.getAddress());
        String[] address = poiListViewItem.getAddress().split(" ");
        String upperAddrName = address[0];
        String middleAddrName = address[1];
        String lowerAddrName = address[2];
//        String detailAddrName = address[3];   //  null값의 원인
        addressTextView.setText(upperAddrName +" "+ middleAddrName +" "+ lowerAddrName);

        return convertView;
    }

    public void putSearchResults(POI poi){
        String poiName;
        String address;
        double longitude;
        double latitude;

        poiName = poi.POIgetName();
        address = poi.POIgetAddress();
        longitude = poi.POIgetLongitude();
        latitude = poi.POIgetLatitude();

        PoiListViewItem poiitem = new PoiListViewItem();
        poiitem.setPoiName(poiName);
        poiitem.setAddress(address);

        poiListViewItemList.add(poiitem);

    }
}
