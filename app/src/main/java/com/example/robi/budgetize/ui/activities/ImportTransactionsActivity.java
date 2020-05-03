package com.example.robi.budgetize.ui.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.robi.budgetize.R;
import com.example.robi.budgetize.backend.APIs.expandingviewAPI.ExpandingItem;
import com.example.robi.budgetize.backend.APIs.expandingviewAPI.ExpandingList;

import java.util.List;

public class ImportTransactionsActivity extends AppCompatActivity {
    private ExpandingList mExpandingList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_transactions);
        mExpandingList = findViewById(R.id.expanding_list_main);
    }

    private void populateLists() {
        //Display Wallet Name:
//        ExpandingItem first_item = mExpandingList.createNewItem(R.layout.first_ie_item);
//        if (first_item != null) {
//            ((TextView) first_item.findViewById(R.id.wallet_name_ie_screen)).setText(wallet.getName());//TODO
//        }

        //Add Categories and IEs
        for (CategoryObject categoryObject : categoryObjectsList) {
            addItem(categoryObject);
//            executor.execute(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });
        }
    }

    private void depopulateLists() {
        mExpandingList.removeAllViews();
    }

    private void addItem(final CategoryObject categoryObject) {
        double ie_value = mainActivityViewModel.getCategoryIESUM(categoryObject.getCategory_id());
        List<IEObject> ieObjectList = mainActivityViewModel.getCategorysIE(categoryObject.getCategory_id());

        IEActivityDiegodobelo.this.runOnUiThread(() -> {
            final ExpandingItem item = getCorrectItem(ie_value);
            if (item != null) {
                //setAllColors(ie_value);
                item.setIndicatorColor(Color.WHITE);
                //item.setIndicatorColorRes(getIconIndicatorColor(ie_value));//R.color.positiveBackgroundColor);
                item.setIndicatorIcon(getIcon(ie_value));//R.drawable.house_icon);
                //1. ImageView category_icon
                ((TextView) item.findViewById(R.id.text_view_value_white)).setText(String.valueOf(ie_value));
                ((TextView) item.findViewById(R.id.text_view_category_name_white)).setText(categoryObject.getName());
                ((TextView) item.findViewById(R.id.text_view_description_white)).setText(categoryObject.getDescription());
                item.createSubItems(ieObjectList.size());
                for (int i = 0; i < item.getSubItemsCount(); i++) {
                    //Let's get the created sub item by its index
                    final View view = item.getSubItemView(i);
//                if(i== item.getSubItemsCount()-1) {
//                    RelativeLayout layout = view.findViewById(R.id.layout_subitem);
//                    ViewGroup.LayoutParams params = layout.getLayoutParams();
//                    params.height = 300;
//                    view.setLayoutParams(params);
//                }
                    //Let's set some values in
                    configureSubItem(item, view, ieObjectList.get(i), categoryObject);
                }
//            item.findViewById(R.id.add_more_sub_items).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //launch create Category activity
//                    Intent myIntent = new Intent(IEActivityDiegodobelo.this, CreateIEActivity.class);
//                    myIntent.putExtra("wallet", walletID); //Optional parameters
//                    IEActivityDiegodobelo.this.startActivity(myIntent);
//                }
//            });
                item.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        importItemClickListener(categoryObject, item);
                    }
                });
            }
            Log.d("UI thread", "I am the UI thread");
        });
    }

    private void configureSubItem(final ExpandingItem item, final View view, final IEObject ieObject, final CategoryObject categoryObject) {
        //subItemViewHolder.ieIcon.setText(ieObject.getSubItemTitle()); //to be implemented
        if (ieObject.type == 1) {
            ((TextView) view.findViewById(R.id.text_view_ie_value_white)).setText("-" + String.valueOf(ieObject.amount));
        } else {
            ((TextView) view.findViewById(R.id.text_view_ie_value_white)).setText(String.valueOf(ieObject.amount));
        }
        ((TextView) view.findViewById(R.id.text_view_title_white)).setText(String.valueOf(ieObject.name));
        //((TextView) view.findViewById(R.id.text_view_ie_description_white)).setText("TO BE IMPLEMENTED");
        view.findViewById(R.id.remove_sub_item_white).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importSubItemClickListener(ieObject.getId(), item, view, categoryObject);
            }
        });
    }

    private void importItemClickListener(Object object, final ExpandingItem expandingItem) {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        try {
                            if (object instanceof IEObject) {
                                mainActivityViewModel.deleteIE(((IEObject) object).getId());
                                mExpandingList.removeView(expandingItem);
                            } else {
                                mainActivityViewModel.deleteCategory(((CategoryObject) object).getCategory_id());
                                mExpandingList.removeView(expandingItem);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                            Toast toast = Toast.makeText(IEActivityDiegodobelo.this, "Unable to delete the Category", Toast.LENGTH_SHORT);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            v.setTextColor(Color.RED);
                            toast.show();
                        }


                        //Yes button clicked
                        break;
                    }

                    case DialogInterface.BUTTON_NEGATIVE: {
                        //No button clicked
                        break;
                    }
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want do DELETE permanently this Category?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void importSubItemClickListener(final long ieID, final ExpandingItem expandingItem, final View view, final CategoryObject categoryObject) {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        try {
                            //TODO:issue here, when we delete one IE, we must removeSubItem() and recalculate category
                            mainActivityViewModel.deleteIE(ieID);
                            Toast.makeText(IEActivityDiegodobelo.this, "I/E Deleted", Toast.LENGTH_SHORT).show();
                            expandingItem.removeSubItem(view);
                            recalculateCategorySum(expandingItem, categoryObject);
//                            mExpandingList.removeAllViews();
//                            populateLists();

                            //mExpandingList.replaceItem(expandingItem);
                            //refresh_category(expandingItem, view, categoryObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                            Toast toast = Toast.makeText(IEActivityDiegodobelo.this, "Unable to delete the I/E", Toast.LENGTH_SHORT);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            v.setTextColor(Color.RED);
                            toast.show();
                        }
                        //Yes button clicked
                        break;
                    }

                    case DialogInterface.BUTTON_NEGATIVE: {
                        //No button clicked
                        break;
                    }
                }
            }
            /*
            private void refresh_category(ExpandingItem expandingItem, View view, CategoryObject categoryObject) {
                double ieValue = MainActivity.myDatabase.categoryDao().getCategoryIESUM(wallet.getId(), categoryObject.getName());
                expandingItem.setIndicatorIcon(getIcon(ieValue));

                if (ieValue > 0) {
                    expandingItem.setBackgroundResource(R.drawable.item_shape_positive);
                } else if (ieValue < 0) {
                    expandingItem.setBackgroundResource(R.drawable.item_shape_negative);
                } else {
                    expandingItem.setBackgroundResource(R.drawable.item_shape_neutral);
                }
                TextView value = (TextView) expandingItem.findViewById(R.id.text_view_value_white);
                value.setText(String.valueOf(ieValue));
            }

             */
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want do DELETE permanently this I/E?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private Drawable getIcon(double ie) {
        //have to return icon id
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.house_icon);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        if (ie > 0) {
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.positiveBackgroundColor));
        } else if (ie < 0) {
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.negativeBackgroundColor));
        } else {
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.neutralBackgroundColor));
        }
        return wrappedDrawable;
    }
}
