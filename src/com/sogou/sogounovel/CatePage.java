package com.sogou.sogounovel;

import com.sogou.R;
import com.sogou.adapter.CateRankAdapter;
import com.sogou.constdata.ConstData;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class CatePage extends Activity {

	ListView rank_list;

	CateRankAdapter cradapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.catepage);
		rank_list = (ListView) findViewById(R.id.rankcate_list);

		cradapter = new CateRankAdapter(this, ConstData.book_cate,
				ConstData.goto_data);
		rank_list.setAdapter(cradapter);

	}

}
