package com.jiandan.leanernfcdemo;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jiandan.leanernfcdemo.entity.TagInfo;
import com.jiandan.leanernfcdemo.mesDao.NfcTagDao;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    NfcAdapter mNfcAdapter;
    EditText mNote;
    EditText mNoteRead;
    PendingIntent mNfcPendingIntent;
    IntentFilter[] mNdefExchangeFilters;
    Button redTaginfo,wirteTagInfo;


    //    ACTION_NDEF_DISCOVERED       uid:044aecaaa24881=
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNoteRead= (EditText) findViewById(R.id.mNoteRead);
        redTaginfo= (Button) findViewById(R.id.read_tag);
        wirteTagInfo= (Button) findViewById(R.id.write_tag);
        /**
         * 判断是否存在NFC功能
         */
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);  //获取安卓适配器
        if (mNfcAdapter == null) {
            Toast.makeText(MainActivity.this, "设备不支持NFC！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(MainActivity.this, "请在系统设置中先启用NFC功能！", Toast.LENGTH_SHORT).show();
            return;
        }
        // Handle all of our received NFC intents in this activity.
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // Intent filters for reading a note from a tag or exchanging over p2p.
        IntentFilter ndefDetected = new IntentFilter(
                NfcAdapter.ACTION_NDEF_DISCOVERED);//ACTION_NDEF_DISCOVERED
        try {
            ndefDetected.addDataType("text/plain");
        } catch (Exception e) {
        }

        mNdefExchangeFilters = new IntentFilter[]{ndefDetected};


        wirteTagInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nd.save(ti);
            }
        });

        redTaginfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DbInfoActivity.class);
                intent.putExtra("username",getUser());
                startActivity(intent);
            }
        });
    }


    public String getUser(){

        return "admin";
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundNdefPush(this);
        }
    }
    TagInfo ti;
    NfcTagDao nd=new NfcTagDao(this);
    @Override
    protected void onResume() {
        byte[] myNFCID = getIntent().getByteArrayExtra(NfcAdapter.EXTRA_ID);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = getNdefMessages(getIntent());
            byte[] payload = messages[0].getRecords()[0].getPayload();
            byte[] payload2 = messages[0].getRecords()[1].getPayload();
            String info="当前登录："+getUser()+"\n"+"标签编号："+ Converter.getHexString(myNFCID, myNFCID.length)+"\n"+"秘钥是："+new String(payload)+"\n"+"楼层信息是："+new String(payload2);

            mNoteRead.setText(info);
            ti = new TagInfo();
            ti.setInfo(new String(payload2).substring(3));
            ti.setKey(new String(payload).substring(3));
            ti.setTagid(Converter.getHexString(myNFCID, myNFCID.length));
            ti.setUsername(getUser());
            ti.setCurtime(new Date().getTime());
//            nd.save(ti);

            dialog();
            setIntent(new Intent());                             // 消除意图
        }
        if (mNfcAdapter != null) {
            enableNdefExchangeMode();
        }
        super.onResume();
    }


    protected void dialog() {
       AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
         builder.setMessage("确认签到吗？"+"\n"+"楼层是："+ti.getInfo()+"\n"+"时间是："+ti.getCurTimeForDate()+"\n"+"签到人："+ti.getUsername());
        builder.setTitle("提示");
       builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
              @Override
             public void onClick(DialogInterface dialog, int which) {
                  nd.save(ti);
                   dialog.dismiss();
              }
             });
         builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
                  }
              });
        builder.create().show();
    }





    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        // NDEF exchange mode
        byte[] myNFCID = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        super.onNewIntent(intent);
    }



    private NdefMessage getNoteAsNdef() {
        byte[] textBytes = mNote.getText().toString().getBytes();//TNF_MIME_MEDIA
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(),
                new byte[] {}, textBytes);
        return new NdefMessage(new NdefRecord[] {
                textRecord
        });
    }

    private void enableNdefExchangeMode() {
        mNfcAdapter.enableForegroundNdefPush(MainActivity.this, getNoteAsNdef());
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
    }

    //读取nfc数据
    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent

        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                        record
                });
                msgs = new NdefMessage[] {
                        msg
                };
            }
        } else {
            Log.d("this", "Unknown intent.");
            finish();
        }
        return msgs;
    }





}
