package tobin.texty.collections;

import android.content.Context;

public class ConversationCollection extends BaseCollection {
    public ConversationCollection(Context context) {
        super(context, "content://sms");
    }
}
