package com.asiawaters.fieldapprover.classes;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asiawaters.fieldapprover.R;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

public class KeyValueTableDataAdapter extends TableDataAdapter<Model_TaskListFields> {

    public KeyValueTableDataAdapter(Context context, List<Model_TaskListFields> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Model_TaskListFields KeyValue = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                renderedView = renderedViewKey(KeyValue);
                break;
            case 1:
                renderedView = renderedViewValue(KeyValue);
                break;
        }
        return renderedView;
    }

    private View renderedViewKey(Model_TaskListFields KeyValue) {
        View child = getLayoutInflater().inflate(R.layout.key_resource, null);
        TextView tv =  (TextView)child.findViewById(R.id.textkey);
        if (KeyValue!=null) {
        tv.setText(KeyValue.getKey());
        }
        else tv.setText("");
        return child;
    }

    private View renderedViewValue(Model_TaskListFields KeyValue) {
        View child = getLayoutInflater().inflate(R.layout.key_value, null);
        TextView tv =  (TextView)child.findViewById(R.id.textvalue);
        if (KeyValue!=null) {
            tv.setText(KeyValue.getValue());
        }
        else tv.setText("");
        return child;
    }


}
