package tobin.texty.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import tobin.texty.R;
import tobin.texty.collections.InboxCollection;
import tobin.texty.viewadapters.InboxViewAdapter;

/**
 * Inbox fragment view.
 */
public class InboxFragment extends Fragment {
    InboxCollection inbox;
    InboxViewAdapter viewAdapter;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        // Setup adapters
        inbox       = new InboxCollection(rootView.getContext());
        viewAdapter = new InboxViewAdapter(rootView.getContext());
        listView    = (ListView) rootView.getRootView().findViewById(R.id.inbox_list);

        // Find our data
        this.populateFragment();

        this.convertListViewToPartial();
        this.bindEvents();

        // Make sure the action bar displays "Inbox"
        getActivity().getActionBar()
                .show();

        getActivity().getActionBar()
                .setTitle(R.string.inbox);

        return rootView;
    }

    private void bindEvents() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> item = viewAdapter.getItem(position);

                this.changeToInboxMessage(item);
            }

            private void changeToInboxMessage(HashMap<String, String> message) {
                ConversationFragment conversationFragment   = new ConversationFragment();
                Bundle args                                 = new Bundle();
                int threadId                                = Integer.parseInt(message.get(Telephony.Sms.Inbox.THREAD_ID));

                args.putInt("conversationId", threadId);

                conversationFragment.setArguments(args);

                getFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.container, conversationFragment, "" + threadId)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void convertListViewToPartial() {
        listView.setAdapter(viewAdapter);
    }

    private void populateFragment() {
        Collection<HashMap<String, String>> inboxData;
        Iterator<HashMap<String, String>> iterator;

        inboxData   = inbox.getAll();
        iterator    = inboxData.iterator();

        while (iterator.hasNext()) {
            HashMap<String, String> item = iterator.next();

            viewAdapter.add(item);
        }
    }
}