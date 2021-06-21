package com.launcher.foodlauncher.ui.find_food;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.launcher.foodlauncher.R;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterCheck extends RecyclerView.Adapter<ListAdapterCheck.ViewHolder>{

    private String[] array;
    private Context context;
    private int rowLayout;
    private List<Boolean> checked = new ArrayList<>();
    private List<String> selected = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        public ViewHolder(View v) {
            super(v);
            checkBox = (CheckBox) v.findViewById(R.id.input_check);
        }
    }

    public ListAdapterCheck(String[] array, int rowLayout, Context context) {
        this.array = array;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @NonNull
    @Override
    public ListAdapterCheck.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ListAdapterCheck.ViewHolder holder, int position) {
        holder.checkBox.setText(array[position]);
        checked.add(position, false);
        selected.add(position, "");

        int i = position;
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checked.get(i)) {
                    holder.checkBox.setChecked(false);
                    checked.set(i, false);
                    selected.set(i, "");
                } else {
                    holder.checkBox.setChecked(true);
                    checked.set(i, true);
                    try{
                        selected.add(i, array[i]);
                    } catch (Exception e) {
                        Log.i("TAG", "onClick: EXCEPTION hello" + e.getMessage());
                    }
                }
            }
        });
    }

    public String getCheckedValue() {
        StringBuilder selectedValue = new StringBuilder();
        for(int i=0; i<selected.size(); i++) {
            if(!selected.get(i).equals(""))
                selectedValue.append(selected.get(i)).append(",");
        }
        if(selectedValue.length() == 0)
            selectedValue.append("null");
        else
            selectedValue.deleteCharAt(selectedValue.lastIndexOf(","));
        return selectedValue.toString();
    }

    @Override
    public int getItemCount() {
        return array.length;
    }

}