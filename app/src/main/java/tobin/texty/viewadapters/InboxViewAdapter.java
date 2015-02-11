package tobin.texty.viewadapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import java.util.HashMap;

import tobin.texty.R;
import tobin.texty.collections.ContactCollection;
import tobin.texty.models.MessageModel;
import tobin.texty.views.RoundImageView;

public class InboxViewAdapter extends ArrayAdapter<HashMap<String, String>> {
    private final ContactCollection contacts = new ContactCollection(getContext());

    int partialView = R.layout.partial_inbox_message;

    public InboxViewAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public int getItemViewType(int position) {
        HashMap<String, String> item = getItem(position);

        return Integer.parseInt(item.get(Telephony.Sms.Conversations._ID));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HashMap<String, String> item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(partialView, parent, false);
        }

        convertView = this.populateViewsWithData(convertView, item, contacts.findByAddress(item.get(Telephony.Sms.Conversations.ADDRESS)));

        return convertView;
    }

    private View populateViewsWithData(View convertView, HashMap<String, String> item, HashMap<String, String> contact) {
        MessageModel model = new MessageModel(item);

        Log.d("MATCH", contact.get("normalized_number") + " matched to " + item.get("address"));

        TextView contactName    = (TextView) convertView.findViewById(R.id.contact_name);
        TextView message        = (TextView) convertView.findViewById(R.id.message);
        TextView time           = (TextView) convertView.findViewById(R.id.time);
        RoundImageView badge    = (RoundImageView) convertView.findViewById(R.id.contact_badge);

        if (contact.get(ContactsContract.PhoneLookup.DISPLAY_NAME) != null) {
            this.setupContactBadge(contact, badge);

            contactName.setText(contact.get(ContactsContract.PhoneLookup.DISPLAY_NAME));
        } else {
            this.setDefaultContactImage(badge, model);

            contactName.setText(model.getValue(Telephony.Sms.ADDRESS));
        }

        message.setText(model.getValue(Telephony.Sms.BODY));
        time.setText(model.toShortDate(Telephony.Sms.DATE));

        return convertView;
    }

    private void setupContactBadge(final HashMap<String, String> contact, RoundImageView badge) {
        String imageUriString = contact.get(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI);
        Uri imageUri;

        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);

            badge.setImageURI(imageUri);
        } else {
            this.setDefaultContactImage(badge);
        }

        if (contact.get(ContactsContract.PhoneLookup._ID) != null) {
            badge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contact.get(ContactsContract.PhoneLookup._ID));

                    intent.setData(uri);

                    getContext().startActivity(intent);
                }
            });
        }
    }

    private void setDefaultContactImage(RoundImageView badge) {
        badge.setImageResource(R.mipmap.man);
    }

    private void setDefaultContactImage(RoundImageView badge, final MessageModel model) {
        this.setDefaultContactImage(badge);

        badge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

                intent.putExtra(ContactsContract.Intents.Insert.PHONE, model.getValue(Telephony.Sms.ADDRESS));

                getContext().startActivity(intent);
            }
        });
    }
}