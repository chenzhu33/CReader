package org.carelife.creader.ui.activity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.util.FileUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.carelife.creader.R;

public class Advisor extends Activity {
	private EditText e1, e2;
	private Button commit, cancer;
	private ProgressDialog prgDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.advise);

		e1 = (EditText) findViewById(R.id.editText1);
		e2 = (EditText) findViewById(R.id.editText2);
		commit = (Button) findViewById(R.id.advise_commit);
		cancer = (Button) findViewById(R.id.advise_cancer);
		commit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String msg = e1.getText().toString();
				final String userinfo = e2.getText().toString();
				if (null == msg || "".equals(msg.trim())) {
					Toast.makeText(Advisor.this, "亲，反馈信息不能为空", Toast.LENGTH_SHORT)
							.show();
				} else {
					Pattern pattern = Pattern.compile("[0-9]*");
					if (null == userinfo || "".equals(userinfo.trim())) {
						Toast.makeText(Advisor.this, "亲，联系方式不能为空",
								Toast.LENGTH_SHORT).show();
					} else if(!(pattern.matcher(userinfo).matches() || FileUtil.isEmail(userinfo))){
						Toast.makeText(Advisor.this, "亲，电话号码或邮箱有错!", 500).show();  
					} else {
						if (prgDialog == null)
							prgDialog = new ProgressDialog(Advisor.this);
						final Handler handler = new Handler() {
							public void handleMessage(Message msg) {
								if (!Thread.currentThread().isInterrupted()) {
									switch (msg.what) {
									case 0:
										prgDialog.show();
										break;
									case 1:
										prgDialog.dismiss();
										Toast.makeText(Advisor.this, "感谢您的提交",
												Toast.LENGTH_SHORT).show();
										finish();
										break;
									case 2:
										if (null != prgDialog)
											prgDialog.dismiss();
										Toast.makeText(Advisor.this, "抱歉提交失败",
												Toast.LENGTH_SHORT).show();
										break;
									}
								}
								super.handleMessage(msg);
							}
						};
						prgDialog.setTitle("请稍等");
						prgDialog.setMessage("正在提交反馈");
						prgDialog.setCancelable(true);
						handler.sendEmptyMessage(0);
						new Thread() {
							public void run() {
								if (sendMessage(msg, userinfo))
									handler.sendEmptyMessage(1);
								else
									handler.sendEmptyMessage(2);
							}
						}.start();
					}
				}
			}
		});
		cancer.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Advisor.this.finish();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	public boolean sendMessage(final String msg, final String userinfo) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(UrlHelper.feedback_url);
			// 添加http头信息
			httppost.addHeader("Content-TYPE",
					"application/x-www-form-urlencoded");

			// httppost.addHeader("Authorization", "your token"); //认证token
			// httppost.addHeader("Content-Type", "application/json");
			// httppost.addHeader("User-Agent", "SogouSearch Android");
			// httppost.addHeader()
			// http post的json数据格式： {"name": "your name","parentId":
			// "id_of_parent"}
			// JSONObject obj = new JSONObject();
			// obj.put("feedback_msg", msg);
			// obj.put("feedback_userinfo", userinfo);
			// httppost.setEntity(new StringEntity(obj.toString()));
			// httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			// Log.i("msg",msg);
			// Log.i("user",userinfo);
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair("feedback_msg", msg));
			nameValuePair.add(new BasicNameValuePair("feedback_userinfo",
					userinfo));
			nameValuePair.add(new BasicNameValuePair("model",
					android.os.Build.MODEL));
			String appVer = getPackageManager().getPackageInfo("com.sogou", 0).versionName;
			nameValuePair.add(new BasicNameValuePair("version", ""
					+ android.os.Build.VERSION.SDK_INT + ";"
					+ android.os.Build.VERSION.RELEASE + ";" + appVer));
			InetAddress localMachine = null;
			String ip = "";
			try {
				localMachine = InetAddress.getLocalHost();
				if (null != localMachine)
					ip = localMachine.getHostAddress();
				// 获取ip地址
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception err) {
				err.printStackTrace();
			}
			nameValuePair.add(new BasicNameValuePair("ip", ip));
			String num = "";
			try {
				TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				// 获取设备的电话号码
				num = telManager.getLine1Number();
				if (null == num)
					num = "";
				int phoneTYPE = telManager.getPhoneType();
				if (phoneTYPE == TelephonyManager.PHONE_TYPE_CDMA) {
					if ("".equals(num))
						num += "CDMA";
					else
						num += ";CDMA";
				} else if (phoneTYPE == TelephonyManager.PHONE_TYPE_GSM) {
					if ("".equals(num))
						num += "GSM";
					else
						num += ";GSM";
				} else {
					if ("".equals(num))
						num += "NONE";
					else
						num += ";NONE";
				}
			} catch (Exception err) {
				err.printStackTrace();
			}
			nameValuePair.add(new BasicNameValuePair("phone", num));

			// HttpPost httpPost = new
			// HttpPost("http://192.168.1.103/webservice/index.php");
			/* 设置请求的数据 */
			// Log.i("",new
			// UrlEncodedFormEntity(nameValuePair).getContent().toString());
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePair,
					HTTP.UTF_8));
			// httppost.getParams().setParameter("feedback_msg", msg);
			// httppost.getParams().setParameter("feedback_userinfo", userinfo);
			HttpResponse response;
			response = httpclient.execute(httppost);
			// 检验状态码，如果成功接收数据
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				// String rev =
				// EntityUtils.toString(response.getEntity());//返回json格式： {"id":
				// "27JpL~j4vsL0LX00E00005","version": "abc"}
				// obj = new JSONObject(rev);
				// String id = obj.getString("id");
				// String version = obj.getString("version");
				return true;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		} catch (Exception e2) {
			e2.printStackTrace();
			return false;
		}
		return false;
	}

}