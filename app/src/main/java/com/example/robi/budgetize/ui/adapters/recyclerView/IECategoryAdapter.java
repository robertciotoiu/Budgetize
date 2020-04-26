//package com.example.robi.budgetize.ui.adapters.recyclerView;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.robi.budgetize.R;
//import com.example.robi.budgetize.data.localdatabase.entities.CategoryObject;
//import com.example.robi.budgetize.data.localdatabase.entities.IEObject;
//import com.example.robi.budgetize.data.localdatabase.entities.Wallet;
//import com.example.robi.budgetize.ui.activities.MainActivity;
//
//import java.util.List;
//
//public class IECategoryAdapter extends RecyclerView.Adapter<IECategoryAdapter.ViewHolder> {
//    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
//    private List<IEObject> ieObjects;
//    private Wallet wallet;
//    private List<CategoryObject> categoryObjectsList;
//
//    private LayoutInflater mInflater;
//    private ItemClickListener mClickListener;
//
//    // data is passed into the constructor
//    public IECategoryAdapter(Context context, Wallet wallet, ItemClickListener mClickListener) {
//        this.mInflater = LayoutInflater.from(context);
//        this.wallet = wallet;
//        this.categoryObjectsList = buildCategoryList();
//        this.mClickListener = mClickListener;
//    }
//
//    private List<CategoryObject> buildCategoryList() {
//        return MainActivity.myDatabase.categoryDao().getAllCategoriesOfAWallet(wallet.getId());
//    }
//
//    // inflates the row layout from xml when needed
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = mInflater.inflate(R.layout.recycle_view_category, parent, false);
//        return new ViewHolder(view, mClickListener);
//    }
//
//    // binds the data to the TextView in each row
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        //MainOAuthActivity.myDatabase.ieoDao().getIESpecificList();
//        CategoryObject categoryObject = categoryObjectsList.get(position);
//        List<IEObject> ieObjectList = MainActivity.myDatabase.categoryDao().getCategorysIE(wallet.getId(), categoryObject.getName());
//        //holder.categoryIcon.draw();
//        holder.textViewValue.setText(String.valueOf(MainActivity.myDatabase.categoryDao().getCategoryIESUM(categoryObject.getWallet_id(), categoryObject.getName())));//EDIT TO VIEW BASED ON SUM OF ALL IE Objects (ANOTHER VIEW)
//        holder.categoryName.setText(categoryObject.getName());
//        holder.textViewDescription.setText(categoryObject.getDescription());
//
//        //Sub RecyclerView
//        LinearLayoutManager layoutManager = new LinearLayoutManager(
//                holder.rvIE.getContext(),
//                LinearLayoutManager.VERTICAL,
//                false
//        );
//        layoutManager.setInitialPrefetchItemCount(ieObjectList.size());
//
//        // Create sub item view adapter
//        IEAdapter subItemAdapter = new IEAdapter(ieObjectList);
//
//        holder.rvIE.setLayoutManager(layoutManager);
//        holder.rvIE.setAdapter(subItemAdapter);
//        holder.rvIE.setRecycledViewPool(viewPool);
//
//    }
//
//    // total number of rows
//    @Override
//    public int getItemCount() {
//        return categoryObjectsList.size();
//    }
//
//
//    // stores and recycles views as they are scrolled off screen
//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        ImageView categoryIcon;
//        TextView textViewValue;
//        TextView categoryName;
//        TextView textViewDescription;
//        RecyclerView rvIE;
//
//        ItemClickListener itemClickListener;
//
//
//        public ViewHolder(View itemView, ItemClickListener itemClickListener) {
//            super(itemView);
//            categoryIcon = itemView.findViewById(R.id.category_icon);
//            textViewValue = itemView.findViewById(R.id.text_view_value);
//            categoryName = itemView.findViewById(R.id.text_view_category_name);
//            textViewDescription = itemView.findViewById(R.id.text_view_description);
//            rvIE = itemView.findViewById(R.id.subRview);
//            this.itemClickListener = itemClickListener;
//
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View view) {
//            if (itemClickListener != null)
//                itemClickListener.onItemClick(view, getAdapterPosition());
//
//        }
//    }
//
//    // convenience method for getting data at click position
//    IEObject getItem(int id) {
//        return ieObjects.get(id);
//    }
//
//    // allows clicks events to be caught
//    public void setClickListener(ItemClickListener itemClickListener) {
//        this.mClickListener = itemClickListener;
//    }
//
//    // parent activity will implement this method to respond to click events
//    public interface ItemClickListener {
//        void onItemClick(View view, int position);
//    }
//}