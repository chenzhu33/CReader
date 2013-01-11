package org.carelife.creader.ui.activity;

import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.ui.adapter.CateRankAdapter;

import org.carelife.creader.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class RankPage extends Activity {

	ListView rank_list;

	CateRankAdapter cradapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.rankpage);

		rank_list = (ListView) findViewById(R.id.rankcate_list);

		cradapter = new CateRankAdapter(this, UrlHelper.book_rank, UrlHelper.goto_data_rank);
		rank_list.setAdapter(cradapter);
	}

}
