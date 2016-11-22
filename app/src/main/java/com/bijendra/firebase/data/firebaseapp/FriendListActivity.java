package com.bijendra.firebase.data.firebaseapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FriendListActivity extends BaseActivity {

    private static final String TAG = "FriendListActivity";
    DatabaseReference root = null;
    Map<String, Object> mapFriend = new HashMap<>();
    ListView lvFriend;
    List<Friend> lstFriend = new ArrayList<>();
    FriendArrayAdapter friendArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        lvFriend = (ListView) findViewById(R.id.lvFriend);

        lstFriend.clear();
        mapFriend.clear();

        friendArrayAdapter = new FriendArrayAdapter();
        lvFriend.setAdapter(friendArrayAdapter);

        root = MyApplication.getDataBaseRef().child("Friends");

        root.child(USER_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mapFriend.clear();
                lstFriend.clear();
                if(dataSnapshot!=null) {
                    Iterator iterator = dataSnapshot.getChildren().iterator();

                    while (iterator.hasNext()) {
                        DataSnapshot p = ((DataSnapshot) iterator.next());
                        Friend frined = p.getValue(Friend.class);
                        mapFriend.put(p.getKey(), frined);
                        lstFriend.add(frined);
                    }
                    friendArrayAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        lvFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              deleteFriendDialog(view.getTag().toString(), ((TextView) view).getText().toString(), i);
            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;

        } else if (item.getItemId() == R.id.action_add) {
            showAdFriendDialog();
            return true;
        }
        else if (item.getItemId() == R.id.action_profile) {
            startActivity(new Intent(this,EditProfileActivity.class));
            this.finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private void showAdFriendDialog() {
        LayoutInflater li = LayoutInflater.from(FriendListActivity.this);
        View promptsView = li.inflate(R.layout.dialog_add_friend, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                FriendListActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText name = (EditText) promptsView
                .findViewById(R.id.etName);
        final EditText age = (EditText) promptsView
                .findViewById(R.id.etAge);
        final EditText add = (EditText) promptsView
                .findViewById(R.id.etAddress);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                addEditFriend("", name.getText().toString(), age.getText().toString(), add.getText().toString());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    private void deleteFriendFromDatabase(final String id, final int position) {
        root.child(USER_ID).child(id).removeValue();


    }

    private void deleteFriendDialog(final String fId, final String name, final int position) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                FriendListActivity.this);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setMessage("Do you want to delete " + name)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                deleteFriendFromDatabase(fId, position);

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }


    private void addEditFriend(String id, String name, String age, String add) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Map<String, Object> mFriend = new HashMap<>();

            if (TextUtils.isEmpty(id)) {

                String _id = root.push().getKey();
                mFriend.put(_id, new Friend(_id, name, age, add));
                root.child(USER_ID).updateChildren(mFriend);

            } else {
                mFriend.put(id, new Friend(id, name, age, add));
                root.child(USER_ID).updateChildren(mFriend);
            }

        }


    }

    class FriendArrayAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return lstFriend.size();
        }

        @Override
        public Object getItem(int i) {
            return lstFriend.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(FriendListActivity.this);
            int padding = getResources().getDimensionPixelSize(R.dimen.dp_10);
            textView.setPadding(padding, padding, padding, padding);
            textView.setText(lstFriend.get(i).username);
            textView.setTag(lstFriend.get(i).userId);
            return textView;
        }
    }

}
