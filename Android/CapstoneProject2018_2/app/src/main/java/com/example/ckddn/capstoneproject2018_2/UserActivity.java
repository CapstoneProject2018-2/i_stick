package com.example.ckddn.capstoneproject2018_2;

import android.Manifest;
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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
    private TextView mConnectionStatus;                 /*  연결 상태 출력 텍스트    */
    private final int REQUEST_BLUETOOTH_ENABLE = 100;   /*  블루투스를 키도록 하는 Dialog intent 전달 값 */
    private ConnectedTask mConnectedTask = null;        /*  */
    private String mConnectedDeviceName = null;         /*  HC-06   */
    static BluetoothAdapter mBluetoothAdapter;          /**/
    static boolean isConnectionError = false;           /**/
    private static final String TAG = "BluetoothClient";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        /* get user info from login page    */
        uno = getIntent().getStringExtra("no");
        userId = getIntent().getStringExtra("id");

        textView = (TextView) findViewById(R.id.user_location_result);
        textView.setText(userId + ": 위치정보 미수신중"); //DEFAULT

        /*  init TMap variables */
        linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(getApplicationContext());
        tMapView.setSKTMapApiKey("85bd1e2c-d3c1-4bbf-93ca-e1f3abbc5788\n");
        pathtext = (TextView) findViewById(R.id.path_text); //  경로에 대한 포인트의 정보 출력 뷰
        pathtext.setMovementMethod(new ScrollingMovementMethod());
        polyLine = new TMapPolyLine();
        polyLine.setLineWidth(2);
        polyLine.setLineColor(Color.BLUE);


        /*  Initialize Location Manager    */
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
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {    /*  뒤로 가기로 화면이 꺼지면 LocationManager 서비스 종료*/
        super.onDestroy();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(mLocationListener);
        /*  Arduino */
        if (mConnectedTask != null) {
            mConnectedTask.cancel(true);
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            curPoint = new TMapPoint(location.getLatitude(), location.getLongitude());
            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //  경도
            double latitude = location.getLatitude();   //  위도

            String provider = location.getProvider();   //위치제공자

            /* 이곳에 네트워크에 위치 보내는 코드 작성 */
            new SendLocTask().execute("http://" + ServerInfo.ipAddress + "/user", longitude + "", latitude + "");

            /* implement algorithm...  */
            if (pathlist != null) {                 //  when pathlist is initialized by FindPathData
                if (pathlistIdx < pathlist.size()) {
                    double distance = MapUtils.getDistance(pathlist.get(pathlistIdx).getPoint(), curPoint);
//                    Toast.makeText(getApplicationContext(), "distance: " + distance + "  idx: " + pathlistIdx, Toast.LENGTH_LONG).show();
                    if (distance < 10) { //  10m 이내면
                        Toast.makeText(getApplicationContext(), pathlist.get(pathlistIdx).getTurnType() + "", Toast.LENGTH_LONG).show();
                        /*  send turnType to Arduino    */
                        String sendMessage = pathlist.get(pathlistIdx).getTurnType() + "";  //  turnType을 String 형으로 아두이노에 전송
                        if (sendMessage.length() > 0) {
                            sendMessage(sendMessage);
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
//                    new FindPathData().execute();
                    /*  Replacing with using FCM  */
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
            tMapView.setCenterPoint(curPoint.getLongitude(), curPoint.getLatitude());

            /*  Arduino Setups...   */
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    mConnectionStatus = (TextView) findViewById(R.id.connection_status_textview); // 연결 확인
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {    //  블루투스 기능이 존재하지 않는 장치일 때
                        showErrorDialog("블루트스가 지원되지 않는 장치 입니다.");
                        return;
                    }
                    if (!mBluetoothAdapter.isEnabled()) {   //  블루투스 기능이 켜져 있지 않다면
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //  블루투스를 허용할 것인지 물어보는 dialog생성
                        startActivityForResult(intent, REQUEST_BLUETOOTH_ENABLE);
                    } else {
                        Log.d("Arduino>>>", "Initialisation successful.");
                        showPairedDevicesListDialog();
                    }
                }
            }.start();
            /*  나중에 새로운 경로가 지정될 때 이 쓰레드를 종료하고 새로운 연결을 생성해주어야 한다 */
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
//                Log.d("if문통과>>>>>", "run: ");
                for (int i = 0; i < list.getLength(); i++) {
//                    Log.d("for문>>>>>", "run: ");

                    Element item = (Element) list.item(i);
                    String str = HttpConnect.getContentFromNode(item, "coordinates");
                    if (str != null) {
                        String[] str2 = str.split(" ");
//                        Log.d("포인트로 나눔>>>>>", "run: ");
                        for (int k = 0; k < str2.length; k++) {
                            try {
                                String[] str3 = str2[k].split(",");
                                TMapPoint point = new TMapPoint(Double.parseDouble(str3[1]), Double.parseDouble(str3[0]));
//                                pathPoint.append("(" + i + ", "+ k + ")" + point.toString() + "\n");
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

    /*  블루투스 연결 가능한 리스트 생성 함수  */
    public void showPairedDevicesListDialog() {
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        final BluetoothDevice[] pairedDevices = devices.toArray(new BluetoothDevice[0]);

        if (pairedDevices.length == 0) {   // 등록된 블루투스가 존재하지 않으면 종료
            showQuitDialog("No devices have been paired.\n"
                    + "You must pair it with another device.");
            return;
        }

        String[] items = new String[pairedDevices.length];
        for (int i = 0; i < pairedDevices.length; i++) {
            if (pairedDevices[i].getName().equals("HC-06")) {   //  HC-06(아두이노 블루투스 디바이스)가 등록되어 있다면
                ConnectTask task = new ConnectTask(pairedDevices[i]);
                task.execute(); //  연결
            }
        }
    }

    public void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isConnectionError) {
                    isConnectionError = false;
                    finish();
                }
            }
        });
        builder.create().show();
    }


    /*  블루투스 연결이 되지 않았을 때   */
    public void showQuitDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

    /* 블루투스 연결 Task */
    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {
        String TAG = "ConnectTask";
        private BluetoothSocket mBluetoothSocket = null;
        private BluetoothDevice mBluetoothDevice = null;

        ConnectTask(BluetoothDevice bluetoothDevice) {
            mBluetoothDevice = bluetoothDevice;
            mConnectedDeviceName = bluetoothDevice.getName();   //  아두이노 블루투스 디바이스 HC-06

            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");    //  범용 SPP 모듈

            try {
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                Log.d(TAG, "create socket for " + mConnectedDeviceName);
            } catch (IOException e) {
                Log.e(TAG, "socket create failed " + e.getMessage());
            }
            mConnectionStatus.setText("connecting...");
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "doInBackground: start!");
            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mBluetoothSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mBluetoothSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " +
                            " socket during connection failure", e2);
                }

                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean isSucess) {

            if (isSucess) {
                connected(mBluetoothSocket);
            } else {

                isConnectionError = true;
                Log.d(TAG, "Unable to connect device");
                showErrorDialog("Unable to connect device");
            }
        }
    }

    /*  Connected Task*/
    private class ConnectedTask extends AsyncTask<Void, String, Boolean> {

        private InputStream mInputStream = null;
        private OutputStream mOutputStream = null;
        private BluetoothSocket mBluetoothSocket = null;
        private String TAG = "ConnectedTask";

        ConnectedTask(BluetoothSocket socket) {

            mBluetoothSocket = socket;
            try {
                mInputStream = mBluetoothSocket.getInputStream();
                mOutputStream = mBluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "socket not created", e);
            }

            Log.d(TAG, "connected to " + mConnectedDeviceName);
            mConnectionStatus.setText("connected to " + mConnectedDeviceName);
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "doInBackground: start!");
            byte[] readBuffer = new byte[1024];
            int readBufferPosition = 0;


            while (true) {

                if (isCancelled()) return false;

                try {

                    int bytesAvailable = mInputStream.available();

                    if (bytesAvailable > 0) {

                        byte[] packetBytes = new byte[bytesAvailable];

                        mInputStream.read(packetBytes);

                        for (int i = 0; i < bytesAvailable; i++) {

                            byte b = packetBytes[i];
                            if (b == '\n') {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0,
                                        encodedBytes.length);
                                String recvMessage = new String(encodedBytes, "UTF-8");

                                readBufferPosition = 0;

                                Log.d(TAG, "recv message: " + recvMessage);
                                publishProgress(recvMessage);
                            } else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                } catch (IOException e) {

                    Log.e(TAG, "disconnected", e);
                    return false;
                }
            }

        }

        @Override
        protected void onPostExecute(Boolean isSucess) {
            super.onPostExecute(isSucess);

            if (!isSucess) {


                closeSocket();
                Log.d(TAG, "Device connection was lost");
                isConnectionError = true;
                showErrorDialog("Device connection was lost");
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);

            closeSocket();
        }

        void closeSocket() {

            try {

                mBluetoothSocket.close();
                Log.d(TAG, "close socket()");

            } catch (IOException e2) {

                Log.e(TAG, "unable to close() " +
                        " socket during connection failure", e2);
            }
        }

        void write(String msg) {

            msg += "\n";

            try {
                mOutputStream.write(msg.getBytes());
                mOutputStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "Exception during send", e);
            }
        }
    }

    public void connected(BluetoothSocket socket) {
        mConnectedTask = new ConnectedTask(socket);
        mConnectedTask.execute();
    }

    /*  Send Message(turnType) to ino   */
    void sendMessage(String msg) {
        if (mConnectedTask != null) {
            mConnectedTask.write(msg);
            Log.d(TAG, "send message: " + msg);
        }
    }

    /*   onActivityResult   */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BLUETOOTH_ENABLE) {    /* dialog: 블루투스를 허용 하시겠습니까? */
            if (resultCode == RESULT_OK) {   //  예
                //BlueTooth is now Enabled
                showPairedDevicesListDialog();
            }
            if (resultCode == RESULT_CANCELED) {  //  아니오
                showQuitDialog("You need to enable bluetooth");
            }
        }
    }


    /*  When message received from FCM
    * newIntent from LocFirebaseMessagingService
    * get destination and FindPath
    * */
    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            String longitude = intent.getStringExtra("longitude");
            String latitude = intent.getStringExtra("latitude");
            if (longitude.isEmpty() || latitude.isEmpty()) {    //  도착지점의 정보가 오지 않는 경우
                Toast.makeText(getApplicationContext(), "not destination data", Toast.LENGTH_LONG).show();
                return;
            }
            destPoint = new TMapPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
            new FindPathData().execute();
        }
        super.onNewIntent(intent);
    }
}
