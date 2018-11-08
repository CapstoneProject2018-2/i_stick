package com.example.ckddn.capstoneproject2018_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

//보호자가 관리 대상을 선정하면 나오는 메뉴
//implemented by 양인수

public class ParentMenu extends AppCompatActivity implements View.OnClickListener {
    private String pno, uno, name, mobile;    //  parent no and managed user no
    private CardView setPath, hisLoc;
    private TextView nameText, mobileText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_menu);

        pno = getIntent().getStringExtra("pno");
        uno = getIntent().getStringExtra("uno");
        name = getIntent().getStringExtra("name");
        mobile = getIntent().getStringExtra("mobile");

        nameText = (TextView) findViewById(R.id.nameText);
        nameText.setText(name);
        mobileText = (TextView) findViewById(R.id.mobileText);
        mobileText.setText(mobile);

//        Toast.makeText(getApplicationContext(),"manage uno: " + uno,Toast.LENGTH_LONG).show();
        setPath = (CardView)findViewById(R.id.set_path);
        hisLoc = (CardView)findViewById(R.id.his_loc);

        setPath.setOnClickListener(this);
        hisLoc.setOnClickListener(this);


        /* implements by ckddn */
        Button deleteBtn = (Button) findViewById(R.id.c_delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("uno", uno);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()){
            case R.id.set_path : intent = new Intent(this,SetPathActivity.class);
                intent.putExtra("uno", uno);    //  for set user location
                startActivity(intent);
                break;
            case R.id.his_loc : intent = new Intent(this, HisLocActivity.class);
                intent.putExtra("uno", uno);    //  for get user location
                intent.putExtra("pno", pno);
                intent.putExtra("userName", name);
                intent.putExtra("userMobile", mobile);
                startActivity(intent);
                break;
            default: break;
        }

    }
}
