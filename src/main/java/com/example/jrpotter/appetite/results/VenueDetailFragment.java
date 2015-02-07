package com.example.jrpotter.appetite.results;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jrpotter.appetite.R;
import com.example.jrpotter.appetite.drawer.SubDrawerFragment;
import com.example.jrpotter.appetite.tool.Locu;
import com.example.jrpotter.appetite.tool.UserLocation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 *
 */
public class VenueDetailFragment extends SubDrawerFragment {

    private MapView mMapView;
    private Locu.Venue venue;


    // Public Methods
    // ==================================================

    public void setVenue(Locu.Venue venue) {
        this.venue = venue;
    }


    // Hook Methods
    // ==================================================

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_venue_detail, container, false);
        mMapView = (MapView) view.findViewById(R.id.fragment_venue_detail_map);
        mMapView.onCreate(savedInstanceState);

        // Initialize
        initializeMap(view);
        initializeHeader(view);
        initializeButtons(view);

        return view;
    }


    // Header Methods
    // ==================================================

    private void initializeMap(View view) {
        if(venue.location != null) {
            MapsInitializer.initialize(getActivity());
            mMapView.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(GoogleMap googleMap) {

                    // Current position and target position
                    LatLng current = UserLocation.getInstance(null).getLocation();
                    LatLng target = new LatLng(venue.location.latitude, venue.location.longitude);
                    LatLngBounds bounds = new LatLngBounds.Builder().include(current).include(target).build();

                    // Updating camera
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                    // Add Markers
                    googleMap.addMarker(new MarkerOptions().position(current).title("My Location"));
                    googleMap.addMarker(new MarkerOptions().position(target).title("Destination"));

                }
            });
        }
    }

    private void initializeHeader(View view) {

        // Set name
        if(venue.name != null) {
            TextView nameView = (TextView) view.findViewById(R.id.fragment_venue_detail_name);
            nameView.setText(venue.name);
        }

        // Set Address Line
        if(venue.location != null) {

            TextView addressLineView = (TextView) view.findViewById(R.id.fragment_venue_detail_address_line);
            TextView localityLineView = (TextView) view.findViewById(R.id.fragment_venue_detail_locality_line);

            // Set Address Line
            if(venue.location.address1 != null) {
                addressLineView.append(venue.location.address1);
                if(venue.location.address2 != null) {
                    addressLineView.append("\n" + venue.location.address2);
                }
                if(venue.location.address3 != null) {
                    addressLineView.append("\n" + venue.location.address3);
                }
            }

            // Set Locality Line
            if(venue.location.locality != null || venue.location.country != null || venue.location.postal_code != null) {
                if(venue.location.locality != null) {
                    localityLineView.append(venue.location.locality);
                    if(venue.location.country != null || venue.location.postal_code != null) {
                        localityLineView.append(", ");
                    }
                }
                if(venue.location.country != null) {
                    localityLineView.append(venue.location.country);
                    if(venue.location.postal_code != null) {
                        localityLineView.append(" ");
                    }
                }
                if(venue.location.postal_code != null) {
                    localityLineView.append(venue.location.postal_code);
                }
            }

        }

    }


    // Button Methods
    // ==================================================

    private void initializeButtons(View view) {

        Button dirView = (Button) view.findViewById(R.id.fragment_venue_detail_directions);
        Button phoneView = (Button) view.findViewById(R.id.fragment_venue_detail_phone);
        Button websiteView = (Button) view.findViewById(R.id.fragment_venue_detail_website);

        // Setup navigation via Google Maps
        if (venue.location != null) {
            dirView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Notify user a navigation activity is about to begin
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Navigate to Venue?").setTitle("Directions");
                    builder.setNegativeButton(R.string.cancel, null);

                    // Initiate navigation
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String daddr = String.valueOf(venue.location.latitude);
                            daddr += "," + String.valueOf(venue.location.longitude);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("http://maps.google.com/maps?daddr=" + daddr));
                            getActivity().startActivity(intent);
                        }
                    });

                    builder.create().show();
                }
            });
        } else {
            dirView.setVisibility(View.GONE);
        }

        // Allow navigation to phone
        if (venue.contact != null && venue.contact.phone != null) {
            phoneView.setText(venue.contact.phone);
            phoneView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Notify user a phone call is about to begin
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Call Venue?").setTitle("Phone");
                    builder.setNegativeButton(R.string.cancel, null);

                    // Initiate phone call
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + venue.contact.phone));
                            getActivity().startActivity(intent);
                        }
                    });

                    builder.create().show();
                }
            });
        } else {
            phoneView.setVisibility(View.GONE);
        }

        // Allow navigation to browser
        if(venue.website_url != null) {
            websiteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Notify user the browser is about to open
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Open URL?").setTitle("Website");
                    builder.setNegativeButton(R.string.cancel, null);

                    // Initiate browser redirect
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(venue.website_url));
                            getActivity().startActivity(intent);
                        }
                    });

                    builder.create().show();

                }
            });
        } else {
            websiteView.setVisibility(View.GONE);
        }
    }

}
