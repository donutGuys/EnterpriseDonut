package uk.ac.tees.q5113445live.enterpriseproject2;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TestFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String DRIVER_BOOLEAN = "driverCheck";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private DatabaseReference mDatabase;
    private DatabaseReference userDatabase;
    private FirebaseUser user;
    private DatabaseReference bidDatabase;
    private boolean driverCheck;
    private ArrayList<String> location;
    private ArrayList<String> bid;
    private FirebaseUser fUser;
    private HashMap<String, String> advertMap;
    private String userBidOn;
    private ArrayList advertKey;
    private OnListFragmentInteractionListener mListener;
    public static final List<Advert> ITEMS = new ArrayList<Advert>();
    public static final Map<String, Advert> ITEM_MAP = new HashMap<String, Advert>();
    private Button updateBid;
    private RecyclerView recyclerView;
    private MyItemRecyclerViewAdapter recycleAdapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TestFragment()
    {

    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TestFragment newInstance(int columnCount, boolean d)
    {
        TestFragment fragment = new TestFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        mDatabase = FirebaseDatabase.getInstance().getReference("advert");
        location = new ArrayList<>();
        advertMap = new HashMap<>();
        advertKey = new ArrayList();

        refresh();
        if (getArguments() != null)
        {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            driverCheck = getArguments().getBoolean(DRIVER_BOOLEAN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (mListener != null)
        {
            mListener.onListFragmentInteraction("View Adverts");
        }
        final View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        checkDriver(view);
        updateBid = view.findViewById(R.id.updateBid);
        updateBid.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view2)
            {
                recycleAdapter.updateBid(view, advertKey,mDatabase, advertMap);
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener
    {
        //TODO: make it so that the card expands when clicking on it
        void onListFragmentInteraction(String title);
    }



    private static void addItem(Advert item)
    {
        //Adds the items to a static list which is shown to the user
        ITEMS.add(item);
        ITEM_MAP.put(item.getName(),item);
        ITEM_MAP.put(item.getFrom(),item);
        ITEM_MAP.put(item.getTo(),item);
        ITEM_MAP.put(item.getDeliveryType(), item);
        ITEM_MAP.put(item.getSize(),item);
        ITEM_MAP.put(item.getWeight(),item);
    }

    private void recyclerMethod(View view)
    {
        //Recyclers which handles the showing of items to the user.
        if (view.findViewById(R.id.list) instanceof RecyclerView)
        {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1)
            {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }
            else
            {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recycleAdapter = new MyItemRecyclerViewAdapter(ITEMS, mListener);
            recyclerView.setAdapter(recycleAdapter);

        }
    }
    private void refresh()
    {
        ITEMS.clear();
        ITEM_MAP.clear();
    }
    public void checkDriver(final View view)
    {
        mDatabase.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                //Add each advert to a list.
                if(driverCheck)
                {
                    for(DataSnapshot child : dataSnapshot.getChildren())
                    {
                        if(user.getUid().equals(dataSnapshot.getKey()))
                        {
                        }
                        else
                        {
                            Advert advert = child.getValue(Advert.class);
//                            location = getLocation(advert.from,advert.to);
//                            advert.setFrom(location.get(0));
//                            advert.setTo(location.get(1));
                            addItem(advert);
                            recyclerMethod(view);
                        }

                    }
                }
                else
                {
                    for(DataSnapshot child : dataSnapshot.getChildren())
                    {
                        System.out.println(user.getUid());
                        if(user.getUid().equals(dataSnapshot.getKey()))
                        {
                            Advert advert = child.getValue(Advert.class);
                            addItem(advert);
                            recyclerMethod(view);
                        }
                    }
                }
                for (DataSnapshot q : dataSnapshot.getChildren())
                {
                    if(!advertKey.contains(q.getKey()))
                    {
                        advertKey.add(q.getKey());
                    }
                    if (!q.getKey().equals(user.getUid()))
                    {
                        userBidOn = dataSnapshot.getKey();
//                        for (DataSnapshot advertSnapshot: q.getChildren())
//                        {
                            advertMap.put( q.getKey().toString(),userBidOn);
//                        }
                    }
                    //userData = dataSnapshot;
                    // bidText(advert,view);
                }

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                //Initialises user to stored data and populates TextViews in layout.Advert advert = userData.getValue(Advert.class);
                //Creates separate section for bids
//                for (DataSnapshot userSnapshot : dataSnapshot.getChildren())
//                {
//                    if (!userSnapshot.getKey().equals(fUser.getUid()))
//                    {
//                        userBidOn = userSnapshot.getKey();
//                        for (DataSnapshot advertSnapshot: userSnapshot.getChildren())
//                        {
//                            advertMap.put( advertSnapshot.getKey().toString(),userBidOn);
//                        }
//                    }
//                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }



    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onListFragmentInteraction("View Adverts");
        }
    }


    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(String title);
    }
//    public void getDriver()
//    {
//        ValueEventListener userListener = new ValueEventListener()
//        {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot)
//            {
//                User user = dataSnapshot.getValue(User.class);
//                driver = user.isDriver();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        userDatabase.addValueEventListener(userListener);
//    }
    public ArrayList<String> getLocation(String from, String to)
    {
        ArrayList<String> location = new ArrayList<>();

        String cityFrom = "";
        String cityTo = "";
        Geocoder gps = new Geocoder(getActivity(), Locale.getDefault());
        if (gps.isPresent()) {
            try {
                List<Address> list = gps.getFromLocationName(from, 1);
                Address address = list.get(0);
                double lat = address.getLatitude();
                double lng = address.getLongitude();
                cityFrom = address.getLocality();

                list = gps.getFromLocationName(to, 1);
                address = list.get(0);
                lat = address.getLatitude();
                lng = address.getLongitude();
                cityTo = address.getLocality();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        location.add(cityFrom);
        location.add(cityTo);

        return location;
    }

}
