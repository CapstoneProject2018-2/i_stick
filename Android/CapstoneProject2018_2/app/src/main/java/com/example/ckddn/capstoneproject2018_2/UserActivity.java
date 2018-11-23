package com.example.ckddn.capstoneproject2018_2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ckddn.capstoneproject2018_2.Oblu.DeviceControlActivity;
import com.skt.Tmap.MapUtils;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.util.HttpConnect;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

//LocationManager & LocationListener를 이용한 실시간 위치 찍기
//ver 1.0
//양인수


public class UserActivity extends AppCompatActivity {
    private String uno;
    private String userId;
    private TMapPoint curPoint, destPoint;    //  curPoint in LocationListener, destPoint in SendLocTask.onPostExecute()
    private TextView textView;

    /*  for FindPath    */
    private LinearLayout linearLayoutTmap;
    private TMapView tMapView = null;
    private TextView pathtext;
    private StringBuilder pathPoint = new StringBuilder();
    private ArrayList<PathItem> pathlist = null;
    private TMapPolyLine polyLine;
    private int pathlistIdx = 0;

    /*  Arduino */
    private BluetoothSPP bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // get user info from login page
        uno = getIntent().getStringExtra("no");
        userId = getIntent().getStringExtra("id");

        textView = (TextView) findViewById(R.id.user_location_result);
        textView.setText(userId + ": 위치정보 미수신중"); //DEFAULT

        linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(getApplicationContext());
        tMapView.setSKTMapApiKey("85bd1e2c-d3c1-4bbf-93ca-e1f3abbc5788\n");
        pathtext = (TextView) findViewById(R.id.path_text); //  경로에 대한 포인트의 정보 출력 뷰
        pathtext.setMovementMethod(new ScrollingMovementMethod());
        polyLine = new TMapPolyLine();
        polyLine.setLineWidth(2);
        polyLine.setLineColor(Color.BLUE);



        /*  Location Manager    */
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this, ServerInfo.permissions, PackageManager.PERMISSION_GRANTED);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            textView.setText("수신중...");
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    100, // 0.1초
                    1, // 1m 이상 움직이면 갱신한다
                    mLocationListener); //위치가 바뀌면 onLocationChanged로 인해서 갱신된다.
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    100, // 0.1초
                    1, // 1m 이상 움직이면 갱신
                    mLocationListener);

        } catch (SecurityException ex) { ex.printStackTrace();}

        // init Arduino BluetoothSPP
        bt = new BluetoothSPP(getApplicationContext());

        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "블루투스가 사용 불가능합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), name +"에 연결" /*+ " 주소: " + address*/ , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext(),"연결이 끊어졌습니다.",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext(), "연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
            bt.disconnect();
        } else {
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        }
    }

    @Override
    protected void onDestroy() {    /*  뒤로 가기로 화면이 꺼지면 LocationManager 서비스 종료*/
        super.onDestroy();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(mLocationListener);
        bt.stopService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (bt != null) {
            if (!bt.isBluetoothEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
            } else {
                if (!bt.isServiceAvailable()) {
                    bt.setupService();
                    bt.startService(BluetoothState.DEVICE_OTHER);
                }
            }
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            curPoint = new TMapPoint(location.getLatitude(), location.getLongitude());
            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //  경도
            double latitude = location.getLatitude();   //  위도


            String provider = location.getProvider();   //위치제공자

            /* save user cur location */
            new SendLocTask().execute("http://" + ServerInfo.ipAddress + "/user", longitude + "", latitude + "");

            /* signal making algorithm...  */
            if (pathlist != null) {
                if (pathlistIdx < pathlist.size()) {
                    double distance = MapUtils.getDistance(pathlist.get(pathlistIdx).getPoint(), curPoint);

                    if (distance < 7) { //  7m 이내면
                        Toast.makeText(getApplicationContext(), pathlist.get(pathlistIdx).getTurnType() + "", Toast.LENGTH_LONG).show();
                        /*  send turnType to Arduino    */
                        String sendMessage = pathlist.get(pathlistIdx).getTurnType() + "";//보낼 택스트
                        if (sendMessage.length() > 0) {
                            if (bt != null)
                                bt.send(sendMessage, true); //  send to IStick
                        }
                        pathlistIdx++;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "목적지로 도착하였습니다.", Toast.LENGTH_LONG).show();
                }
            }
            textView.setText("위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude);
        }

        // for LocationListener
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };


    /* LocationListener에서 위치가 바뀔때 마다 사용자의 현재위치에 대한 정보를 서버에 전송하는 작업 수행 */
    public class SendLocTask extends AsyncTask<String, String, String> {
        String TAG = "SendLocTask>>>";

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: start!");
            try {   //  json accumulate
                JSONObject locationInfo = new JSONObject();
                locationInfo.accumulate("uno", uno);
                locationInfo.accumulate("longitude", strings[1]);
                locationInfo.accumulate("latitude", strings[2]);

                Log.d(TAG, "doInBackground: create json");
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                try {   //  for HttpURLConnection
                    URL url = new URL(strings[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");  //  POST방식
                    conn.setRequestProperty("Cache-Control", "no-cache");        // 컨트롤 캐쉬 설정(?)
                    conn.setRequestProperty("Content-Type", "application/json"); // json형식 전달
                    conn.setRequestProperty("Accept", "application/text");       // text형식 수신
                    conn.setRequestProperty("Accept", "application/json");       // json형식 수신
                    conn.setDoOutput(true); //  OutputStream으로 POST데이터 전송
                    conn.setDoInput(true);  //  InputStream으로 서버로부터 응답 전달받음
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    //  버퍼생성
                    writer.write(locationInfo.toString());
                    writer.flush();
                    writer.close();
                    //  send Sign In Info to Server...
                    InputStream stream = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        //  readLine : string or null(if end of data...)
                        buffer.append(line);
                        Log.d(TAG, "doInBackground: readLine, " + line);
                    }
                    return buffer.toString();
                } catch (MalformedURLException e) {
                    //  이상한 URL일 때
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!result.equals("ok")) {
                try {   //  새로운 목적지 정보가 도착한다면...
                    JSONObject jsonObject = new JSONObject(result);
                    destPoint = new TMapPoint(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude")); //  도착 포인트
                    Toast.makeText(getApplicationContext(), destPoint.toString(), Toast.LENGTH_SHORT).show();
                    /*  경로 탐색 시작 */
//                    new FindPathData().execute(); //  FCM에게 위임한다.
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /*  curPoint와 destPoint로 경로 탐색 후, turnType과 위치 array에 저장과 맵에 경로 표시해주는 작업 수행*/
    public class FindPathData extends AsyncTask<String, Void, Document> {
        String TAG = "FindPathData>>>";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pathlist = new ArrayList<PathItem>();

            /*  새로운 PloyLine을 위해 기존의 view제거*/
            linearLayoutTmap.removeView(tMapView);

            tMapView = new TMapView(getApplicationContext());
            tMapView.setSKTMapApiKey("85bd1e2c-d3c1-4bbf-93ca-e1f3abbc5788\n");
            tMapView.setCenterPoint(curPoint.getLongitude(), curPoint.getLatitude());

            /*  기존의 polyLine제거*/
            tMapView.removeAllTMapPolyLine();
            polyLine = new TMapPolyLine();
            polyLine.setLineWidth(2);
            polyLine.setLineColor(Color.BLUE);
        }

        @Override
        protected Document doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: start!");
            Document document = null;
            try {
                document = new TMapData().findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, curPoint, destPoint);  //  send find query to TMapServer..
                return document;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "경로탐색에 실패 하였습니다.", Toast.LENGTH_LONG).show();
            return null;    //  if failed...
        }

        @Override
        protected void onPostExecute(Document doc) {
            if (doc != null) {
                /*  Parse by turntype and point information */
                XPathFactory xPathFactory = XPathFactory.newInstance();
                XPath xPath = xPathFactory.newXPath();
                try {
                    XPathExpression expr = xPath.compile("//Placemark");
                    NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        NodeList child = nodeList.item(i).getChildNodes();
                        int turnType = -1;
                        TMapPoint point;

                        for (int j = 0; j < child.getLength(); j++) {
                            Node node = child.item(j);
                            if (node.getNodeName().equals("tmap:turnType")) {
                                turnType = Integer.parseInt(node.getTextContent());
                            }
                            if (node.getNodeName().equals("Point")) {
                                String[] str = node.getTextContent().split(",");
                                point = new TMapPoint(Double.parseDouble(str[1]), Double.parseDouble(str[0]));
                                PathItem pathItem = new PathItem(turnType, point);
                                pathlist.add(pathItem);
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pathPoint = new StringBuilder();
                for (int i = 0; i < pathlist.size(); i++) {
                    pathPoint.append(pathlist.get(i).toString() + "\n");
                }
                pathtext.setText(pathPoint.toString());

                /*  Parse and draw PloyLine on TMapView*/
                NodeList list = doc.getElementsByTagName("LineString");
                for (int i = 0; i < list.getLength(); i++) {

                    Element item = (Element) list.item(i);
                    String str = HttpConnect.getContentFromNode(item, "coordinates");
                    if (str != null) {
                        String[] str2 = str.split(" ");
                        for (int k = 0; k  < str2.length; k++) {
                            try {
                                String[] str3 = str2[k].split(",");
                                TMapPoint point = new TMapPoint(Double.parseDouble(str3[1]), Double.parseDouble(str3[0]));
                                polyLine.addLinePoint(point);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        tMapView.addTMapPolyLine("path", polyLine);
                    }
                }
            }
            pathlistIdx = 0;

            linearLayoutTmap.addView(tMapView);
        }
    }

    /*  BluetoothSPP for HC-06  */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("FCM_MESSAGE_RECEIVED", "onNewIntent: ");
        if (intent != null) {
            String longitude = intent.getStringExtra("longitude");
            String latitude = intent.getStringExtra("latitude");
            if (longitude.isEmpty() || latitude.isEmpty()) {
                Toast.makeText(getApplicationContext(), "목적지 설정 오류...", Toast.LENGTH_LONG).show();
                return;
            }
            destPoint = new TMapPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
            new FindPathData().execute();
        }
        super.onNewIntent(intent);
    }
}
