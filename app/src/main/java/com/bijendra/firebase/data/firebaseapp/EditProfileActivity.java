package com.bijendra.firebase.data.firebaseapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends BaseActivity {

    boolean isSocialMedia=false;

    private static final int RC_IMAGE = 1001;
    private static final String TAG ="SaveRetriveDataActivity" ;
    EditText etName,etAge,etProfession,etEmail;
    SimpleDraweeView myImageView;
    Uri uriImage;
    private String imageUrl="";
    private ProgressDialog pDialog;
    Button butAddEditProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        butAddEditProfile= (Button) findViewById(R.id.butAddEditProfile);
        etName= (EditText) findViewById(R.id.etName);
        etAge= (EditText) findViewById(R.id.etAge);
        etProfession= (EditText) findViewById(R.id.etProfession);
        etEmail= (EditText) findViewById(R.id.etEmail);
        myImageView= (SimpleDraweeView) findViewById(R.id.myImageView);
        showProfileDetail();
        /*if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            etName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            etEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                if(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null)
                {
                    isSocialMedia=true;
                    imageUrl=FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
                    myImageView.setImageURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());

                }
                else {
                    isSocialMedia = false;
                    imageUrl="";
                }
        }*/
    }
    public void showProfileDetail()
    {
        DatabaseReference root=MyApplication.getDataBaseRef().child("Users/"+USER_ID);
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);


                if(user!=null) {
                    etName.setText(user.username);
                    etAge.setText(user.age);
                    etProfession.setText(user.profession);
                    etEmail.setText(user.email);
                    myImageView.setImageURI(Uri.parse(user.imageUrl));
                    butAddEditProfile.setText(getString(R.string.add_profile));

                }
                else
                {
                    if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()!=null)
                      etName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

                    if(FirebaseAuth.getInstance().getCurrentUser().getEmail()!=null)
                     etEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                    if(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null)
                    {
                        isSocialMedia=true;
                        imageUrl=FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
                        myImageView.setImageURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());

                    }
                    else {
                        isSocialMedia = false;
                        imageUrl="";
                    }
                    butAddEditProfile.setText(getString(R.string.edit_profile));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.inner_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_logout)
        {
            logout();
            return true;

        }
        if(item.getItemId()==R.id.action_friend_list)
        {
            startActivity(new Intent(EditProfileActivity.this,FriendListActivity.class));
            this.finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public  void gotoAddValue(View view)
    {
       if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            uploadImage();
        //uploadUserData("");
    }
    private void uploadImage()
    {

        pDialog=new ProgressDialog(this);
        pDialog.setMessage("Loading");
        pDialog.show();
        if(!isSocialMedia) {
            StorageReference mStorage = MyApplication.getStorageRef().child("Photos");
            if (uriImage != null) {
                mStorage.child(uriImage.getLastPathSegment());

                mStorage.putFile(uriImage).addOnSuccessListener(EditProfileActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadUserData(taskSnapshot.getDownloadUrl().toString());

                    }
                })
                        .addOnFailureListener(EditProfileActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, e.getMessage());
                                pDialog.dismiss();
                                Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
            else
                uploadUserData("");
        }
        else
            uploadUserData(imageUrl);


    }
    private void uploadUserData(final String imageUrl)
    {
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference root=MyApplication.getDataBaseRef().child("Users");

        Map<String,Object> map2 = new HashMap<String, Object>();
        map2.put(uid,new User(uid,etEmail.getText().toString(),etName.getText().toString(),
            etAge.getText().toString(),etProfession.getText().toString(),imageUrl));


        root.updateChildren(map2, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
            {
                pDialog.dismiss();
                pDialog=null;
               // Toast.makeText(SaveRetriveDataActivity.this,"Value Added",Toast.LENGTH_LONG).show();
                etName.setText(null);
                etAge.setText(null);
                etProfession.setText(null);
                etEmail.setText(null);
                myImageView.setImageURI(Uri.EMPTY);
                isSocialMedia=false;
                EditProfileActivity.this.imageUrl="";
                startActivity(new Intent(EditProfileActivity.this,FriendListActivity.class));
                EditProfileActivity.this.finish();

            }
        });
    }

    public void gotoRetriveValue(View v)
    {
        DatabaseReference root=MyApplication.getDataBaseRef().child("Users/"+etName.getText().toString());
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                etName.setText(user.username);
                etAge.setText(user.age);
                etProfession.setText(user.profession);
                etEmail.setText(user.email);
                myImageView.setImageURI(Uri.parse(user.imageUrl));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void gotoSelectImage(View v)
    {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,RC_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_IMAGE && resultCode==RESULT_OK)
        {
            uriImage=data.getData();
            myImageView.setImageURI(uriImage);

        }
    }


}
