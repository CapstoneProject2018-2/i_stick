package com.example.ckddn.capstoneproject2018_2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

//보호자가 관리 대상을 선정하면 나오는 메뉴
//implemented by 양인수

public class ParentMenu extends AppCompatActivity implements View.OnClickListener {
    private String pno, uno;    //  parent no and managed user no
    private CardView setPath, hisLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_menu);

        // 181028
        pno = getIntent().getStringExtra("pno");
        uno = getIntent().getStringExtra("uno");

        Toast.makeText(getApplicationContext(),"manage uno: "+uno,Toast.LENGTH_LONG).show();
        setPath = (CardView)findViewById(R.id.set_path);
        hisLoc = (CardView)findViewById(R.id.his_loc);

        setPath.setOnClickListener(this);
        hisLoc.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()){
            case R.id.set_path : intent = new Intent(this,SetPathActivity.class);
                startActivity(intent);
                break;
            case R.id.his_loc : intent = new Intent(this, HisLocActivity.class);
                intent.putExtra("uno", uno);
                intent.putExtra("pno", pno);
                startActivity(intent);
                break;
            default: break;
        }

    }
}
