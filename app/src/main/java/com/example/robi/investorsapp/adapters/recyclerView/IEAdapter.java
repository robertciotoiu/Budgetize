package com.example.robi.investorsapp.adapters.recyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robi.investorsapp.R;
import com.example.robi.investorsapp.localdatabase.entities.ie.IEObject;

import java.util.List;

public class IEAdapter extends RecyclerView.Adapter<IEAdapter.SubViewHolder> {
    private List<IEObject> ieObjectList;

    IEAdapter(List<IEObject> subItemList) {
        this.ieObjectList = subItemList;
    }

    @NonNull
    @Override
    public SubViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_ie, viewGroup, false);
        return new SubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubViewHolder subItemViewHolder, int i) {
        IEObject ieObject = ieObjectList.get(i);
        //subItemViewHolder.ieIcon.setText(ieObject.getSubItemTitle()); //to be implemented
        subItemViewHolder.ieValue.setText(String.valueOf(ieObject.amount));
        subItemViewHolder.textViewTitle.setText(String.valueOf(ieObject.name));
        subItemViewHolder.ieDescription.setText("TO BE IMPLEMENTED");
    }

    @Override
    public int getItemCount() {
        return ieObjectList.size();
    }

    class SubViewHolder extends RecyclerView.ViewHolder {
        ImageView ieIcon;
        TextView ieValue;
        TextView textViewTitle;
        TextView ieDescription;

        SubViewHolder(View itemView) {
            super(itemView);
            ieIcon = itemView.findViewById(R.id.ie_icon);
            ieValue = itemView.findViewById(R.id.text_view_ie_value);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            ieDescription = itemView.findViewById(R.id.text_view_ie_description);
        }
    }
}
