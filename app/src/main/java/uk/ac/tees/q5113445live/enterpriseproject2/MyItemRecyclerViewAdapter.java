package uk.ac.tees.q5113445live.enterpriseproject2;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * specified {@link OnListFragmentInteractionListener}.
 *
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Advert> mValues;
    private final List<String> mAds;
    private final OnListFragmentInteractionListener mListener;
    private static final String TAG = "Bidding Activity";
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
    private static int pos;
    private ArrayList value;
    private StorageReference mStorageRef;
    private ImageView imageView;
    private View view;
    private double userWallet;
    private double newBid;
    private String remainingWallet;
    public ArrayList<String> listBid;
    private ViewAdvertFragment advertFrag;

    public MyItemRecyclerViewAdapter(List<Advert> items,List<String> advertId, OnListFragmentInteractionListener listener)
    {
        mValues = items;
        mAds = advertId;
        mListener = listener;
    }

    public interface OnListFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onListFragmentInteraction(String title);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        pos = -1;
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        final View parentView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_list, parent, false);
        advertMap = new HashMap<>();
        //mDatabase = FirebaseDatabase.getInstance().getReference("advert");
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.mItem = mValues.get(position);
        holder.n.setText(mValues.get(position).getName());
        holder.c.setText(mValues.get(position).getFrom());
        holder.d.setText(mValues.get(position).getTo());
        holder.s.setText(mValues.get(position).getSize());
        holder.w.setText(mValues.get(position).getWeight());
        mStorageRef = FirebaseStorage.getInstance().getReference("AdvertImage").child(mAds.get(position));

        //______________________________________________________________________________________
        advertFrag = new ViewAdvertFragment();
        //______________________________________________________________________________________

        //holder.s.setText(mValues.get(position).getBid());
        try
        {
            getProfileImage(holder.mView,holder.i,mStorageRef);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
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
                    //imageView = view.findViewById(R.id.imageView3);
                }

            }
        });
        if(pos == position)
        {
            //advertFrag.removeAdvert();
            holder.mView.setBackgroundColor(Color.GREEN);
        }
        else {
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
        public final TextView s;
        public final TextView w;
        public final ImageView i;
        //public final TextView s;
        //public final TextView s;

        public Advert mItem;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            //mBid = view;
            n = view.findViewById(R.id.advertName);
            c = (TextView) view.findViewById(R.id.collect);
            d = (TextView) view.findViewById(R.id.deliver);
            s= (TextView) view.findViewById(R.id.size);
            w= (TextView) view.findViewById(R.id.weight);
            i = (ImageView) view.findViewById(R.id.imageView3);
            //s = (TextView) view.findViewById(R.id.updateBid);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" +n.getText() +c.getText() + "'" + d.getText();
        }
    }

    public void updateBid(final View view, final ArrayList<String> advertKey, DatabaseReference reference, final HashMap<String, String> advertMap, User user)
    {
        //Trying to return name after bid is placed!
        listBid = new ArrayList<>();
        if(pos == -1)
        {
            Toast.makeText(view.getContext(), "Please select job", Toast.LENGTH_SHORT).show();
        }
        else
            {
            userKey = fUser.getUid();
            String id = reference.child(advertMap.get(String.valueOf(advertKey.get(pos)))).
                    child(String.valueOf(advertKey.get(pos))).child("bid").push().getKey();
            HashMap<Object, Object> result = new HashMap<>();
            TextView bidUser = view.findViewById(R.id.enterBid);
            //Getting the users wallet and getting the new bid
                //Subtracting the usersWallet from the new bid
                userWallet = Double.valueOf(user.getWallet().toString());
                newBid = Double.valueOf(bidUser.getText().toString());
                double d = userWallet - newBid;
                remainingWallet = String.valueOf(d);
                //Only lists the bid if user has enough in his wallet
            if(newBid > userWallet)
            {
                Toast.makeText(view.getContext(), "You have insufficient funds in your wallet!", Toast.LENGTH_SHORT).show();
            }
            else {
                result.put(userKey, bidUser.getText().toString());
                listBid.add(remainingWallet);
                updateWallet(view);
                reference.child(advertMap.get(String.valueOf(advertKey.get(pos)))).
                        child(String.valueOf(advertKey.get(pos))).child("bid").child(id).setValue(result);
                Log.d(TAG, "Bid Added:success");
                Toast.makeText(view.getContext(), "Bid Added!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void findUser()
    {

    }

    public void updateWallet(final View view)
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference reference = firebaseDatabase.getReference();

        //Only changes the wallet if the User has enough in his wallet
        // TODO: 24/04/2018 "Is this check redundant??"
        if(newBid > userWallet)
        {
            Toast.makeText(view.getContext(), "You have insufficient funds in your wallet!", Toast.LENGTH_SHORT).show();
        }
        else {
            reference.child("users").child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //User user = dataSnapshot.getValue(User.class);
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("wallet", remainingWallet);
                    reference.child("users").child(fUser.getUid()).updateChildren(result);
                    Toast.makeText(view.getContext(), "Your Remaining balance is: £" + remainingWallet, Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "UpdateDetails:Success");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Logger.error(TAG, ">>> Error:" + "find onCancelled:" + databaseError);
                }
            });
        }
    }

    public void getProfileImage(View view,ImageView i,StorageReference ref) throws IOException
    {

        Glide.with(view.getContext())
                .using(new FirebaseImageLoader())
                .load(ref)
                .into(i);

    }

    public static int getPosition()
    {
        return pos;
    }
    
}
