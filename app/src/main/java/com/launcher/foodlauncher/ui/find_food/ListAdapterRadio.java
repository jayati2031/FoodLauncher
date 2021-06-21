package com.launcher.foodlauncher.ui.find_food;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.launcher.foodlauncher.R;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterRadio extends RecyclerView.Adapter<ListAdapterRadio.ViewHolder>{

    private String[] array;
    private Context context;
    private int rowLayout;
    private List<Boolean> checked = new ArrayList<>();
    private List<String> selectedValue = new ArrayList<>();
    List<ViewHolder> viewHolder = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder{
        RadioButton radioButton;
        public ViewHolder(View v) {
            super(v);
            radioButton = (RadioButton) v.findViewById(R.id.input_radio);
        }
    }

    public ListAdapterRadio(String[] array, int rowLayout, Context context) {
        this.array = array;
        this.rowLayout = rowLayout;
        this.context = context;
        selectedValue.add(0, "");
    }

    @NonNull
    @Override
    public ListAdapterRadio.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ListAdapterRadio.ViewHolder holder, int position) {
        holder.radioButton.setText(array[position]);
        viewHolder.add(position, holder);
        checked.add(position, false);
        int i = position;
        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checked.get(position)) {
                    deselect(i);
                    holder.radioButton.setChecked(false);
                    selectedValue.set(0, "");
                } else {
                    deselect(i);
                    holder.radioButton.setChecked(true);
                    checked.set(i, true);
                    selectedValue.set(0, array[position]);
                }
            }
        });
    }

    private void deselect(int i) {
        for(int j = 0; j<array.length; j++) {
            if(j != i) {
                viewHolder.get(j).radioButton.setChecked(false);
            }
            checked.set(j, false);
        }
    }

    public String getCheckedValue() {
        if(selectedValue.get(0).length() == 0)
            selectedValue.set(0, "null");
        return selectedValue.get(0);
    }

    @Override
    public int getItemCount() {
        return array.length;
    }
}