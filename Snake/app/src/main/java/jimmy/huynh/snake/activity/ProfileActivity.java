package jimmy.huynh.snake.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.nifcloud.mbaas.core.DoneCallback;
import com.nifcloud.mbaas.core.NCMBAcl;
import com.nifcloud.mbaas.core.NCMBException;
import com.nifcloud.mbaas.core.NCMBFile;
import com.nifcloud.mbaas.core.NCMBQuery;
import com.nifcloud.mbaas.core.NCMBUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import jimmy.huynh.snake.R;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_thumb)
    ImageView ivThumb;
    @BindView(R.id.edt_username)
    EditText edtUserName;
    @BindView(R.id.edt_mail)
    EditText edtMail;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.btn_reset_pass)
    Button btnResetPass;
    private ProgressDialog progressDialog;

    private String public_path = "https://mbaas.api.nifcloud.com/2013-09-01/applications/{1}/publicFiles/{2}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        public_path = public_path.replace("{1}", getString(R.string.APPLICATION_ID));
        public_path = public_path.replace("{2}", NCMBUser.getCurrentUser().getUserName() + ".jpg");

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        //Initial view here...
        initView();

        btnUpdate.setOnClickListener(this);
        ivThumb.setOnClickListener(this);
        btnResetPass.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                updateInfo();
                break;
            case R.id.iv_thumb:

                if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        chooseImage();
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(ProfileActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                44);
                    }
                } else {
                    chooseImage();
                }
                break;
            case R.id.btn_reset_pass:
                resetPassword();
                break;
        }
    }

    private void resetPassword() {
        progressDialog.show();
        final NCMBUser ncmbUser = NCMBUser.getCurrentUser();
        if (null != ncmbUser.getMailAddress()) {
            NCMBUser.requestPasswordResetInBackground(ncmbUser.getMailAddress(), new DoneCallback() {
                @Override
                public void done(NCMBException error) {
                    progressDialog.dismiss();
                    if(error != null ){
                        Toast.makeText(getBaseContext(), "Reset password error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        // 送信後処理
                        Toast.makeText(getBaseContext(), "Please check mail in " + ncmbUser.getMailAddress() + " to reset password", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(getBaseContext(), "Your account do not have email address.", Toast.LENGTH_LONG).show();
        }

    }

    private void initView() {
        progressDialog.show();
        NCMBUser ncmbUser = NCMBUser.getCurrentUser();
        if (null != ncmbUser.getUserName()) {
            edtUserName.setText(ncmbUser.getUserName());
        }
        if (null != ncmbUser.getMailAddress()) {
            edtMail.setText(ncmbUser.getMailAddress());
        }

        String thumb = ncmbUser.getString("Thumb");
        if (null != thumb) {
            loadImage(thumb);
        }
        progressDialog.dismiss();
    }

    private void loadImage(String img) {
        img = img.trim();
        Glide.with(ProfileActivity.this).load(img)
                .placeholder(R.drawable.holder)
                .fitCenter()
                .circleCrop()
                .signature(new ObjectKey(System.currentTimeMillis()))
                .into(ivThumb);
    }

    private void updateInfo() {
        progressDialog.show();
        NCMBUser ncmbUser = NCMBUser.getCurrentUser();
        ncmbUser.setMailAddress(edtMail.getText().toString());
        ncmbUser.setUserName(edtUserName.getText().toString());

        ncmbUser.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                progressDialog.dismiss();
                if (e != null) {
                    Toast.makeText(getBaseContext(), "Update info error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "Update info successfully!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void chooseImage() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //to get image and videos, I used a */"
        i.setType("image/jpg");
        startActivityForResult(i, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String imagepath = getPath(selectedImageUri);
            File imageFile = new File(imagepath);
            byte[] bytes = getData(imageFile);

            uploadFile(NCMBUser.getCurrentUser().getUserName() + ".jpg", bytes);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 44: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    chooseImage();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ProfileActivity.this, "Can not select image", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    private void uploadFile(String name, final byte[] data) {
        progressDialog.show();
        try {
            NCMBFile ncmbFile = new NCMBFile(name, data, new NCMBAcl());
            ncmbFile.saveInBackground(new DoneCallback() {
                @Override
                public void done(NCMBException e) {
                    progressDialog.dismiss();
                    if (e != null) {
                        //失敗
                        Toast.makeText(ProfileActivity.this, "Can not upload file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        //成功
                        // Fetch data and update view

                        NCMBUser ncmbUser = NCMBUser.getCurrentUser();
                        try {
                            ncmbUser.put("Thumb", public_path);
                            ncmbUser.saveInBackground(new DoneCallback() {
                                @Override
                                public void done(NCMBException e) {
                                    if (null != e) {
                                        Toast.makeText(ProfileActivity.this, "Can not upload file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    } else {
                                        loadImage(public_path);
                                    }
                                }
                            });
                        } catch (NCMBException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        } catch (NCMBException e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }
    }

    private byte[] getData(File file) {
        byte[] bytes = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            for (int readNum; (readNum = fis.read(buf)) != -1; ) {

                bos.write(buf, 0, readNum);
                System.out.println("read " + readNum + " bytes,");
            }
            bytes = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

}
