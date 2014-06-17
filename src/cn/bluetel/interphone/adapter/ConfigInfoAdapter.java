package cn.bluetel.interphone.adapter;

import java.util.List;

import cn.bluetel.interphone.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConfigInfoAdapter extends BaseAdapter{
	
	private LayoutInflater mInflater;
	private List<String> mConfigInfo;
	private String[] mKeys;
	
	public ConfigInfoAdapter(Context context, List<String> configs) {
		this.mConfigInfo = configs;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mKeys = new String[] {"本机地址", "对讲方式", "对讲地址", "对讲端口", "编码方式", "回放"};
	}
	
	@Override
	public int getCount() {
		return mConfigInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return mConfigInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder = null;
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_config, null);
			mHolder = new ViewHolder();
			mHolder.tvLeft = (TextView) convertView.findViewById(R.id.tv_left);
			mHolder.tvRight = (TextView) convertView.findViewById(R.id.tv_right);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		mHolder.tvLeft.setText(mKeys[position]);
		mHolder.tvRight.setText(mConfigInfo.get(position));
		return convertView;
	}

	private static class ViewHolder {
		TextView tvLeft;
		TextView tvRight;
	}

}