package com.example.ckddn.capstoneproject2018_2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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


// 내 정보 수정하기 기능 관련 class by ckddn
//  getStringExtra id
public class EditParentInfo extends AppCompatActivity {
    String id;  //  from ParentActivity
    EditText oldpw;
    EditText newpw;
    EditText confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_parent_info);

        id = getIntent().getStringExtra("id");
        oldpw = (EditText) findViewById(R.id.p_pw_now);
        newpw = (EditText) findViewById(R.id.p_pw_new);
        confirm = (EditText) findViewById(R.id.p_pw_confirm);


        Button cPwBtn = (Button) findViewById(R.id.cPwBtn);
        cPwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFilled()) {
                    new EditTask().execute("http://" + ServerInfo.ipAddress + "/parent/edit");
                }
            }
        });
    }

    /*  check that Password editTexts are filled  */
    private boolean isFilled() {
        if (oldpw.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "please fill the current password text", Toast.LENGTH_LONG).show();
            return false;
        }
        if (newpw.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "please fill the new password text", Toast.LENGTH_LONG).show();
            return false;
        }
        if (confirm.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "please fill the confirm password text", Toast.LENGTH_LONG).show();
            return false;
        }
        if ( !(newpw.getText().toString().equals(confirm.getText().toString()))) {
            Toast.makeText(getApplicationContext(), "The passwords do not match", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public class EditTask extends AsyncTask<String, String, String> {
        String TAG = "EditTask>>>";

        @Override
        protected String doInBackground(String... strings) {
            try {   //  json accumulate
                JSONObject editObject = new JSONObject();
                editObject.accumulate("id", id);  //  send id
                editObject.accumulate("oldpw", oldpw.getText().toString());  //  send oldpw
                editObject.accumulate("newpw", newpw.getText().toString());  //  send newpw

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
                    writer.write(editObject.toString());
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
            if (result.equals("OK")) {
                finish();   // 변경되었으면 finish();
                Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }

    }

}
