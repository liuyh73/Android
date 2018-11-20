package com.example.liuyh73.storage2;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private LinearLayout loginForm;
    private EditText loginFormUsername;
    private EditText loginFormPassword;
    private LinearLayout registerForm;
    private ImageView registerFormImage;
    private EditText registerFormUsername;
    private EditText registerFormNewPass;
    private EditText registerFormConfirmPass;
    private Button okBtn;
    private Button clearBtn;
    private DBDAO dbDAO;
    private Bitmap portrait;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // radioGroup
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroupCheckedChangeListener());

        // loginForm
        loginForm = findViewById(R.id.loginForm);
        // loginFormUsername
        loginFormUsername = findViewById(R.id.loginFormUsername);
        // loginFormNewPass
        loginFormPassword = findViewById(R.id.loginFormPassword);
        // registerForm
        registerForm = findViewById(R.id.registerForm);
        // registerFormImage
        registerFormImage = findViewById(R.id.registerFormImage);
        registerFormImage.setOnClickListener(new RegisterFormImageClickListener());
        // registerFormUsername
        registerFormUsername = findViewById(R.id.registerFormUsername);
        // registerFormNewPass
        registerFormNewPass = findViewById(R.id.registerFormNewPass);
        // registerFormConfirmPass
        registerFormConfirmPass = findViewById(R.id.registerFormConfirmPass);

        // okBtn
        okBtn = findViewById(R.id.ok);
        okBtn.setOnClickListener(new OkBtnListener());
        // clearBtn
        clearBtn = findViewById(R.id.clear);
        clearBtn.setOnClickListener(new ClearBtnListener());

        // dbDAO
        dbDAO = new DBDAO(MainActivity.this);
    }

    class RadioGroupCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int index) {
            if (index == R.id.loginRadio) {
                registerForm.setVisibility(View.GONE);
                loginForm.setVisibility(View.VISIBLE);
                loginFormUsername.setText(registerFormUsername.getText());
            } else {
                registerForm.setVisibility(View.VISIBLE);
                loginForm.setVisibility(View.GONE);
                registerFormUsername.setText(loginFormUsername.getText());
            }
        }
    }

    class OkBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(loginForm.getVisibility() == View.VISIBLE) {
                if (TextUtils.isEmpty(loginFormUsername.getText())) {
                    Toast.makeText(MainActivity.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(loginFormPassword.getText())) {
                    Toast.makeText(MainActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    if (dbDAO.get(loginFormUsername.getText().toString()) == null) {
                        Toast.makeText(MainActivity.this, "Username not exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        User user = dbDAO.get(loginFormUsername.getText().toString());
                        if (!user.getPassword().equals(loginFormPassword.getText().toString())) {
                            Toast.makeText(MainActivity.this, "Invalid Password.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Login successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, CommentActivity.class);
                            intent.putExtra("user", loginFormUsername.getText().toString());
                            clear();
                            startActivity(intent);
                        }
                    }
                }
            } else {
                if (TextUtils.isEmpty(registerFormUsername.getText())) {
                    Toast.makeText(MainActivity.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(registerFormNewPass.getText())) {
                    Toast.makeText(MainActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    if(!registerFormNewPass.getText().toString().equals(registerFormConfirmPass.getText().toString())){
                        Toast.makeText(MainActivity.this, "Password Mismatch.", Toast.LENGTH_SHORT).show();
                    } else {
                        User user = dbDAO.get(registerFormUsername.getText().toString());
                        if(user != null) {
                            Toast.makeText(MainActivity.this, "Username already existed.", Toast.LENGTH_SHORT).show();
                        } else {
                            if(portrait == null) {
                                portrait = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.mipmap.me);
                            }
                            //Log.i("registerPortrait", portrait.toString());
                            dbDAO.insert(new User(registerFormUsername.getText().toString(), registerFormNewPass.getText().toString(), portrait));
                            Toast.makeText(MainActivity.this, "Register successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, CommentActivity.class);
                            intent.putExtra("user", registerFormUsername.getText().toString());
                            clear();
                            startActivity(intent);
                        }
                    }
                }
            }
        }
    }

    class ClearBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            loginFormUsername.setText(null);
            registerFormUsername.setText(null);
            clear();
        }
    }

    public void clear(){
        loginFormPassword.setText(null);
        registerFormNewPass.setText(null);
        registerFormConfirmPass.setText(null);
        registerFormImage.setImageResource(R.mipmap.add);
    }

    class RegisterFormImageClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri originalUri = data.getData();
            portrait = BitmapFactory.decodeResource(this.getResources(), R.mipmap.me);
            File file = null;
            if (originalUri != null) {
                file = getFileFromMediaUri(MainActivity.this, originalUri);
            }
            try{
                portrait = getBitmapFormUri(MainActivity.this, originalUri);
            } catch (IOException e){
                Log.i("IOException", e.getMessage());
            }
            portrait = rotateBitmapByDegree(portrait, getBitmapDegree(file.getAbsolutePath()));
            registerFormImage.setImageBitmap(portrait);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    /**
     * 通过Uri获取文件
     * @param ac
     * @param uri
     * @return
     */
    public static File getFileFromMediaUri(Context ac, Uri uri) {
        if(uri.getScheme().toString().compareTo("content") == 0){
            ContentResolver cr = ac.getContentResolver();
            Cursor cursor = cr.query(uri, null, null, null, null);// 根据Uri从数据库中找
            if (cursor != null) {
                cursor.moveToFirst();
                String filePath = cursor.getString(cursor.getColumnIndex("_data"));// 获取图片路径
                cursor.close();
                if (filePath != null) {
                    return new File(filePath);
                }
            }
        }else if(uri.getScheme().toString().compareTo("file") == 0){
            return new File(uri.toString().replace("file://",""));
        }
        return null;
    }


    /**
     * 通过uri获取图片并进行压缩
     * @param uri
     */
    public static Bitmap getBitmapFormUri(Context ac, Uri uri) throws IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x400为标准
        float hh = 400f;//这里设置高度为400f
        float ww = 400f;//这里设置宽度为400f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }

    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
