package tobin.texty.collections;


import android.content.Context;
import android.provider.Telephony;

public class InboxCollection extends BaseCollection implements CollectionInterface {
    public InboxCollection(Context context) {
        super(context, Telephony.Threads.CONTENT_URI.toString());
    }
}
