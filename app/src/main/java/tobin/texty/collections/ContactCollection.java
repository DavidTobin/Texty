package tobin.texty.collections;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import java.util.HashMap;

public class ContactCollection extends BaseCollection implements CollectionInterface {
    public ContactCollection(Context context) {
        super(context, ContactsContract.Contacts.CONTENT_URI.toString());
    }

    public HashMap<String, String> findByAddress (String address) {
        HashMap<String, String> item = this.findDataByIndex(ContactsContract.PhoneLookup.NUMBER, Uri.encode(address));

        if (!item.isEmpty()) {
            return item;
        }

        // Proxy to query
        return this.queryByAddress(address);
    }

    private HashMap<String, String> queryByAddress(String address) {
        Cursor cursor = this.context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, address), null, null, null, null);

        this.moveColumnsToData(cursor);

        return this.findDataByIndex(ContactsContract.PhoneLookup.NORMALIZED_NUMBER, PhoneNumberUtils.normalizeNumber(address));
    }
}
