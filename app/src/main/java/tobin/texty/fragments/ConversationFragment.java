package tobin.texty.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import tobin.texty.R;
import tobin.texty.collections.ConversationCollection;
import tobin.texty.viewadapters.ConversationViewAdapter;

/**
 * Inbox fragment view.
 */
public class ConversationFragment extends Fragment {
    ConversationCollection conversation;
    Collection<HashMap<String, String>> conversationData;
    ConversationViewAdapter viewAdapter;
    ListView listView;

    int conversationId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView       = inflater.inflate(R.layout.fragment_conversation, container, false);
        Bundle args         = getArguments();

        this.conversationId = args.getInt("conversationId");

        // Setup adapters
        conversation    = new ConversationCollection(rootView.getContext());
        viewAdapter     = new ConversationViewAdapter(rootView.getContext(), getActivity());
        listView        = (ListView) rootView.getRootView().findViewById(R.id.conversation_list);

        this.populateFragment();
        this.convertListViewToPartial();
        this.bindEvents();

        this.hideActionBar();

        return rootView;
    }

    private void hideActionBar() {
        getActivity().getActionBar().hide();
    }

    private void bindEvents() {

    }

    private void convertListViewToPartial() {
        listView.setAdapter(viewAdapter);
    }

    private void populateFragment() {
        String condition = Telephony.Sms.Conversations.THREAD_ID + " = \"" + conversationId + "\"";
        Iterator<HashMap<String, String>> iterator;

        conversationData    = conversation.getWhere(condition, "DATE asc");
        iterator            = conversationData.iterator();

        while (iterator.hasNext()) {
            HashMap<String, String> item = iterator.next();

            viewAdapter.add(item);
        }
    }
}