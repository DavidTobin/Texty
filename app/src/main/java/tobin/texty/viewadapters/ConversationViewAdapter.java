package tobin.texty.viewadapters;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import tobin.texty.R;
import tobin.texty.collections.ContactCollection;
import tobin.texty.models.ConversationModel;
import tobin.texty.views.RoundImageView;

public class ConversationViewAdapter extends ArrayAdapter<HashMap<String, String>> {
    private final Activity activity;

    HashMap<String, String> contact;

    int inboundPartialView  = R.layout.partial_conversation_inbound;
    int outboundPartialView = R.layout.partial_conversation_outbound;

    public ConversationViewAdapter(Context context, Activity activity) {
        super(context, 0);

        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        HashMap<String, String> item = getItem(position);

        return Integer.parseInt(item.get(Telephony.Sms.TYPE));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HashMap<String, String> item = getItem(position);
        ConversationModel model = new ConversationModel(item);
        HashMap<String, String> contact = new ContactCollection(getContext()).findByAddress(model.getValue(Telephony.Sms.ADDRESS));

        // Check if an existing view is being reused, otherwise inflate the view
        switch (getItemViewType(position)) {
            case 1:
                convertView = LayoutInflater.from(getContext()).inflate(inboundPartialView, parent, false);
                break;

            case 2:
                convertView = LayoutInflater.from(getContext()).inflate(outboundPartialView, parent, false);
                break;
        }

        convertView = this.populateViewsWithData(convertView, model);

        // Update actionbar
        if (contact.get(ContactsContract.PhoneLookup.DISPLAY_NAME) != null) {
            activity.getActionBar()
                    .setTitle(contact.get(ContactsContract.PhoneLookup.DISPLAY_NAME));
        } else {
            activity.getActionBar()
                    .setTitle(model.getValue(Telephony.Sms.ADDRESS));
        }

        return convertView;
    }

    private View populateViewsWithData(View convertView, ConversationModel model) {
        TextView message                        = (TextView) convertView.findViewById(R.id.message);
        TextView time                           = (TextView) convertView.findViewById(R.id.time);
        RoundImageView contactBadge             = (RoundImageView) convertView.findViewById(R.id.contact_badge);
        RelativeLayout contactBadgeContainer    = (RelativeLayout) convertView.findViewById(R.id.contact_container);

        if (contact == null) {
            contact = new ContactCollection(getContext()).findByAddress(model.getValue(Telephony.Sms.ADDRESS));
        }

        message.setText(model.getValue(Telephony.Sms.BODY));
        time.setText(model.toShortDate(Telephony.Sms.DATE));


        if (!contact.isEmpty() && getItemViewType(getPosition(model.data)) == 1) {
            this.setupContactBadge(contact, contactBadge);
        } else {
            if (getItemViewType(getPosition(model.data)) == 1) {
                this.removeContactBadge(contactBadgeContainer);
            }
        }

        return convertView;
    }

    private void removeContactBadge(RelativeLayout contactBadgeContainer) {
        contactBadgeContainer.setLayoutParams(new RelativeLayout.LayoutParams(75, 0));
    }

    private void setupContactBadge(HashMap<String, String> contact, RoundImageView contactBadge) {
        String imageUriString = contact.get(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI);
        Uri imageUri;

        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
            contactBadge.setDrawingCacheEnabled(true);
            contactBadge.setImageURI(imageUri);
        }
    }
}