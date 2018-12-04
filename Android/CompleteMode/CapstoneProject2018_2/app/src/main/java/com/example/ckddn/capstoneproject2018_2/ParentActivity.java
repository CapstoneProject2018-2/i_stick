package com.example.ckddn.capstoneproject2018_2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


//Parent Mode로 로그인 후 보이는 메인 화면
//implemented by 양인수
//network code implemented by 손창우

public class ParentActivity extends AppCompatActivity {
    // info from LoginActivity
    String pno, pId, pName, pMobile;
    ContactListViewAdapter adapter;
    ListView contact_listview = null;


    /*  implement by ckddn */
    @Override
    public void onBackPressed() {
        // logout 버튼으로만 나갈 수 있다.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        /*  implement by ckddn */
        pno = getIntent().getStringExtra("no");
        pId = getIntent().getStringExtra("id");
        pName = getIntent().getStringExtra("name");
        pMobile = getIntent().getStringExtra("mobile");
        TextView welcome = (TextView) findViewById(R.id.id);
        welcome.setText(pName + "님 환영합니다!");

        Button logOutBtn = (Button) findViewById(R.id.logout);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   //  logout -> finish this activity and go back to login activity
            }
        });

        final ArrayList<ContactListViewItem> items = new ArrayList<ContactListViewItem>();

        adapter = new ContactListViewAdapter();
        contact_listview = (ListView) findViewById(R.id.clistview);
        contact_listview.setAdapter(adapter);

        /*  implement by ckddn  */
        getUserInfo();  //  contactAdapter.addItem()

        /*  implement by ckddn */
        Button editParentInfoBtn = (Button)findViewById(R.id.p_edit);
        editParentInfoBtn.setOnClickListener(new View.OnClickListener() {
            //  parent 내 정보 수정
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentActivity.this, EditParentInfo.class);
                intent.putExtra("id", pId);
                intent.putExtra("name", pName);
                intent.putExtra("mobile", pMobile);
                startActivity(intent);
            }
        });

        //부모 모드에서 관리대상 사용자 추가 할때
        Button addButton = (Button) findViewById(R.id.c_add);
        addButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddUserDialog();
            }
        });

        final EditText editTextFilter = (EditText) findViewById(R.id.editTextFilter);
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {
                String filterText = edit.toString();
                ((ContactListViewAdapter)contact_listview.getAdapter()).getFilter().filter(filterText);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });
//        editTextFilter.clearFocus();
        //리스트의 아이템 클릭시 핸들러
        contact_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ParentActivity.this, ParentMenu.class);
                intent.putExtra("pno", pno);
                ContactListViewItem item = (ContactListViewItem) adapter.getItem(position);
                intent.putExtra("uno", item.getUno());
                intent.putExtra("cName", item.getTitle());
                intent.putExtra("cMobile", item.getDesc());
                intent.putExtra("pName", pName);
                intent.putExtra("pMobile", pMobile);
                startActivityForResult(intent, 1);
            }
        });


    }

    /*  request user info to Server */
    /*  implement by ckddn  */
    private void getUserInfo() {
        new RequestUserInfoTask(adapter).execute("http://" + IStickInfo.ipAddress +"/parent");
    }


    /* delete task request from ParentMenu */
    /* implement by ckddn */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String deleteUno = data.getStringExtra("uno");
            new DeleteUserTask(adapter, deleteUno).execute("http://" + IStickInfo.ipAddress + "/parent/delete");
        }
    }

    /* change to private */
    private void AddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.add_user,null);
        builder.setView(view);

        final EditText userId = (EditText)view.findViewById(R.id.enter_id);
        final EditText userPw = (EditText)view.findViewById(R.id.enter_pw);
        final Button u_addBtn = (Button)view.findViewById(R.id.d_add_btn);

        final AlertDialog dialog = builder.create();

        u_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = userId.getText().toString();
                String pw = userPw.getText().toString();
                /*  implement by ckddn  */
                new AddUserTask(adapter).execute("http://" + IStickInfo.ipAddress + "/parent/register", id, pw);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /* delete user in management listView */
    public class DeleteUserTask extends AsyncTask<String, String, String> {
        private ContactListViewAdapter adapter;
        private String uno;
        String TAG = "DeleteUserTask>>>";
        /*  constructor : needs adapter to add userInfo into ListView   */
        public DeleteUserTask(ContactListViewAdapter adapter, String uno) {
            this.adapter = adapter;
            this.uno = uno;
        }
        @Override
        protected String doInBackground(String... strings) {
            try {   //  json accumulate
                JSONObject requestObject = new JSONObject();
                requestObject.accumulate("pno", pno);
                requestObject.accumulate("uno", uno);

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
                    writer.write(requestObject.toString());
                    writer.flush();
                    writer.close();
                    //  send request to server
                    InputStream stream = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while((line = reader.readLine()) != null) {
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
            // rpu 제거가 성공하였으면
            if (result.equals("ok")) {
                adapter.deleteItem(uno);
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "삭제 완료.",Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    }

    /*  request user info to Server */
    public class RequestUserInfoTask extends AsyncTask<String, String, String> {
        private ContactListViewAdapter adapter;
        String TAG = "RequestUserInfoTask>>>";

        /*  constructor : needs adapter to add userInfo into ListView   */
        public RequestUserInfoTask(ContactListViewAdapter adapter) {   this.adapter = adapter; }

        @Override
        protected String doInBackground(String... strings) {
            try {   //  json accumulate
                JSONObject requestObject = new JSONObject();
                requestObject.accumulate("pno", pno);
                requestObject.accumulate("id", pId);  //  send pno, pId
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
                    writer.write(requestObject.toString());
                    writer.flush();
                    writer.close();
                    //  send request to server
                    InputStream stream = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while((line = reader.readLine()) != null) {
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
            try {
                JSONArray jsonArray = new JSONArray(result);    //  get JSONArray from Server...
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    adapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.parent_icon),jsonObject.getString("no"), jsonObject.getString("name"), jsonObject.getString("mobile"));
                }
            } catch (JSONException e) { //  JSON형식이 아니라면 ERROR
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();    //  맡고있는 user가 없을 시
                e.printStackTrace();
            }
        }

    }

    /*  check user info to Server */
    public class AddUserTask extends AsyncTask<String, String, String> {
        private ContactListViewAdapter adapter;
        String TAG = "AddUserTask>>>";
        /*  constructor : needs adapter to add userInfo into ListView   */
        public AddUserTask(ContactListViewAdapter adapter) {   this.adapter = adapter; }

        @Override
        protected String doInBackground(String... strings) {
            try {   //  json accumulate
                JSONObject requestObject = new JSONObject();
                requestObject.accumulate("pno", pno);
                requestObject.accumulate("id", strings[1]);
                requestObject.accumulate("pw", strings[2]);  //  send pno, userid, userpw
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
                    writer.write(requestObject.toString());
                    writer.flush();
                    writer.close();
                    //  send request to server
                    InputStream stream = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while((line = reader.readLine()) != null) {
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
            try {
                JSONObject userInfo = new JSONObject(result);
                adapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.parent_icon), userInfo.getString("no"), userInfo.getString("name"), userInfo.getString("mobile"));
                Toast.makeText(getApplicationContext(), userInfo.getString("name") + "님을 관리 목록에 추가하였습니다.", Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

    }
}