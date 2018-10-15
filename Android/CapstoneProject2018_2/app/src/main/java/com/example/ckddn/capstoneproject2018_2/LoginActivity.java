package com.example.ckddn.capstoneproject2018_2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

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

//어플리케이션 첫 화면 로그인 or 등록 화면
//implemented by 손창우
//modifed by 양인수

public class LoginActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_REG = 101;
    private EditText id;
    private EditText password;
    private RadioButton user_radio;
    private RadioButton parent_radio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        user_radio = (RadioButton) findViewById(R.id.user_radio_btn);
        parent_radio = (RadioButton) findViewById(R.id.parent_radio_btn);

        final Button signInBut = (Button) findViewById(R.id.sign_in_button);
        signInBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new SignInTask().execute("http://192.168.0.14:5555/login", id.getText().toString(), password.getText().toString());

                //intended for off-line coding
                //does not request for correctness to server (free pass)
                signInBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoginActivity.this, ParentActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        Button regBut = (Button) findViewById(R.id.register_button);
        regBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(getApplicationContext(), RegistActivity.class);
                startActivityForResult(regIntent, REQUEST_CODE_REG);
//                new RegistTask().execute();
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
                    signInIntent.setClass(getApplicationContext(), UserActivity.class);
                }
                else if (parent_radio.isChecked()) {
//                    Log.d(TAG, "onPostExecute: parent mode login");
                    signInIntent.setClass(getApplicationContext(), ParentActivity.class);
                }

                signInIntent.putExtra("id", jsonObject.getString("id"));
                startActivity(signInIntent);
            } catch (JSONException e) { //  JSON형식이 아니라면 ERROR
                Log.d(TAG, "onPostExecute: " + result);
                e.printStackTrace();
            }
        }
    }

//    public class RegistTask extends AsyncTask<String, String, String> {
//        @Override
//        protected String doInBackground(String... strings) {
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//        }
//    }

}

