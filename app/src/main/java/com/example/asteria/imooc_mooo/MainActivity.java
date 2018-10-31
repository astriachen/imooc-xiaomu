package com.example.asteria.imooc_mooo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.imooc.mooo.bean.ChatMessage;
import com.imooc.mooo.utils.HttpUtils;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView mMsgs;
    private ChatMessageAdapter mAdapter;
    private List<ChatMessage> mDatas;
    private EditText mInputMsg;
    private Button mSendMag;
    private Handler mHandler=new Handler(){
        public void HandleMessage(android.os.Message msg){
         //等待接收，子线程完成数据的返回
            ChatMessage fromMessage=(ChatMessage) msg.obj;
            mDatas.add(fromMessage);
            mAdapter.notifyDataSetChanged();
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        initDatas();
        //初始化事件
        initListener();
    }

    private void initListener() {
        mSendMag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final String toMsg= mInputMsg.getText().toString();
               if(TextUtils.isEmpty(toMsg)){
                   Toast.makeText(MainActivity.this,"发送消息不能为空！",Toast.LENGTH_SHORT).show();
                   return;
               }
               ChatMessage toMessage=new ChatMessage();
               toMessage.setDate(new Date());
               toMessage.setMsg(toMsg);
               toMessage.setType(ChatMessage.Type.OUTCOMING);
               mAdapter.notifyDataSetChanged();
               mInputMsg.setText("");
               new Thread(){
                   public void run(){
                       ChatMessage fromMessage=HttpUtils.sendMessage(toMsg);
                       Message m= Message.obtain();
                       m.obj=fromMessage;
                       mHandler.sendMessage(m);

                   };
               }.start();


            }
        });
    }

    private void initDatas() {
        mDatas = new ArrayList<ChatMessage>();
        mDatas.add(new ChatMessage("你好，小慕为您服务", ChatMessage.Type.INCOMING, new Date()));
        mDatas.add(new ChatMessage("你好", ChatMessage.Type.OUTCOMING, new Date()));
        mAdapter = new ChatMessageAdapter(this, mDatas);
        mMsgs.setAdapter(mAdapter);

    }

    private void initView() {
        mMsgs = (ListView) findViewById(R.id.id_listview_msgs);
        mInputMsg = (EditText) findViewById(R.id.id_input_msg);
        mSendMag = (Button) findViewById(R.id.id_send_msg);

    }


}
