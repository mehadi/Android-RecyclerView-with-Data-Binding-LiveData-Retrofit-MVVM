package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.handler;

import android.content.Context;
import android.widget.Toast;

/**
 * Created By - Mehadi
 * Created On - 2/12/2020 : 12:48 PM
 * Email - hi@mehadih.me
 * Website - www.mehadih.me
 */
public class ItemUserClickHandler {
    Context mContext;

    public ItemUserClickHandler(Context mContext) {
        this.mContext = mContext;
    }

    public void onViewButtonClick(String name) {
        Toast.makeText(mContext, "You just clicked: " + name, Toast.LENGTH_SHORT).show();
    }
}
