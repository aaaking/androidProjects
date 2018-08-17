package com.example.jeliu.bipawallet.Asset;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.BaseActivity;
import com.example.jeliu.bipawallet.Common.AttentionsManager;
import com.example.jeliu.bipawallet.Common.Constant;
import com.example.jeliu.bipawallet.Fragment.ContactsFragment;
import com.example.jeliu.bipawallet.Model.HZContact;
import com.example.jeliu.bipawallet.Network.HZHttpRequest;
import com.example.jeliu.bipawallet.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by liuming on 13/05/2018.
 */

public class SearchActivity extends AppCompatActivity {
    private ImageView ivBack;
    private EditText etSearch;
    private SearchAdapter adapter;
    private ListView listView;
    private JSONArray tokenList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = findViewById(R.id.listview);

        View v = (View)getLayoutInflater().inflate(R.layout.layout_search_toolbar, null);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(v);

        ivBack = v.findViewById(R.id.imageView_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        etSearch = v.findViewById(R.id.editText_search);
        etSearch.addTextChangedListener(mTextWatcher);

        adapter = new SearchAdapter(this);
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        String content = intent.getStringExtra("content");
        try {
            tokenList = new JSONArray(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ActionBar.LayoutParams params=new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.FILL_VERTICAL|Gravity.FILL_HORIZONTAL );

        getSupportActionBar().setCustomView(v, params);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

    }

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart ;
        private int editEnd ;
        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                      int arg3) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2,
                                  int arg3) {
            //etSearch.setText(s);
            doSearch(s.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

        //  @Override
//        public void afterTextChanged(Editable s) {
//            editStart = etSearch.getSelectionStart();
//            editEnd = etSearch.getSelectionEnd();
//            if (temp.length() > 10) {
//                s.delete(editStart-1, editEnd);
//                int tempSelection = editStart;
//                etSearch.setText(s);
//                doSearch(s.toString());
//                etSearch.setSelection(tempSelection);
//            }
//        }
    };

    private void doSearch(String query) {
        ArrayList<String> searchedData = new ArrayList<>();
        if (tokenList != null) {
            int length = tokenList.length();
            for (int i = 0; i < length; ++ i) {
                try {
                    String token = tokenList.getString(i);
                    if (token.contains(query)) {
                        searchedData.add(token);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        adapter.setContent(searchedData);
    }

    private class SearchAdapter extends BaseAdapter {
        protected Context context;
        protected LayoutInflater inflater;

        private ArrayList<String> searchedData = new ArrayList<>();

        public SearchAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(this.context);
        }

        public void setContent(ArrayList<String> jsonArr) {
            searchedData.clear();
            searchedData.addAll(jsonArr);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return searchedData.size();
        }

        @Override
        public Object getItem(int position) {
            return searchedData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
           UnitListHolder holder = null;
            if (null == view) {
                view = inflater.inflate(R.layout.layout_new_attention_item, null);
                holder = new UnitListHolder();

                holder.tvName = (TextView) view.findViewById(R.id.textView_name);//textView_desc
                holder.tvDes = (TextView) view.findViewById(R.id.textView_desc);
                holder.ivSelect = (ImageView) view.findViewById(R.id.imageView_wallet_pic);
                holder.ivAddAttention = (ImageView) view.findViewById(R.id.imageView_attention);

                view.setTag(holder);
            } else {
                holder = (UnitListHolder) view.getTag();
            }
            final String name = searchedData.get(position);
            holder.tvName.setText(name);
            holder.tvDes.setText(name);
            if (AttentionsManager.getInst().contains(name)) {
                holder.ivAddAttention.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
            } else {
                holder.ivAddAttention.setImageDrawable(getResources().getDrawable(R.drawable.attention));
            }
            holder.ivAddAttention.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AttentionsManager.getInst().contains(name)) {
                        AttentionsManager.getInst().removeAttention(name);
                    } else {
                        AttentionsManager.getInst().addAttention(name);
                    }
                    notifyDataSetChanged();
                }
            });

            return view;
        }


        class UnitListHolder {
            public TextView tvName;
            public TextView tvDes;
            public ImageView ivSelect;
            public ImageView ivAddAttention;
        }
    }

}
