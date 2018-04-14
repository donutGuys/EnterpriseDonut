package uk.ac.tees.q5113445live.enterpriseproject2;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uk.ac.tees.q5113445live.enterpriseproject2.TestFragment.OnListFragmentInteractionListener;
import uk.ac.tees.q5113445live.enterpriseproject2.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Advert> mValues;
    private final OnListFragmentInteractionListener mListener;
    private TextView userBid;
    private DataSnapshot userData;

    private DatabaseReference mDatabase;
    private DatabaseReference uid;
    private Button updateBid;
    private String userKey;
    private FirebaseUser fUser;
    //private ArrayList advertKey;
    private String userBidOn;
    private HashMap<String, String> advertMap;
    private int pos;
    private ArrayList value;

    public MyItemRecyclerViewAdapter(List<Advert> items, OnListFragmentInteractionListener listener)
    {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        pos = -1;
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        final View parentView =LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_list, parent, false);

        //advertKey = new ArrayList();
        advertMap = new HashMap<>();

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("advert");

        // Attach a listener to read the data at our posts reference
//        mDatabase.addValueEventListener(new ValueEventListener()
//        {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot)
//            {
//                {
//                    //Initialises user to stored data and populates TextViews in layout.Advert advert = userData.getValue(Advert.class);
//                    //Creates separate section for bids
////                    userBidOn = dataSnapshot.getKey();
////                    Advert advert = dataSnapshot.getValue(Advert.class);
////
////                     Getting current user Id
//                    uid = FirebaseDatabase.getInstance().getReference("advert");
////                     Filter User
//                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren())
//                    {
//                        if (!userSnapshot.getKey().equals(fUser.getUid()))
//                        {
//                            userBidOn = userSnapshot.getKey();
//                            for (DataSnapshot advertSnapshot: userSnapshot.getChildren())
//                            {
//                                advertMap.put( advertSnapshot.getKey().toString(),userBidOn);
//                                System.out.println("HELLO");
//                            }
//                        }
//                    }
//                      //userData = dataSnapshot;
////                    bidText(advert,view);
//
//                    //dataSnapshot.getKey();
//                    for (DataSnapshot q : dataSnapshot.getChildren())
//                    {
//                        Advert advert = dataSnapshot.getValue(Advert.class);
//                        if(!advertKey.contains(q.getKey()))
//                        {
//                            advertKey.add(q.getKey());
//                        }
//                        //userData = dataSnapshot;
//                       // bidText(advert,view);
//                    }
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError)
//            {
//
//            }
//            });

//            updateBid = view.findViewById(R.id.updateBid);
//            updateBid.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                updateBid(view);
//            }
//        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.mItem = mValues.get(position);
        holder.n.setText(mValues.get(position).getName());
        holder.c.setText(mValues.get(position).getFrom());
        holder.d.setText(mValues.get(position).getTo());
        //holder.s.setText(mValues.get(position).getBid());


        holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem.getDeliveryType());
                    pos = position;
                    notifyDataSetChanged();
                }

            }
        });
        if(pos == position)
        {

            holder.mView.setBackgroundColor(Color.GREEN);
        }
        else
        {
            holder.mView.setBackgroundColor(Color.WHITE);
        }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView n;
        public final TextView c;
        public final TextView d;
        //public final TextView s;

        public Advert mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            //mBid = view;
            n = view.findViewById(R.id.itemName);
            c = (TextView) view.findViewById(R.id.collect);
            d = (TextView) view.findViewById(R.id.deliver);
            //s = (TextView) view.findViewById(R.id.bid);

        }

        @Override
        public String toString()
        {
            return super.toString() + " '" +n.getText() +c.getText() + "'" + d.getText();
        }
    }

    public void bidText(Advert advert, View view)
    {
        //userBid = view.findViewById(R.id.bid);
        //userBid.setText(advert.getBid());
    }

    public void updateBid(final View view, final ArrayList<String> advertKey, DatabaseReference reference, final HashMap<String, String> advertMap)
    {
        //Trying to return name after bid is placed!
        userKey = fUser.getUid();

        HashMap<Object, Object> result = new HashMap<>();
        TextView bidUser = view.findViewById(R.id.enterBid);
        result.put(userKey, bidUser.getText().toString());
        reference.child(advertMap.get(String.valueOf(advertKey.get(pos)))).
                    child(String.valueOf(advertKey.get(pos))).child("bid").setValue(result);
        System.out.println("HELLO");
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        final DatabaseReference reference = firebaseDatabase.getReference();
//        reference.child("advert").addListenerForSingleValueEvent(new ValueEventListener()
//        {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot)
//            {
//                //User user = dataSnapshot.getValue(User.class);
//                HashMap<Object, Object> result = new HashMap<>();
//                TextView bidUser = view.findViewById(R.id.enterBid);
//                result.put(userKey, bidUser.getText().toString());
//                reference.child("advert").child(advertMap.get(String.valueOf(advertKey.get(pos)))).child(String.valueOf(advertKey.get(pos))).child("bid").setValue(result);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                //Logger.error(TAG, ">>> Error:" + "find onCancelled:" + databaseError);
//
//            }
//        });
    }
    public void findUser()
    {

    }
}
