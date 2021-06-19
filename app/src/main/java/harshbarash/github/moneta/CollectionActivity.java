package harshbarash.github.moneta;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import harshbarash.github.monetaandroid.R;

//раздел коллекция
public class CollectionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public Adapter mAdapter;
    public static final int COINLOADER = 0;
    ImageButton back;


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_collection);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(CollectionActivity.this, DefineActivity.class);
            startActivity(intent);
        });


        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }

        });


        ListView listView = findViewById(R.id.list);
        mAdapter = new Adapter(this, null);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
        Intent intent = new Intent(CollectionActivity.this, DefineActivity.class);
            Uri newUri = ContentUris.withAppendedId(Moneta.CoinEntry.CONTENT_URI, id);
            intent.setData(newUri);
            startActivity(intent);
    });

        getLoaderManager().initLoader(COINLOADER, null, this);

}

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                Moneta.CoinEntry._ID,
                Moneta.CoinEntry.COLUMN_NOMINAL,
                Moneta.CoinEntry.COLUMN_CONDITION,
                Moneta.CoinEntry.COLUMN_MAGNET,
                Moneta.CoinEntry.COLUMN_STAMP,
                Moneta.CoinEntry.COLUMN_TYPE,
                Moneta.CoinEntry.COLUMN_MATERIAL,
                Moneta.CoinEntry.COLUMN_YEAR,
                Moneta.CoinEntry.COLUMN_PRICE,
                Moneta.CoinEntry.COLUMN_DESCRIPTION};


        return new CursorLoader(this, Moneta.CoinEntry.CONTENT_URI,
                projection, null,
                null,
                null);
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);

    }


}

