package app.tfkproject.taekwondoclient;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by taufik on 29/05/18.
 */

public class ListServerHolder extends RecyclerView.ViewHolder {

    public TextView nama, status;
    public ImageView img_sts;
    public CardView cardItem;

    public ListServerHolder(View view){
        super(view);
        nama = (TextView) view.findViewById(R.id.txt_nama);
        status = (TextView) view.findViewById(R.id.txt_status);
        img_sts = (ImageView) view.findViewById(R.id.img_status);
        cardItem = (CardView) view.findViewById(R.id.card_item);
    }
}
