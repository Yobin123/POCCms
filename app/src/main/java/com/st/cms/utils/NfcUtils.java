package com.st.cms.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

@SuppressLint("NewApi")
public class NfcUtils {

    private static NfcAdapter nfcAdapter;
    private static PendingIntent mPendingIntent;
    private static IntentFilter[] filters = null;
    private static String[][] techList = null;
    private static String tagId;
	public static String readFromTag(Intent intent){
		/*if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())){			
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			for (String tech : tagFromIntent.getTechList()) {   
	            System.out.println(tech);   
	        }
		}*/
		//System.out.println(NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()));
	    /*Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);  
	    NdefMessage mNdefMsg = (NdefMessage)rawArray[0];  
	    NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];  
	    try {  
	        if(mNdefRecord != null){  
	            String readResult = new String(mNdefRecord.getPayload(),"UTF-8");
	            
	            return readResult;  
	         }  
	    }  
	    catch (UnsupportedEncodingException e) {  
	         e.printStackTrace();  
	    }*/
        String result = null;
        try {
            result = readNFCTag(intent);
        } catch (Exception e) {
            result = e.getMessage();
            e.printStackTrace();
        }
        return result;
	}
	
	public static boolean writeToTag(Intent intent,String data){
		 if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
			 ||NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
				||NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {  
	        Tag tag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);		        
	        
            NdefRecord ndefRecord=createTextRecord(data);  
            NdefRecord[] records={ndefRecord};  
            NdefMessage ndefMessage=new NdefMessage(records);  
            boolean flag=writeTag(ndefMessage, tag);	            
            return flag;
		 }
		 return false;
	}

	//创建一个封装要写入的文本的NdefRecord对象
    public static NdefRecord createTextRecord(String text) {
        //生成语言编码的字节数组，中文编码
        byte[] langBytes = Locale.CHINA.getLanguage().getBytes(
                Charset.forName("US-ASCII"));
        //将要写入的文本以UTF_8格式进行编码
        Charset utfEncoding = Charset.forName("UTF-8");
        //由于已经确定文本的格式编码为UTF_8，所以直接将payload的第1个字节的第7位设为0
        byte[] textBytes = text.getBytes(utfEncoding);
        int utfBit = 0;
        //定义和初始化状态字节
        char status = (char) (utfBit + langBytes.length);
        //创建存储payload的字节数组
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        //设置状态字节
        data[0] = (byte) status;
        //设置语言编码
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        //设置实际要写入的文本
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
                textBytes.length);
        //根据前面设置的payload创建NdefRecord对象
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return record;
    }
    
    //将NdefMessage对象写入标签，成功写入返回ture，否则返回false
    public static boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        Ndef ndef = null;
        try {
            //获取Ndef对象
            ndef = Ndef.get(tag);
            if (ndef != null) {
                //允许对标签进行IO操作
                Log.e("TEST", "isconnected: ----" + ndef.isConnected());
                if(!ndef.isConnected())
                {
                    ndef.connect();
                }

                if (!ndef.isWritable()) {
                    System.out.println("只读");
                    return false;
 
                }
                if (ndef.getMaxSize() < size) {
                   System.out.println("空间不足");
                    return false;
                }
                //向标签写入数据
                ndef.writeNdefMessage(message);
                System.out.println("写入成功1");
                return true;
 
            } else {
                //获取可以格式化和向标签写入数据NdefFormatable对象
                NdefFormatable format = NdefFormatable.get(tag);
                //向非NDEF格式或未格式化的标签写入NDEF格式数据
                if (format != null) {
                    try {
                        //允许对标签进行IO操作
                        format.connect();
                        format.format(message);
                        System.out.println("写入成功2");
                        return true;
                    } catch (Exception e) {
                        System.out.println("写入NDEF格式数据失败！");
                        return false;
                    }
                } else {
                    System.out.println("NFC标签不支持NDEF格式！");
                    return false;
 
                }
            }
        } catch (Exception e) {
            System.out.println("报错了");
            e.printStackTrace();
            return false;
        }
        finally {
            if(ndef != null)
            {
                try {
                    ndef.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
 
    }
    
    //将纯文本内容从NdefRecord对象（payload）中解析出来
    public static String parse(NdefRecord record) {
        //验证TNF是否为NdefRecord.TNF_WELL_KNOWN
        if (record.getTnf() != NdefRecord.TNF_WELL_KNOWN)
            return null;
        //验证可变长度类型是否为RTD_TEXT
        if (!Arrays.equals(record.getType(), NdefRecord.RTD_TEXT))
            return null;
 
        try {
            //获取payload
            byte[] payload = record.getPayload();
            //下面代码分析payload：状态字节+ISO语言编码（ASCLL）+文本数据（UTF_8/UTF_16）
            //其中payload[0]放置状态字节：如果bit7为0，文本数据以UTF_8格式编码，如果为1则以UTF_16编码
            //bit6是保留位，默认为0
            /*
             * payload[0] contains the "Status Byte Encodings" field, per the
             * NFC Forum "Text Record Type Definition" section 3.2.1.
             * 
             * bit7 is the Text Encoding Field.
             * 
             * if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1):
             * The text is encoded in UTF16
             * 
             * Bit_6 is reserved for future use and must be set to zero.
             * 
             * Bits 5 to 0 are the length of the IANA language code.
             */
            String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8"
                    : "UTF-16";
            //处理bit5-0。bit5-0表示语言编码长度（字节数）
            int languageCodeLength = payload[0] & 0x3f;
            //获取语言编码（从payload的第2个字节读取languageCodeLength个字节作为语言编码）
            String languageCode = new String(payload, 1, languageCodeLength,
                    "US-ASCII");
            //解析出实际的文本数据
            String text = new String(payload, languageCodeLength + 1,
                    payload.length - languageCodeLength - 1, textEncoding);
            //创建一个TextRecord对象，并返回该对象
            return text;
        } catch (UnsupportedEncodingException e) {
            // should never happen unless we get a malformed tag.
            throw new IllegalArgumentException(e);
        }
    }
    
    public static String readNFCTag(Intent intent) {
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
        {
            // 从标签读取数据（Parcelable对象）
            Parcelable[] rawMsgs = intent
                    .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msgs[] = null;
            int contentSize = 0;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                System.out.println("NdefMessage 大小：" + rawMsgs.length);
                // 标签可能存储了多个NdefMessage对象，一般情况下只有一个NdefMessage对象
                for (int i = 0; i < rawMsgs.length; i++) {
                    // 转换成NdefMessage对象
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    // 计算数据的总长度
                    contentSize += msgs[i].toByteArray().length;

                }
            }
            try {
                if (msgs != null) {
                    // 程序中只考虑了1个NdefRecord对象，若是通用软件应该考虑所有的NdefRecord对象
                    NdefRecord record = msgs[0].getRecords()[0];
                    System.out.println("NdefRecord 大小：" + msgs[0].getRecords().length);
                    // 分析第1个NdefRecorder，并创建TextRecord对象
                    String text = parse(msgs[0].getRecords()[0]);
                    Log.e("TEST", text);
                    return text;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		return null;
        
    }
    
    public static String getTagId(Intent intent){
    	//获取tag id
    	byte[] ids=intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
    
    	String uid=bin2hex(ids);    	
    	return uid;
    }
    
    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1,data));
    }
    public static boolean bindAdapter(Context context, Class clazz, Activity activity)
    {
        String result  = null;
        if(!checkNfcAvailable(context))
        {
            return false;
        }
        if(filters == null || techList == null)
        {
            initParams();
        }
        if(mPendingIntent == null)
        {
            initPendingIntent(context, clazz);
        }
        nfcAdapter.enableForegroundDispatch(activity, mPendingIntent, filters, techList);
        return true;
    }
    public static void initPendingIntent(Context context, Class clazz)
    {
        //设置 activity 的启动方式为 singleTop，
        mPendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, clazz),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public static void initParams()
    {
        IntentFilter filter=new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter filter1=new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter filter2=new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        //指定过滤数据类型
        try {
            filter.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        filters = new IntentFilter[]{filter,filter1,filter2};
        techList = new String[][] {
                new String[] { MifareClassic.class.getName() },
                new String[] { NfcA.class.getName() },
                new String[] { NdefFormatable.class.getName() },
                new String[] { Ndef.class.getName() }};
    }
    private static boolean checkNfcAvailable(Context context)
    {
        if(nfcAdapter == null)
        {
            nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        }
        if(nfcAdapter == null)
        {
            return false;
        }
        return true;
    }
    public static boolean unbindAdapter(Activity activity)
    {
        if (nfcAdapter != null && mPendingIntent != null && filters != null & techList != null)
        {
            mPendingIntent = null;
            nfcAdapter.disableForegroundDispatch(activity);
            return true;
        }
        return false;
    }
    public static boolean isNfcIntent(Activity activity)
    {
        if(!(NfcAdapter.ACTION_TECH_DISCOVERED.equals(activity.getIntent().getAction())
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(activity.getIntent().getAction())
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(activity.getIntent().getAction())))
        {
            return false;
        }
        return true;
    }
//    public static String testNfc(Intent intent)
//    {
//        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
//                ||NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
//                ||NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
//            //获取nfc卡片id
//            tagId = NfcUtils.getTagId(intent);
//            Log.e("TEST", tagId);
//            boolean flag = NfcUtils.writeToTag(intent, "level=0000;hospital=0000");
//            if(flag)
//            {
//                Log.e("TEST", "插入成功！");
//            }
//        }
//        return tagId;
//    }
}