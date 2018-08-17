package com.example.jeliu.bipawallet.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jeliu.bipawallet.Base.BaseFragment;
import com.example.jeliu.bipawallet.Contact.AddContactActivity;
import com.example.jeliu.bipawallet.Model.HZContact;
import com.example.jeliu.bipawallet.R;
import com.example.jeliu.bipawallet.UserInfo.UserInfoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuming on 05/05/2018.
 */

public class ContactsFragment extends BaseFragment {
    @BindView(R.id.listview)
    ListView listView;
    private ContactsAdapter adapter;

    @OnClick(R.id.imageView_add) void onAdd() {
        Intent intent = new Intent(getActivity(), AddContactActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, null);
        ButterKnife.bind(this, view);

        adapter = new ContactsAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0, 0, 0, getString(R.string.delete));
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                intent.putExtra("check", true);
                intent.putExtra("index", i);
                startActivityForResult(intent, 1);
            }
        });
        ArrayList<HZContact> contacts = UserInfoManager.getInst().getContacts();
        adapter.setContent(contacts);
        return view;
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch(item.getItemId()){
            case 0:
                //Toast.makeText(ManagerListActivity.this, id, Toast.LENGTH_SHORT).show();
            /*移除list的某项数据，注意remove()里的数据只能是int，这里用了强制转换，将long转换成int*/
                //list.remove((int)info.id);
                //更新listview的数据
                //myadapter.notifyDataSetChanged();
                ArrayList<HZContact> contacts = UserInfoManager.getInst().getContacts();
                HZContact contact = contacts.get((int) info.id);
                UserInfoManager.getInst().removeContact(contact);

                contacts = UserInfoManager.getInst().getContacts();
                adapter.setContent(contacts);

                return true;
            case 1:
                //Toast.makeText(ManagerListActivity.this, "11", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            ArrayList<HZContact> contacts = UserInfoManager.getInst().getContacts();
            adapter.setContent(contacts);
        }
    }

    private class ContactsAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<HZContact> contacts;
        private LayoutInflater inflater;

        public ContactsAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(this.context);
        }

        public void setContent(ArrayList<HZContact> contacts) {
            this.contacts = contacts;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (contacts == null) {
                return 0;
            }
            return contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return contacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            UnitListHolder holder = null;
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.layout_contact_item, null);
                holder = new UnitListHolder();
                holder.iconImageview = convertView.findViewById(R.id.imageView_photo);
                holder.tvName = convertView.findViewById(R.id.textView_name);
                holder.tvDate = convertView.findViewById(R.id.textView_des);

                convertView.setTag(holder);
            } else {
                holder = (UnitListHolder) convertView.getTag();
            }
            HZContact contact = contacts.get(position);
            holder.tvName.setText(contact.lastname+" "+contact.firstname);
            holder.tvDate.setText(contact.address);

//            try {
//                //final JSONObject jsonObj = jsonArr.getJSONObject(position);
//
//            }
//            catch (JSONException ex) {
//                return null;
//            }

            return convertView;
        }


        class UnitListHolder {
            public TextView tvName;
            public TextView tvDate;
            public ImageView iconImageview;
        }
    }
}
