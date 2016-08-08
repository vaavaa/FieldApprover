package com.asiawaters.fieldapprover.classes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asiawaters.fieldapprover.FieldApprover;
import com.asiawaters.fieldapprover.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class FAExpandableListAdapter extends BaseExpandableListAdapter implements Filterable, PinnedHeaderAdapter, AbsListView.OnScrollListener {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private FAFilter filter;
    Model_ListMembers[] lst;
    List<String> listDataHeader0;
    HashMap<String, List<String>> listDataChild0;

    public FAExpandableListAdapter(Context context, List<String> listDataHeader,
                                   HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        FieldApprover FA = ((FieldApprover) _context.getApplicationContext());
        lst = FA.getList_values();
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        class ViewHolder {
            TextView firstLine;
            TextView secondLine;
            ImageView icon;
            RelativeLayout RL;
        }

        final ViewHolder viewHolder;

        if (convertView == null) {


            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.RL = (RelativeLayout) convertView.findViewById(R.id.RL_row);
            viewHolder.firstLine = (TextView) convertView.findViewById(R.id.firstLine);
            viewHolder.secondLine = (TextView) convertView.findViewById(R.id.secondLine);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (childPosition % 2 == 1) {
            viewHolder.RL.setBackgroundColor(Color.parseColor("#EFF0F1"));
        }

        Model_ListMembers vle = findMember(this._listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition));


        if (vle.isActiveBP())
            viewHolder.icon.setImageDrawable(ContextCompat.getDrawable(_context, R.drawable.ic_action_done_all));
        else
            if (vle.isActive())
                viewHolder.icon.setImageDrawable(ContextCompat.getDrawable(_context, R.drawable.ic_action_pospone));
            else
                viewHolder.icon.setImageDrawable(ContextCompat.getDrawable(_context, R.drawable.no_action));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy' 'HH:mm");
        viewHolder.firstLine.setText(vle.getTaskName() + " " + "#" + vle.getNumberOfTask());
        int ih = 0;
        final TextView tv = viewHolder.firstLine;
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewHolder.firstLine.setHeight(tv.getLineHeight() * viewHolder.firstLine.getLineCount());
                tv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        viewHolder.secondLine.setText("Date: " + sdf.format(vle.getAppointmentDateOfTask()) +
                "  Upto: " + sdf.format(vle.getTargetDatesForTheTask()));
        if (vle.isActive()) viewHolder.firstLine.setTypeface(null, Typeface.BOLD);
        else viewHolder.firstLine.setTypeface(null, Typeface.NORMAL);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);

        TextView lblcount = (TextView) convertView.findViewById(R.id.item_counter);
        lblcount.setTypeface(null, Typeface.BOLD);
        lblcount.setText("" + getChildrenCount(groupPosition));

        return convertView;
    }

    public void configurePinnedHeader(View v, int position, int alpha) {
        TextView header = (TextView) v.findViewById(R.id.lblListHeader);
        final String title = (String) getGroup(position);

        header.setText(title);
        if (alpha == 255) {
            header.setBackgroundColor(Color.WHITE);
            header.setTextColor(Color.BLACK);
        } else {
            header.setBackgroundColor(Color.argb(alpha,
                    Color.red(Color.WHITE),
                    Color.green(Color.WHITE),
                    Color.blue(Color.WHITE)));
            header.setTextColor(Color.argb(alpha,
                    Color.red(Color.WHITE),
                    Color.green(Color.WHITE),
                    Color.blue(Color.WHITE)));
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view instanceof PinnedHeaderExpListView) {
            ((PinnedHeaderExpListView) view).configureHeaderView(firstVisibleItem);
        }

    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
    }



    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public Filter getFilter() {
        if (filter == null) filter = new FAFilter();
        return filter;
    }

    private Model_ListMembers findMember(String codeIsIn) {
        for (Model_ListMembers Model : lst) {
            if (Model.getGuidTask().equals(codeIsIn)) {
                return Model;
            }
        }
        return null;
    }

    private class FAFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence cs) {
            listDataHeader0 = new ArrayList<String>();
            listDataChild0 = new HashMap<String, List<String>>();

            FilterResults results = new FilterResults();
            if (cs == null || cs.length() == 0) {

                for (int i = 0; i < lst.length; i++) {
                    if (i == 0)
                        listDataHeader0.add(lst[i].getTemplate());
                    else {
                        if (!listDataHeader0.contains(lst[i].getTemplate()))
                            listDataHeader0.add(lst[i].getTemplate());
                    }
                }

                List<String>[] NestedList = new ArrayList[listDataHeader0.size()];

                for (int i = 0; i < listDataHeader0.size(); i++) {
                    NestedList[i] = new ArrayList<String>();
                    for (int ii = 0; ii < lst.length; ii++) {
                        if (lst[ii].getTemplate().equals(listDataHeader0.get(i)))
                            NestedList[i].add(lst[ii].getGuidTask());
                    }
                    listDataChild0.put(listDataHeader0.get(i), NestedList[i]);
                }
            } else {


                for (int ii = 0; ii < lst.length; ii++) {
                    if ((lst[ii].getTaskName().toLowerCase().contains(cs.toString().toLowerCase())) ||
                            (lst[ii].getNumberOfTask().toLowerCase().contains(cs.toString().toLowerCase()))) {
                        if (!listDataHeader0.contains(lst[ii].getTemplate()))
                            listDataHeader0.add(lst[ii].getTemplate());
                    }
                }
            }
            List<String>[] NestedList = new ArrayList[listDataHeader0.size()];
            for (int i = 0; i < listDataHeader0.size(); i++) {
                NestedList[i] = new ArrayList<String>();
                for (int ii = 0; ii < lst.length; ii++) {
                    if (lst[ii].getTemplate().equals(listDataHeader0.get(i)))
                        if ((lst[ii].getTaskName().toLowerCase().contains(cs.toString().toLowerCase())) ||
                                (lst[ii].getNumberOfTask().toLowerCase().contains(cs.toString().toLowerCase()))) {
                            NestedList[i].add(lst[ii].getGuidTask());
                        }
                }
                listDataChild0.put(listDataHeader0.get(i), NestedList[i]);
            }

            results.count = listDataChild0.size();
            results.values = listDataChild0;
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                _listDataChild = listDataChild0;
                _listDataHeader = listDataHeader0;
                notifyDataSetChanged();
            }
        }
    }
}
