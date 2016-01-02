package com.dtf.daanx;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by yoyo930021 on 2015/12/5.
 */
public class BaseActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    //讀取system參數
    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e("status", "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e("status", "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    //確定使用ssl加密協定版本
    public void trustTaivs() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = getAssets().open("taivsca.crt");
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                caInput.close();
            }

// Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

// Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);


            SSLContext context = SSLContext.getInstance("TLSV1");
            context.init(null, tmf.getTrustManagers(), null);
//            context.init(null, new X509TrustManager[]{new X509TrustManager() {
//                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                }
//
//                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                }
//
//                public X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[0];
//                }
//            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public void trustDacsc() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

//// Create a KeyStore containing our trusted CAs
//            String keyStoreType = KeyStore.getDefaultType();
//            KeyStore keyStore = KeyStore.getInstance("BKS","BC");
////            KeyStore trustStore = KeyStore.getInstance(KEYGUARD_SERVICE);
//
//// Create a TrustManager that trusts the CAs in our KeyStore
//            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//            tmf.init(keyStore);


            SSLContext context = SSLContext.getDefault();
//            context.getDefaultSSLParameters();
//            context.init(null, tmf.getTrustManagers(), null);
//            context.init(null, new X509TrustManager[]{new X509TrustManager() {
//                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                }
//
//                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                }
//
//                public X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[0];
//                }
//            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }


    //判斷是否有網路連線
    public boolean networkInfo() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        //如果未連線的話，mNetworkInfo會等於null
        if (mNetworkInfo != null) {
            if(!(mNetworkInfo.isConnected()||mNetworkInfo.isAvailable())){
                return false;
            }else {
                return true;
            }
        } else {
            return false;
        }
    }

    //跳到設定頁
    public void networkAlert(){
        //region 跳到設定頁
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setMessage("無法連線到網際網路\n學生資訊將使用離線快取");
        builder.setTitle("無法連線");
        builder.setCancelable(false);
        builder.setPositiveButton("設定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent settintIntent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(settintIntent);
            }
        });
        builder.setNegativeButton("以離線模式使用(只能使用學生查詢部分)", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
        //endregion
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //AES加密，帶入byte[]型態的16位英數組合文字、32位英數組合Key、需加密文字
    private static byte[] EncryptAES(byte[] iv, byte[] key,byte[] text)
    {
        try
        {
            AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
            Cipher mCipher = null;
            mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            mCipher.init(Cipher.ENCRYPT_MODE,mSecretKeySpec,mAlgorithmParameterSpec);

            return mCipher.doFinal(text);
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    //AES解密，帶入byte[]型態的16位英數組合文字、32位英數組合Key、需解密文字
    private static byte[] DecryptAES(byte[] iv,byte[] key,byte[] text)
    {
        try
        {
            AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, "AES");
            Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            mCipher.init(Cipher.DECRYPT_MODE,
                    mSecretKeySpec,
                    mAlgorithmParameterSpec);

            return mCipher.doFinal(text);
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    private static String Md516(String sourceStr) {
        String  result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i; StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if(i<0)
                    i+= 256;
                if(i<16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString().substring(8,24);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return result;
    }

    private static String Md532(String sourceStr) {
        String  result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i; StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if(i<0)
                    i+= 256;
                if(i<16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return result;
    }


    public String pwdSecure(String pwd){
        SharedPreferences preferences=getSharedPreferences("setting",0);
        String IvAES = Md516(preferences.getString("stu_name","")+preferences.getString("class","")+preferences.getString("num",""));
        String KeyAES = Md532(preferences.getString("stu_name","")+preferences.getString("class","")+preferences.getString("num",""));

        String TEXT="";
        try {
            byte[] TextByte = EncryptAES(IvAES.getBytes("UTF-8"), KeyAES.getBytes("UTF-8"), pwd.getBytes("UTF-8"));
            TEXT = Base64.encodeToString(TextByte, Base64.DEFAULT);
        }catch (Exception e){/**/}
        return TEXT;
    }

    public String pwdNoSecure(String pwd){
        SharedPreferences preferences=getSharedPreferences("setting",0);
        String IvAES = Md516(preferences.getString("stu_name","")+preferences.getString("class","")+preferences.getString("num",""));
        String KeyAES = Md532(preferences.getString("stu_name","")+preferences.getString("class","")+preferences.getString("num",""));

        String TEXT="";
        try {
            byte[] TextByte = DecryptAES(IvAES.getBytes("UTF-8"), KeyAES.getBytes("UTF-8"),Base64.decode(pwd.getBytes("UTF-8"), Base64.DEFAULT));
            TEXT = new String(TextByte,"UTF-8");
        }catch (Exception e){/**/}
        return TEXT;
    }


}
