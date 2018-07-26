package com.training.android.app.contactsapp;

import java.util.ArrayList;

import android.Manifest;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

    private ArrayList<String> conNames = new ArrayList<>();
    private ArrayList<String> conNumbers = new ArrayList<>();
    private Cursor mCursorContacts;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static final int ADD_CONTACT_REQ_CODE = 101;
    private boolean mIsPermissionGranted;
    private MyAdapter mContactsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            mIsPermissionGranted = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsPermissionGranted)
            init();
    }

    private void init() {
        mCursorContacts = ContactHelper.getContactCursor(getContentResolver(), "");
        mCursorContacts.moveToFirst();

        conNames.clear();
        conNumbers.clear();

        while (!mCursorContacts.isAfterLast()) {
            conNames.add(mCursorContacts.getString(1));
            conNumbers.add(mCursorContacts.getString(2));
            mCursorContacts.moveToNext();
        }

        if (mContactsAdapter == null) {
            mContactsAdapter = new MyAdapter(this, android.R.layout.simple_list_item_1,
                    R.id.tvNameMain, conNames);
            setListAdapter(mContactsAdapter);
        }
        mContactsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                    mIsPermissionGranted = true;
                } else {
                    Toast.makeText(this, "Please grant the permission", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }

        }
    }

    private class MyAdapter extends ArrayAdapter<String> {

        public MyAdapter(Context context, int resource, int textViewResourceId,
                         ArrayList<String> conNames) {
            super(context, resource, textViewResourceId, conNames);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = setList(position, parent);
            return row;
        }

        private View setList(int position, ViewGroup parent) {
            LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inf.inflate(R.layout.liststyle, parent, false);

            TextView tvName = (TextView) row.findViewById(R.id.tvNameMain);
            TextView tvNumber = (TextView) row.findViewById(R.id.tvNumberMain);

            tvName.setText(conNames.get(position));
            tvNumber.setText("No: " + conNumbers.get(position));

            return row;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater imf = getMenuInflater();
        imf.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item1) {
            Intent intent = new Intent(MainActivity.this, AddContact.class);
            startActivityForResult(intent, ADD_CONTACT_REQ_CODE);
        } else if (item.getItemId() == R.id.item2) {
            Intent intent = new Intent(MainActivity.this, DeleteContacts.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CONTACT_REQ_CODE && data != null) {
            boolean isSuccess = data.getBooleanExtra("add", false);
            if (isSuccess)
                init();
        }
    }
}
