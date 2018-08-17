package com.example.jeliu.bipawallet.Common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.jeliu.bipawallet.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jeliu on 6/27/17.
 */

public class ListWithSectionAdapter  extends BaseAdapter  {
    public final Map<String,Adapter> sections = new LinkedHashMap<String,Adapter>();
    public final ArrayAdapter<String> headers;
    public final static int TYPE_SECTION_HEADER = 0;
    private boolean mIsGroup;

    public ListWithSectionAdapter(Context context, boolean isGroup, int resource) {
        if (resource == 0) {
            headers = new ArrayAdapter<String>(context, R.layout.layout_record_header_item, R.id.textView_name);
        } else {
            headers = new ArrayAdapter<String>(context, resource);
        }
        mIsGroup = isGroup;
    }

    public void addSection(String section, Adapter adapter) {
        this.headers.add(section);
        this.sections.put(section, adapter);
    }

    public void clearSection() {
        headers.clear();
        sections.clear();
    }

    public Object getItem(int position) {
        if (!mIsGroup) {
            for(Object section : this.sections.keySet()) {
                Adapter adapter = sections.get(section);
                return adapter.getItem(position);
            }
        } else {
            for(Object section : this.sections.keySet()) {
                Adapter adapter = sections.get(section);
                int size = adapter.getCount() + 1;

                // check if position inside this section
                if(position == 0) return section;
                if(position < size) return adapter.getItem(position - 1);

                // otherwise jump into next section
                position -= size;
            }
        }

        return null;
    }

    public int getCount() {
        // total together all sections, plus one for each section header
        if (!mIsGroup) {
            int total = 0;
            for(Adapter adapter : this.sections.values())
                total += adapter.getCount();
            return total;
        } else {
            int total = 0;
            for(Adapter adapter : this.sections.values())
                total += adapter.getCount() + 1;
            return total;
        }
    }

    public int getCountExceptSection() {
        int total = 0;
        for(Adapter adapter : this.sections.values())
            total += adapter.getCount();
        return total;
    }

    public int getViewTypeCount() {
        // assume that headers count as one, then total all sections
        int total = 1;
        for(Adapter adapter : this.sections.values())
            total += adapter.getViewTypeCount();
        return total;
    }

    public int getItemViewType(int position) {
        if (!mIsGroup) {
            int type = 1;
            for(Object section : this.sections.keySet()) {
                Adapter adapter = sections.get(section);
                int size = adapter.getCount();

                // check if position inside this section
                if(position < size) return type + adapter.getItemViewType(position);

                // otherwise jump into next section
                position -= size;
                type += adapter.getViewTypeCount();
            }
            return -1;
        } else {
            int type = 1;
            for(Object section : this.sections.keySet()) {
                Adapter adapter = sections.get(section);
                int size = adapter.getCount() + 1;

                // check if position inside this section
                if(position == 0) return TYPE_SECTION_HEADER;
                if(position < size) return type + adapter.getItemViewType(position - 1);

                // otherwise jump into next section
                position -= size;
                type += adapter.getViewTypeCount();
            }
            return -1;
        }
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    public boolean isEnabled(int position) {
        return (getItemViewType(position) != TYPE_SECTION_HEADER);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (!mIsGroup) {
            int sectionnum = 0;
            for(Object section : this.sections.keySet()) {
                Adapter adapter = sections.get(section);
                int size = adapter.getCount();

                // check if position inside this section
                if(position < size) return adapter.getView(position, convertView, parent);

                // otherwise jump into next section
                position -= size;
                sectionnum++;
            }
        } else {
            int sectionnum = 0;
            for(Object section : this.sections.keySet()) {
                Adapter adapter = sections.get(section);
                int size = adapter.getCount() + 1;

                // check if position inside this section
                if(position == 0) {
                    LinearLayout ll = (LinearLayout)headers.getView(sectionnum, null, parent);
                    TextView textView = ll.findViewById(R.id.textView_name);
                    return textView;
                }
                if(position < size) return adapter.getView(position - 1, convertView, parent);

                // otherwise jump into next section
                position -= size;
                sectionnum++;
            }
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Collection<Adapter> allAdapters() {
        return (Collection<Adapter>) sections.values();
    }

    public int indexExceptionSection(int position) {
        if (mIsGroup) {
            int result = position;
            for(Object section : this.sections.keySet()) {
                Adapter adapter = sections.get(section);
                int size = adapter.getCount() + 1;

                // check if position inside this section
                if(position == 0) {
                }
                if(position < size) {
                    -- result;
                    break;
                }

                // otherwise jump into next section
                position -= size;
                -- result;
            }
            return result;
        } else  {
            return position;
        }
    }
}
