package com.st.cms.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.st.cms.application.PocApplication;
import com.st.cms.entity.Detail;
import com.st.cms.entity.Wperson;
import com.st.cms.fragment.TreatmentMainFragment;
import com.st.cms.fragment.ZoneSecondFragment;
import com.st.cms.utils.Constants;
import com.st.cms.utils.DensityUtil;
import com.st.cms.utils.IntentGoTo;
import com.st.cms.utils.NfcUtils;
import com.st.cms.utils.OkHttpClientHelper;
import com.st.cms.utils.PropertiesUtils;
import com.st.cms.utils.SystemBarTintManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import cms.st.com.poccms.R;

public class BaseMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
	public boolean connected = false;
	public Detail detail = null;
	public Gson gson;
	public boolean isAvailable = false;
	//当前的activity标记
	public static int index = 0;
	//toolbar公用部分
	public Toolbar toolbar;
	public FloatingActionButton fab;
	public FloatingActionButton arrow_up;
	public DrawerLayout drawer;
	public ActionBarDrawerToggle toggle;
	public NavigationView navigationView;
	//toolbar content部分与上面的不同，是返回类型的
	public Toolbar toolbar_content;
	public ImageView iv_back;
	public TextView tv_title;
	//中间内容部分
	public View prioritise_main;
	//layout_container_main
	public FrameLayout frameLayout;

	//场景3 treatment的底部菜单栏
	//treat_tab_marker
	public RadioButton radioButton1;
	//treat_tab_treatment
	public RadioButton radioButton2;
	//treat_tab_signs
	public RadioButton radioButton3;
	//treat_tab_gcs
	public RadioButton radioButton4;
	//rg_tab_bar
	public RadioGroup radioGroup;
	//radio button的数组
	public RadioButton[] arrRadioButton;
	//fragment管理器
	FragmentManager manager = null;
	FragmentTransaction transaction = null;

	//碎片的集合，作为数据源
	public List<Fragment> list_Fragment = new ArrayList<>();
	//当前被选中的碎片在数据源中的索引下标
	public int currentIndex = -1;
	//当前radiobutton的下标
	public int checkedIndex = 0;
	//版本信息
	private Properties properties;

	public ProgressDialog progressDialog;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//沉浸式状态栏4.4及以上版本有效
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		SystemBarTintManager tintManager=new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		//使StatusBarTintView 和 actionbar的颜色保持一致，风格统一。
		tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
		// 设置状态栏的文字颜色
		tintManager.setStatusBarDarkMode(true, this);
		manager = getSupportFragmentManager();
		properties = PropertiesUtils.loadConfig(getApplicationContext());
		if(properties != null)
		{
			Log.e("TEST", properties.getProperty("poc.version"));
		}
		gson = new Gson();

		if(progressDialog == null){
			progressDialog = new ProgressDialog(BaseMainActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Loading......");
		}

//		Callback callback = new Callback() {
//			@Override
//			public void onFailure(Request request, IOException e) {
//				e.printStackTrace();
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						Toast.makeText(getApplicationContext(), "Request Failure!Please check the network.", Toast.LENGTH_SHORT).show();
//					}
//				});
//			}
//
//			@Override
//			public void onResponse(Response response) throws IOException {
//				if (response.isSuccessful()) {
//					ResponseBody body = response.body();
//					if (body != null) {
//						String result = body.string();
//						Type type = new TypeToken<ArrayList<Wperson>>() {}.getType();
//						List<Wperson> persons = gson.fromJson(result, type);
//						PocApplication.getPocApplication().setPersons(persons);
//						if(persons != null)
//						{
//							Log.e("TEST", result);
//							Log.e("TEST", persons.size() + "");
//						}
//					}
//
//				}
//				else
//				{
//					Log.e("TEST", response.isSuccessful() + "---" + response.body());
//				}
//
//			}
//		};
//		if(PocApplication.getPocApplication().getPersons() == null)
//		{
//			OkHttpClientHelper.loadNetworkData(getApplicationContext(), Constants.getUrl(Constants.GET_ALL_TAG), callback);
//		}
		loadVictims();
		isConnected();
		Log.e("TEST","create this --:  " + this.getClass() + "----" + this.getIntent().getAction());
	}
	@Override
	protected void onStart() {
		super.onStart();
	}
	@Override
	protected void onResume() {
		Log.e("TEST", "bind nfc" + this.getClass());
		isAvailable = NfcUtils.bindAdapter(getApplicationContext(), this.getClass(), this);
		checkConnection();
		super.onResume();
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();


	}
	public void initView() {
		fab = (FloatingActionButton) findViewById(R.id.fab);
		arrow_up = (FloatingActionButton) findViewById(R.id.arrow_up);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});


	}
	public void initToolBar(String model)
	{
		if(toolbar == null || toolbar_content == null)
		{
			toolbar = (Toolbar) findViewById(R.id.toolbar);
			toolbar_content = (Toolbar) findViewById(R.id.toolbar_content);
		}
		switch(model)
		{
			case "main":
				toolbar.setVisibility(View.VISIBLE);
				toolbar_content.setVisibility(View.GONE);
				setSupportActionBar(toolbar);
				drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
				drawer.setDrawerListener(toggle);
				toggle = new ActionBarDrawerToggle(
						this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
				toggle.syncState();
				navigationView = (NavigationView) findViewById(R.id.nav_view);
				navigationView.setNavigationItemSelectedListener(this);
				if(properties != null)
				{
					navigationView.getMenu().findItem(R.id.menu_about).setTitle("Version:" + properties.getProperty("poc.version"));
				}
				break;
			case "content":
				toolbar_content.setVisibility(View.VISIBLE);
				toolbar.setVisibility(View.GONE);
				setSupportActionBar(toolbar_content);
				iv_back = (ImageView) findViewById(R.id.iv_back);
				tv_title = (TextView)findViewById(R.id.tv_title);
				break;
			default:
				break;
		}
	}

	/**
	 * 这个是导航栏的选择事件
	 * @param item
	 * @return
     */
	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		Intent intent = null;
		switch (item.getItemId())
		{
			case R.id.menu_prioritese:
				if(index == 0)
				{
					return true;
				}
				intent = new Intent(this, MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case R.id.menu_identify:
				if(index == 1)
				{
					return true;
				}
				IntentGoTo.intentActivity(this,IdentyfyActivity.class);
				finish();
				break;
			case R.id.menu_treatment:
				if(index == 2)
				{
					return true;
				}
				intent = new Intent(this, TreatmentMainActivity.class);
				startActivity(intent);
				finish();
				break;
			case R.id.menu_convey:
				if(index == 3)
				{
					return true;
				}
				intent = new Intent(this, ConveyListActivity.class);
				startActivity(intent);
				finish();
				break;
			default:
				break;
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.e("TEST","new intent this --:  " + this.getClass() + "intent --:  " + intent.getAction());
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.e("TEST", "unbind---:" + this.getClass());
		Log.e("TEST", "is unbind success:" + NfcUtils.unbindAdapter(this));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	public void replaceFragment(int i) {
		transaction = manager.beginTransaction();
		transaction.replace(R.id.layout_container_main, list_Fragment.get(i));
		transaction.commit();
	}

	public void switchFragment(int i) {
		//往页面里添加碎片，需要由碎片管理器启动事务让碎片替换占位
		transaction = manager.beginTransaction();
		if (list_Fragment.get(i).isAdded()) {
			transaction.hide(list_Fragment.get(currentIndex)).show(list_Fragment.get(i));
		} else {
			transaction.hide(list_Fragment.get(currentIndex)).add(R.id.layout_container_main,
					list_Fragment.get(i));
		}

		transaction.commit();
		currentIndex = i;
	}

	public void changeFragment(int pre, int current)
	{
		//往页面里添加碎片，需要由碎片管理器启动事务让碎片替换占位
		transaction = manager.beginTransaction();
		if (list_Fragment.get(pre).isAdded() && list_Fragment.get(current).isAdded()) {
			transaction.hide(list_Fragment.get(current)).show(list_Fragment.get(pre));
		}
		transaction.commit();
		currentIndex = pre;
	}
//	public void switchZoneFragment(int i) {
//		//往页面里添加碎片，需要由碎片管理器启动事务让碎片替换占位
//		transaction = manager.beginTransaction();
//		if (list_Fragment.get(i).isAdded()) {
//			transaction.hide(list_Fragment.get(currentIndex)).show(list_Fragment.get(i));
//		} else {
//			transaction.hide(list_Fragment.get(currentIndex)).add(R.id.fl_container_zone,
//					list_Fragment.get(i));
//		}
//		transaction.commit();
//		currentIndex = i;
//	}

	protected  void initZoneFragment(){
		list_Fragment.clear();
		//初始化五个大碎片，并添加进list集合
		Bundle bundle = null;

		bundle = new Bundle();
		bundle.putInt("index",0);
		ZoneSecondFragment fragment1 = new ZoneSecondFragment();
		fragment1.setArguments(bundle);

		bundle = new Bundle();
		bundle.putInt("index",1);
		ZoneSecondFragment fragment2 =  new ZoneSecondFragment();
		fragment2.setArguments(bundle);

		bundle = new Bundle();
		bundle.putInt("index",2);
		ZoneSecondFragment fragment3 = new ZoneSecondFragment();
		fragment3.setArguments(bundle);

		bundle = new Bundle();
		bundle.putInt("index",3);
		ZoneSecondFragment fragment4 =   new ZoneSecondFragment();
		fragment4.setArguments(bundle);

		list_Fragment.add(fragment1);
		list_Fragment.add(fragment2);
		list_Fragment.add(fragment3);
		list_Fragment.add(fragment4);

	}




	protected void initFragment() {
		list_Fragment.clear();
		//初始化五个大碎片，并添加进list集合
		Bundle bundle = null;

		bundle = new Bundle();
		bundle.putString("type", "mark");
		TreatmentMainFragment fragment1 = new TreatmentMainFragment();
		fragment1.setArguments(bundle);

		bundle = new Bundle();
		bundle.putString("type", "treat");
		TreatmentMainFragment fragment2 = new TreatmentMainFragment();
		fragment2.setArguments(bundle);

		bundle = new Bundle();
		bundle.putString("type", "sign");
		TreatmentMainFragment fragment3 = new TreatmentMainFragment();
		fragment3.setArguments(bundle);

		bundle = new Bundle();
		bundle.putString("type", "gcs");
		TreatmentMainFragment fragment4 = new TreatmentMainFragment();
		fragment4.setArguments(bundle);

		list_Fragment.add(fragment1);
		list_Fragment.add(fragment2);
		list_Fragment.add(fragment3);
		list_Fragment.add(fragment4);
		currentIndex = 0;
	}
	//由于第一和第二、第三模块的界面不相同（底部菜单栏导致的，所以从新设置margin）
	public void setFabMargin(int zone)
	{
		CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)fab.getLayoutParams();
		if(zone != 1)
		{
			layoutParams.setMargins(0, 0, DensityUtil.dip2px(getApplicationContext(), 16), DensityUtil.dip2px(getApplicationContext(), 72));
			fab.setLayoutParams(layoutParams);
		}
		else
		{
			layoutParams.setMargins(0, 0, DensityUtil.dip2px(getApplicationContext(), 16), DensityUtil.dip2px(getApplicationContext(), 16));
			fab.setLayoutParams(layoutParams);
		}
	}
	//判断是否是一个json字符串
	public boolean isGoodJson(String json) {

		try {
			new JsonParser().parse(json);
			return true;
		} catch (JsonParseException e) {
			System.out.println("bad json: " + json);
			return false;
		}
	}
	//读取nfc tag并转成detail对象
	public void readTag(Intent intent, int num)
	{
		detail = null;
		String content = NfcUtils.readFromTag(intent);
		if(content == null)
		{
//			resetNFCData();
//			content = NfcUtils.readFromTag(intent);
//			content = resetNFCData();
			Toast.makeText(getApplicationContext(), "Have not got data, please try again.", Toast.LENGTH_SHORT).show();
		}
//		Toast.makeText(this, "---" + content, Toast.LENGTH_SHORT).show();
		if(content != null)
		{
			if(isGoodJson(content))
			{
				try {
					detail = gson.fromJson(content, Detail.class);
					if(detail == null)
					{
						detail = new Detail();
						detail.setTagId(NfcUtils.getTagId(getIntent()));
						detail.setZone("Triage");
					}
					if(detail != null)
					{
						if(detail.getZone() == null || detail.getZone().length() == 0)
						{
							detail.setZone("Triage");
						}
					}
					//一般的读取用1，需要给提示的用0
					if(num == 0)
					{
						Toast.makeText(getApplicationContext(), "Read successful!", Toast.LENGTH_SHORT).show();
					}
				} catch (JsonSyntaxException e) {
					resetNFCData();
					Log.e("TEST", e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	private String resetNFCData()
	{
		detail = new Detail();
		detail.setTagId(NfcUtils.getTagId(getIntent()));
		detail.setZone("Triage");
		detail.setPlv(-1);
		Log.e("TEST", "save:" + detail.toString());
		String content = gson.toJson(detail);
		NfcUtils.writeToTag(this.getIntent(), content);
		return content;
	}
	public void loadVictims()
	{
		Callback callback = new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "Request Failure!Please check the network.", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onResponse(Response response) throws IOException {
				if (response.isSuccessful()) {
					ResponseBody body = response.body();
					if (body != null) {
						String result = body.string();
						Type type = new TypeToken<ArrayList<Wperson>>() {}.getType();
						List<Wperson> persons = gson.fromJson(result, type);
						PocApplication.getPocApplication().setPersons(persons);
						if(persons != null)
						{
							Log.e("TEST", result);
							Log.e("TEST", persons.size() + "");
						}
					}

				}
				else
				{
					Log.e("TEST", response.isSuccessful() + "---" + response.body());
				}

			}
		};
		if(PocApplication.getPocApplication().getPersons() == null)
		{
			OkHttpClientHelper.loadNetworkData(getApplicationContext(), Constants.getUrl(Constants.GET_ALL_TAG), callback);
		}
	}
	public void checkConnection()
	{
		if (isConnect(this)==false)
		{
			new AlertDialog.Builder(this)
					.setTitle("网络错误")
					.setMessage("网络连接失败，请确认网络连接")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							Intent intent=new Intent();
							intent.setClassName("com.android.settings", "com.android.settings.Settings");
							startActivity(intent);
//							android.os.Process.killProcess(android.os.Process.myPid());
//							System.exit(0);
						}
					}).show();
		}
	}
	public static boolean isConnect(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null&& info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Log.v("error",e.toString());
		}
		return false;
	}

	/**
	 * 测试与服务器连通性
	 */
	public void isConnected()
	{
		Log.e("TEST", "test connect");
		new Thread(new Runnable() {
			@Override
			public void run() {
				int timeOut = 100000;
				try {
					InetAddress inet = InetAddress.getByName(Constants.IP);

					connected = isReachable(inet, Constants.PORT, timeOut);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	public static boolean isReachable(InetAddress netAddr, int port, int timeout) {
		boolean isReachable = false;
		Socket socket = null;
		try {
			socket = new Socket();
			// Creates a socket address from an IP address and a port number
			InetSocketAddress endpointSocketAddr = new InetSocketAddress(netAddr, port);
			socket.connect(endpointSocketAddr, timeout);
			Log.e("TEST", "SUCCESS - remote: " + netAddr.getHostAddress() + " port " + port);
			isReachable = true;
		} catch (IOException e) {
			Log.e("TEST", "FAILRE - remote: " + netAddr.getHostAddress() + " port " + port);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					Log.e("TEST", "Error occurred while closing socket..");
				}
			}
		}
		return isReachable;
	}
	public void saveSuccess()
	{
		Intent intent = new Intent(getApplicationContext(), ConfirmActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("status", 0);
		bundle.putString("confirm", getResources().getString(R.string.ok));
		bundle.putString("info", getResources().getString(R.string.prioritise_completed));
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
