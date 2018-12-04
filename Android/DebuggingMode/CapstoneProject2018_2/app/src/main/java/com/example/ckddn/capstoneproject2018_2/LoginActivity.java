package com.example.ckddn.capstoneproject2018_2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ckddn.capstoneproject2018_2.Oblu.DeviceScanActivity;
import com.google.firebase.iid.FirebaseInstanceId;

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

//implemented by Changwoo Son
//modified by Insu Yang

public class LoginActivity extends AppCompatActivity {
    private EditText id;
    private EditText password;
    private RadioButton user_radio;
    private RadioButton parent_radio;
    private RadioGroup radioGroup;

    @Override
    protected void onStart() {
        super.onStart();
        /* give and check permission(CALL_PHONE) */
        ActivityCompat.requestPermissions(this, IStickInfo.common_permissions, PackageManager.PERMISSION_GRANTED);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        user_radio = (RadioButton) findViewById(R.id.user_radio_btn);
        parent_radio = (RadioButton) findViewById(R.id.parent_radio_btn);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        final Button signInBut = (Button) findViewById(R.id.sign_in_button);
        signInBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((id.getText().toString().equals("") || password.getText().toString().equals("") || radioGroup.getCheckedRadioButtonId() == -1))
                    Toast.makeText(getApplicationContext(), "로그인을 위한 정보를 적어주세요.", Toast.LENGTH_LONG).show();
                else
                    new SignInTask().execute("http://" + IStickInfo.ipAddress +"/login", id.getText().toString(), password.getText().toString());
            }
        });

        Button regBut = (Button) findViewById(R.id.register_button);
        regBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(getApplicationContext(), RegistActivity.class);
                startActivity(regIntent);
            }
        });
    }

    public class SignInTask extends AsyncTask<String, String, String> {
        String TAG = "SignInTask>>>";
        @Override
        protected String doInBackground(String... strings) {
            try {   //  json accumulate
                JSONObject signInInfo = new JSONObject();
                signInInfo.accumulate("id", strings[1].toString());
                signInInfo.accumulate("pw", strings[2].toString());
                if (user_radio.isChecked())   //  user 0
                    signInInfo.accumulate("type", 0);
                else if (parent_radio.isChecked()) //  parent 1
                    signInInfo.accumulate("type", 1);
                /* for FCM messaging*/
                signInInfo.accumulate("token", FirebaseInstanceId.getInstance().getToken());
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
                    writer.write(signInInfo.toString());
                    writer.flush();
                    writer.close();
                    //  send Sign In Info to Server...
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
                JSONObject jsonObject = new JSONObject(result);  //  get json file from server(res.json(data))
                Intent signInIntent = new Intent();
                if (user_radio.isChecked()){
//                    signInIntent.setClass(getApplicationContext(), UserActivity.class);
                    signInIntent.setClass(getApplicationContext(), DeviceScanActivity.class);
                }
                else if (parent_radio.isChecked()) {
                    signInIntent.setClass(getApplicationContext(), ParentActivity.class);
                }
                signInIntent.putExtra("no", jsonObject.getString("no"));
                signInIntent.putExtra("id", jsonObject.getString("id"));
                signInIntent.putExtra("name", jsonObject.getString("name"));
                signInIntent.putExtra("mobile", jsonObject.getString("mobile"));
                startActivity(signInIntent);
            } catch (JSONException e) { //  JSON형식이 아니라면 ERROR
                Log.d(TAG, "onPostExecute: " + result);
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

}


