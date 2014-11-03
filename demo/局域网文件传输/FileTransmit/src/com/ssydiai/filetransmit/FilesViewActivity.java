package com.ssydiai.filetransmit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FilesViewActivity extends Activity {
	SimpleAdapter adapter;
	Button btnSend;
	ListView fileView;
	ArrayList<String> fileName, safeFileName;
	ArrayList<HashMap<String, Object>> listItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_view);
		
		Toast.makeText(getApplicationContext(), "选择要发送的文件", Toast.LENGTH_SHORT).show();
		fileName = new ArrayList<String>();
		safeFileName = new ArrayList<String>();
		fileView = (ListView)findViewById(R.id.fileView);
		listItem = new ArrayList<HashMap<String, Object>>();
		//默认打开目录
		this.FilesListView(Environment.getExternalStorageDirectory().getPath());
		adapter = new SimpleAdapter(
				getApplicationContext(),
				listItem,
				R.layout.item_view,
				new String[] {"image", "name", "path", "type", "parent", "select"},
				new int[]{R.id.image, R.id.file_name, R.id.file_path, R.id.file_type, R.id.file_parent, R.id.select}
		);
		fileView.setAdapter(adapter);
		fileView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView isDirectory = (TextView)view.findViewById(R.id.file_type); 
				TextView path = (TextView)view.findViewById(R.id.file_path);
				TextView name = (TextView)view.findViewById(R.id.file_name);
				
				if (Boolean.parseBoolean(isDirectory.getText().toString())){
					//进入目录
					FilesViewActivity.this.FilesListView(path.getText().toString());
					adapter.notifyDataSetChanged();
					fileName.clear();
					safeFileName.clear();
					SetSendButtonEnabled();
				}else{
					//选择文件
					ImageView select = (ImageView)view.findViewById(R.id.select);
					if(Integer.parseInt(select.getTag().toString()) == 1){
						select.setImageResource(R.drawable.unselected);
						select.setTag(0);
						fileName.remove(name.getText().toString());
						safeFileName.remove(path.getText().toString());
					}else{
						select.setImageResource(R.drawable.selected);
						select.setTag(1);
						fileName.add(name.getText().toString());
						safeFileName.add(path.getText().toString());
					}
					SetSendButtonEnabled();
				}
			}
		});
		btnSend = (Button)findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putStringArrayListExtra("fileName", fileName);
				intent.putStringArrayListExtra("safeFileName", safeFileName);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	void SetSendButtonEnabled(){
		btnSend.setEnabled(false);
		if (fileName.size() > 0){
			btnSend.setEnabled(true);
		}
	}
	private void FilesListView(String selectedPath){
		File selectedFile = new File(selectedPath);
		if (selectedFile.canRead()){
			File[] file = selectedFile.listFiles();
			listItem.clear();
			for (int i = 0; i < file.length; i++){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("image", file[i].isDirectory()?R.drawable.folder:R.drawable.file);
				map.put("name", file[i].getName());
				map.put("path", file[i].getPath());
				map.put("type", file[i].isDirectory());
				map.put("parent", file[i].getParent());
				map.put("select", file[i].isFile()?R.drawable.unselected:"");
				listItem.add(map);
			}
			//判断有无父目录，增加返回上一级目录菜单
			if (selectedFile.getParent() != null){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("name", "返回上一级目录");
				map.put("path", selectedFile.getParent());
				map.put("type", true);
				map.put("parent", selectedFile.getParent());
				listItem.add(0, map);
			}
		}else{
			Toast.makeText(getApplicationContext(), "该目录不能读取", Toast.LENGTH_SHORT).show();
		}
	}

}
