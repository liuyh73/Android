package com.example.liuyh73.storage2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;

public class CommentActivity extends AppCompatActivity {
    private String username;
    private Button sendBtn;
    private EditText comment;
    private List<Comment> commentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter recyclerViewAdapter;
    // final private CommentDAO commentDAO = new CommentDAO(CommentActivity.this);
    final private DBDAO dbDAO = new DBDAO(CommentActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        username = getIntent().getExtras().get("user").toString();
        // recyclerView
        recyclerView = findViewById(R.id.recyclerView);
        final User loginUser = dbDAO.get(username);
        // initial commentList
        commentList.addAll(dbDAO.getAllComments());
        // initial MyRecyclerViewAdapter
        recyclerViewAdapter = new MyRecyclerViewAdapter<Comment>(CommentActivity.this, R.layout.comment, this.commentList) {
            @Override
            public void convert(MyViewHolder holder, final Comment c) {
                // Comment，封装了数据信息，也可以直接将数据做成一个Map，那么这里就是Map<String, Object>
                final TextView commentUsername = holder.getView(R.id.commentUsername);
                commentUsername.setText(c.getCommentUsername());

                final TextView commentTime = holder.getView(R.id.commentTime);
                commentTime.setText(c.getCommentTime());

                final TextView commentContent = holder.getView(R.id.commentContent);
                commentContent.setText(c.getCommentContent());

                final TextView thumbsUpCount = holder.getView(R.id.thumbsUpCount);
                thumbsUpCount.setText(String.valueOf(c.getThumbsUpCount()));

                final User user = dbDAO.get(c.getCommentUsername());
                final List<Comment> userComments = user.getComments();
                ImageView imageViewPortrait = holder.getView(R.id.imageViewPortrait);
                imageViewPortrait.setImageBitmap(user.getPortrait());
                final ImageView thumbsUpImage = holder.getView(R.id.thumbsUpImage);
                thumbsUpImage.setImageResource(R.mipmap.white);
                thumbsUpImage.setTag("white");

                final List<Comment> loginUserThumbsUpComments = loginUser.getThumbsUpComments();
                for(Comment comment : loginUserThumbsUpComments){
                    if(commentUsername.getText().toString().equals(comment.getCommentUsername()) && commentTime.getText().toString().equals(comment.getCommentTime())) {
                        thumbsUpImage.setImageResource(R.mipmap.red);
                        thumbsUpImage.setTag("red");
                        break;
                    }
                }
                thumbsUpImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(thumbsUpImage.getTag() == "white"){
                            thumbsUpImage.setTag("red");
                            thumbsUpImage.setImageResource(R.mipmap.red);
                            thumbsUpCount.setText(String.valueOf(Integer.parseInt(thumbsUpCount.getText().toString()) + 1));
                            c.setThumbsUpCount(Integer.parseInt(thumbsUpCount.getText().toString()));
                            loginUserThumbsUpComments.add(c);
                        } else {
                            thumbsUpImage.setTag("white");
                            thumbsUpImage.setImageResource(R.mipmap.white);
                            thumbsUpCount.setText(String.valueOf(Integer.parseInt(thumbsUpCount.getText().toString()) - 1));
                            c.setThumbsUpCount(Integer.parseInt(thumbsUpCount.getText().toString()));
                            loginUserThumbsUpComments.remove(c);
                        }
                        for(Comment comment : userComments){
                            if(commentUsername.getText().toString().equals(comment.getCommentUsername()) && commentTime.getText().toString().equals(comment.getCommentTime())) {
                                comment.setThumbsUpCount(c.getThumbsUpCount());
                                break;
                            }
                        }
                        dbDAO.updateThumbsUpComments(loginUser);
                        dbDAO.updateComments(user);
                    }
                });
            }
        };

        // 设置LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        // 设置Animation
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(recyclerViewAdapter);
        scaleInAnimationAdapter.setDuration(1000);
        recyclerView.setAdapter(scaleInAnimationAdapter);
        recyclerView.setItemAnimator(new OvershootInLeftAnimator());

        // 设定comment点击监听事件
        recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Comment comment = commentList.get(position);
                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" = \"" + comment.getCommentUsername() + "\"", null, null);
                String phoneNumber = "\nPhone: ";
                if(cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        phoneNumber += cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) + "\n             ";
                    }while (cursor.moveToNext());
                }

                if(phoneNumber.equals("\nPhone: ")) {
                    phoneNumber = "\nPhone number not exists.";
                }
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CommentActivity.this);
                alertDialog.setIcon(R.mipmap.ic_launcher).setTitle("Info").setMessage("Username: "+comment.getCommentUsername() + phoneNumber).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
            @Override
            public void onLongClick(final int position) {
                final Comment comment = commentList.get(position);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CommentActivity.this);
                if(comment.getCommentUsername().equals(username)) {
                    alertDialog.setIcon(R.mipmap.ic_launcher).setTitle("Delete or not").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            commentList.remove(position);
                            recyclerViewAdapter.notifyItemRemoved(position);
                            List<Comment> loginUserComments = loginUser.getComments();
                            loginUserComments.remove(comment);
                            dbDAO.updateComments(loginUser);
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                } else {
                    alertDialog.setIcon(R.mipmap.ic_launcher).setTitle("Report or not").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(CommentActivity.this, "You have report the comment which " + comment.getCommentUsername()+" sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                }
                alertDialog.show();
            }
        });
        comment = findViewById(R.id.comment);
        sendBtn = findViewById(R.id.send);
        sendBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("loginUsername", username);
                if(TextUtils.isEmpty(comment.getText())) {
                    Toast.makeText(CommentActivity.this, "Comment cannot be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    Comment commentNew = new Comment(username, comment.getText().toString(), 0);
                    commentList.add(commentNew);
                    List<Comment> loginUserComments = loginUser.getComments();
                    loginUserComments.add(commentNew);
                    dbDAO.updateComments(loginUser);
                    recyclerViewAdapter.notifyItemInserted(commentList.size()-1);
                    comment.setText(null);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
