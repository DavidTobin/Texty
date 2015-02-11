package tobin.texty.models;


import android.provider.Telephony;

import java.util.HashMap;

public class ConversationModel extends Model {
    private static String ME = "Me";

    public ConversationModel(HashMap<String, String> data) {
        super(data);
    }

    public String getSender() {
        if (this.getValue(Telephony.Sms.TYPE).equalsIgnoreCase("1")) {
            return this.getValue(Telephony.Sms.ADDRESS);
        } else {
            return ME;
        }
    }
}
