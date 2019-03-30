package com.macardo.mycontentprovider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private List<String> mContactList = new ArrayList<>();
    private static final int REQ_CODE_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initAdapter();
        checkContactPermission();
    }

    /**
     * 检查并申请联系人的权限
     */
    private void checkContactPermission() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            //未获取到读取联系人的权限
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},REQ_CODE_CONTACT);
        }else {
            //获取到了读取联系人的权限
            query();
        }
    }

    private void query() {
        ContentResolver contentResolver = this.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        //Uri：content://com.android.contacts/data/phones
        Log.d("MainActivity",ContactsContract.CommonDataKinds.Phone.CONTENT_URI.toString());
        if (cursor != null){
            while(cursor.moveToNext()) {
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mContactList.add("name = " + displayName + " number = " + number);
            }
            mAdapter.notifyDataSetChanged();
            cursor.close();
        }
    }

    private void initAdapter() {
        mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mContactList);
        mListView.setAdapter(mAdapter);
    }

    private void initViews() {
        mListView = findViewById(R.id.listview);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_CODE_CONTACT && grantResults.length >0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //回调中获取到了读取联系人的权限
            query();
        }else {
            Toast.makeText(this,"未获取到读取联系人的权限",Toast.LENGTH_SHORT).show();
        }
    }
}
