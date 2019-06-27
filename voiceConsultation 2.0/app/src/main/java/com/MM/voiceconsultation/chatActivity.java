package com.MM.voiceconsultation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;
import com.xq.voiceconsultation.adapter.ChatListAdapter;
import com.xq.voiceconsultation.entity.ChatData;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class chatActivity extends AppCompatActivity implements View.OnClickListener{
    private ListView lv_chat_list;
    private EditText ed_send;
    private Button btn_send;
    private List<ChatData> mList = new ArrayList<>();
    private ChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //初始化控件
        initView();

    }

    private void initView() {
        lv_chat_list = (ListView) findViewById(R.id.lv_chat_list);
        ed_send = (EditText) findViewById(R.id.ed_send);
        btn_send = (Button) findViewById(R.id.btn_send);
        lv_chat_list.setDivider(null);

        //设置适配器
        adapter = new ChatListAdapter(this,mList);
        lv_chat_list.setAdapter(adapter);

        //设置发送按钮监听
        btn_send.setOnClickListener(this);

        //设置欢迎语
        //addlefttext("你好呀！");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_send:
                //获取输入框内容
                String message = ed_send.getText().toString().trim();
                //判断是否为空
                if(!TextUtils.isEmpty(message)){
                    //清空当前输入框
                    ed_send.setText("");
                    //添加输入的内容到right item
                    addrighttext(message);
                    //定义URL
                    //图灵机器人接口地址：http://www.tuling123.com/openapi/api
                    //key=后接在图灵官网申请到的apikey
                    //info后接输入的内容
                    if( message.equals("@") ){
                        message = "你好";
                    };

                    String url ="http://openapi.tuling123.com/openapi/api/v2";
                           // "apiKey="+"d6e4651bcc224f088c4f2d441fc3b37a"+"&inputText="+message;

                    //post请求简洁版实现
                    HttpParams params = new HttpParams() ;

                    System.out.println(message);
                    String param ="{\"perception\": {\"inputText\": {\"text\": \""+message+"\"}},"
                            +"\"userInfo\": {\"apiKey\": "
                            +"\"d6e4651bcc224f088c4f2d441fc3b37a\",\"userId\": \"00\"}}";

                    params.putJsonParams(param);
                    //请求RxVolley将信息发出（添加RxVolley依赖，
                    // 在app的build.gradle的ependencies中添加compile 'com.kymjs.rxvolley:rxvolley:1.1.4'）
                    RxVolley.jsonPost(url,params,new HttpCallback() {
                        @Override
                        public void onSuccess(String t) {
                            //解析返回的JSON数据
                            pasingJson(t);
                        }
                    });


                }else{
                    return;
                }
                break;
        }
    }
    //解析返回的JSON数据
    private void pasingJson(String message){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(message);
            //通过key（text）转换成Json获取返回值
            String text = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject
                    ("values").getString("text");
            //添加在左边
            addlefttext(text);
            System.out.println(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //添加右侧消息
    private void addrighttext(String message) {
        ChatData data = new ChatData();
        data.setType(ChatListAdapter.chat_right);
        data.setText(message);
        mList.add(data);
        //通知adapter刷新页面
        adapter.notifyDataSetChanged();
        //滚动到底部
        lv_chat_list.setSelection(lv_chat_list.getBottom());

    }

    //添加左侧消息
    private void addlefttext(String message) {
        ChatData data = new ChatData();
        data.setType(ChatListAdapter.chat_left);
        data.setText(message);
        mList.add(data);
        adapter.notifyDataSetChanged();
        lv_chat_list.setSelection(lv_chat_list.getBottom());

    }
}
