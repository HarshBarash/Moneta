package harshbarash.github.moneta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.Year;

import harshbarash.github.monetaandroid.R;

public class Adapter extends CursorAdapter {

    public Adapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nominalView, yearView, conditionView, priceView;


        nominalView = view.findViewById(R.id.textNominal);
        yearView = view.findViewById(R.id.textYear);
        conditionView = view.findViewById(R.id.textCondition);
        priceView = view.findViewById(R.id.textPrice);



        int nominal = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_NOMINAL);
        int condition = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_CONDITION);
        int magnet = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_MAGNET);
        int stamp = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_STAMP);
        int kant = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_TYPE);
        int gurt = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_MATERIAL);
        int year = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_YEAR);
        int price = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_PRICE);
        int description = cursor.getColumnIndex(Moneta.CoinEntry.COLUMN_DESCRIPTION);

        String nominal_tos = cursor.getString(nominal);
        String condition_tos = cursor.getString(condition);
        String magnet_tos = cursor.getString(magnet);
        String stamp_tos = cursor.getString(stamp);
        String kant_tos = cursor.getString(kant);
        String gurt_tos = cursor.getString(gurt);
        String year_tos = cursor.getString(year);
        String price_tos = cursor.getString(price);
        String description_tos = cursor.getString(description);

        yearView.setText("Год: " + year_tos);

        if (price_tos.equals("0")) {
            priceView.setText("Не для продажи");
        } else{
            priceView.setText("Моя цена: " + price_tos + " руб");
        }

        nominalView.setText(nominal_tos);
        conditionView.setText("Состояние: " + condition_tos);


        if (nominal_tos.equals(Moneta.CoinEntry.TYPEOFNOMINAL_1RUBEL)) {
            ImageView mCoinImageView = view.findViewById(R.id.imageStub);
            mCoinImageView.setImageResource(R.drawable.rubel1);
        } else if (nominal_tos.equals(Moneta.CoinEntry.TYPEOFNOMINAL_2RUBEL)){
            ImageView mCoinImageView = view.findViewById(R.id.imageStub);
            mCoinImageView.setImageResource(R.drawable.rubel2);
        } else if (nominal_tos.equals(Moneta.CoinEntry.TYPEOFNOMINAL_5RUBEL)){
            ImageView mCoinImageView = view.findViewById(R.id.imageStub);
            mCoinImageView.setImageResource(R.drawable.rubel5);
        } else {
            ImageView mCoinImageView = view.findViewById(R.id.imageStub);
            mCoinImageView.setImageResource(R.drawable.rubel10);
        }

    }
}
