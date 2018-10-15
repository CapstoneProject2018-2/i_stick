package com.example.ckddn.capstoneproject2018_2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


//Parent Mode로 로그인 후 보이는 메인 화면
//implemented by 양인수
//network code implemented by 손창우

public class ParentActivity extends AppCompatActivity {

    ListView contact_listview = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);


        Button logOutBtn = (Button) findViewById(R.id.logout);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentActivity.this, ParentMenu.class);
                startActivity(intent);
            }
        });


        final ArrayList<ContactListViewItem> items = new ArrayList<ContactListViewItem>();

        final ContactListViewAdapter adapter;
        adapter = new ContactListViewAdapter();
        contact_listview = (ListView) findViewById(R.id.clistview);
        contact_listview.setAdapter(adapter);


        //부모 모드에서 관리대상 사용자 추가 할때
        Button addButton = (Button) findViewById(R.id.c_add);
        addButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddUserDialog();
            }
        });
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.parent_icon), "Iron Man", "010-1111-1111");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.parent_icon), "Iron Man", "010-1111-1111");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.parent_icon), "Iron Man", "010-1111-1111");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.parent_icon), "Iron Man", "010-1111-1111");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.parent_icon), "Iron Man", "010-1111-1111");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.parent_icon), "Iron Man", "010-1111-1111");


        Button deleteButton = (Button) findViewById(R.id.c_delete);
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count;
                int checked;
                count = adapter.getCount();
                if (count > 0) {
                    checked = contact_listview.getCheckedItemPosition();
                    if (checked > -1 && checked < count) {
                        items.remove(checked);
                        contact_listview.clearChoices();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        final EditText editTextFilter = (EditText) findViewById(R.id.editTextFilter);
        editTextFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable edit) {
                String filterText = edit.toString();
                if (filterText.length() > 0)
                    contact_listview.setFilterText(filterText);
                else
                    contact_listview.clearTextFilter();

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });

        //리스트의 아이템 클릭시 핸들러
        contact_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ParentActivity.this, ParentMenu.class);
                startActivity(intent);

            }
        });


    }

    void AddUserDialog() {
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
                dialog.dismiss();

                //String으로 변환해서 id, pw에 저장하였음
                //여기에 서버로 넘기는 코드 짜면 됨

            }
        });

        dialog.show();
    }
}
