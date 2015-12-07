package com.example.brian.temperature_map;

import android.content.Intent;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    static final LatLng temp_probe1 = new LatLng(33.9756, -117.3313);

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //runUdpClient();
        //finish();

        new Thread(new Server()).start();
        /*
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) { }
        */

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in UCR and move the camera
        LatLng UCR_marker = new LatLng(33.9756, -117.3311);
        mMap.addMarker(new MarkerOptions().position(UCR_marker).title("Marker in UCR"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UCR_marker, 18));
        //mMap.moveCamera(CameraUpdateFactory.zoomBy(10, UCR_marker));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(, 10));



        Marker TEMP_PROBE1 = mMap.addMarker(new MarkerOptions()
                .title("draggable marker")
                .snippet("HELLO WORLD")
                .position(temp_probe1)
                .draggable(true));

        TEMP_PROBE1.showInfoWindow();
        System.out.println("created temp_probe1 marker");
    }
    private static final int UDP_SERVER_PORT = 11000;

    public class Server implements Runnable {

        @Override
        public void run() {
            System.out.println("HELLO");
            try {
                InetAddress serverAddr = InetAddress.getByName("192.168.8.100");
                System.out.println("\nServer: Start connecting\n");
                DatagramSocket socket = new DatagramSocket(UDP_SERVER_PORT);
                byte[] buf = new byte[32];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                System.out.println("Server: Receiving\n");
                socket.receive(packet);
                System.out.println("Server: Message received: ‘" + new String(packet.getData()) + "’\n");
                System.out.println("Server: Succeed!\n");
            } catch (Exception e) {
                System.out.println("Server: Error!\n");
            }
        }
    };

}
/*

 class MyDatagramReceiver extends Thread {
    private boolean bKeepRunning = true;
    private String lastMessage = "";

    public void run() {
        String message;
        byte[] lmessage = new byte[MAX_UDP_DATAGRAM_LEN];
        DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);

        try {
            DatagramSocket socket = new DatagramSocket(UDP_SERVER_PORT);

            while(bKeepRunning) {
                socket.receive(packet);
                message = new String(lmessage, 0, packet.getLength());
                lastMessage = message;
                runOnUiThread(updateTextMessage);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (socket != null) {
            socket.close();
        }
    }

    public void kill() {
        bKeepRunning = false;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
*/