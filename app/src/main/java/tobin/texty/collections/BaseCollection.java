package tobin.texty.collections;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class BaseCollection implements CollectionInterface {
    protected Uri uri;
    protected Context context;

    public Collection<HashMap<String, String>> data = new ArrayList<>();

    public BaseCollection(Context context, String uriString) {
        if (context == null) {
            throw new Error("Missing parameter Context");
        }

        if (uriString == null) {
            throw new Error("Missing parameter URI");
        }

        this.context    = context;
        this.uri        = Uri.parse(uriString);
    }

    @Override
    public Collection<HashMap<String, String>> getAll() {
        return this.getAll("date DESC");
    }

    @Override
    public Collection<HashMap<String, String>> getAll(String order) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, order);

        return this.moveColumnsToData(cursor, true);
    }

    @Override
    public HashMap<String, String> getOne(int id) {
        Cursor cursor;
        HashMap<String, String> item;

        item = this.getItemById(id);

        if (!item.isEmpty()) {
            return item;
        }

        // Not in cache, query for it
        cursor = context.getContentResolver().query(Uri.withAppendedPath(uri, "" + id), null, null, null, null);

        this.moveColumnsToData(cursor);

        return this.getItemById(id);
    }

    @Override
    public Collection<HashMap<String, String>> getWhere(String condition, String order) {
        Cursor cursor = context.getContentResolver().query(uri, null, condition, null, order);

        return this.moveColumnsToData(cursor, true);
    }

    @Override
    public Collection<HashMap<String, String>> getWhere(String condition) {
        return this.getWhere(condition, "date DESC");
    }

    @Override
    public void groupBy(String columnName) {

    }

    protected Collection<HashMap<String, String>> moveColumnsToData(Cursor cursor, Boolean force) {
        if (cursor == null) {
            if (force) {
                this.data.clear();
            }

            return this.data;
        }

        String[] columnNames = cursor.getColumnNames();

        if (force) {
            this.data.clear();
        }

        while (cursor.moveToNext()) {
            HashMap<String, String> singleData = new HashMap<>();

            for (int i = 0; i < columnNames.length; i++) {
                singleData.put(columnNames[i], cursor.getString(cursor.getColumnIndex(columnNames[i])));
            }

            this.data.add(singleData);
        }

        cursor.close();

        return this.data;
    }

    protected HashMap<String, String> findDataByIndex(String index, String value) {
        Iterator<HashMap<String, String>> iterator = this.data.iterator();

        while (iterator.hasNext()) {
            HashMap<String, String> item = iterator.next();

            if (item.get(index).equalsIgnoreCase(value)) {
                iterator.remove();

                return item;
            }
        }

        return new HashMap<>();
    }

    protected Collection<HashMap<String, String>> moveColumnsToData(Cursor cursor) {
        return this.moveColumnsToData(cursor, false);
    }

    protected HashMap<String, String> getItemById (int _id) {
        Iterator<HashMap<String, String>> iterator = this.data.iterator();
        String id = "" + _id;

        while (iterator.hasNext()) {
            HashMap<String, String> item = iterator.next();

            if (item.get(Telephony.Sms.Conversations._ID) == id) {
                iterator.remove();

                return item;
            }
        }

        return new HashMap<>();
    }

}
