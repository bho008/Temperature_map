package com.example.brian.temperature_map;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
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
import java.lang.*;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    final static LatLng temp_probe1 = new LatLng(33.9756, -117.3313);

    private TextView tv;

    double tempF;
    double lat = 33.9756;
    double lng = -117.3313;
    double olat = lat;
    double olng = lng;
    Marker TEMP_PROBE1;

    LatLng temp_probe_pos = new LatLng(lat, lng);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        tempF = 0.00;
        UDPListenerService UDPListener = new UDPListenerService();
        UDPListener.startListenForUDPBroadcast();
        //UDPListener.UDPBroadcastThread.run();UDPBroadcastThread
        //new Thread(new UDPListener.UDPBroadcastThread()).start();
        /*
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) { }
        */
        //mHandler = new Handler();
        //startRepeatingTask();
        updateTempMarker.run();
    }

    boolean doneTemp = false;

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


         TEMP_PROBE1 = mMap.addMarker(new MarkerOptions()
                .title("Temperature")
                .snippet(Double.toString(tempF))
                .position(temp_probe1)
                .draggable(true));

        TEMP_PROBE1.showInfoWindow();
        System.out.println("created temp_probe1 marker");
        doneTemp = true;
    }


     Handler mHandler = new Handler();

    private Runnable updateTempMarker = new Runnable(){
        public void run(){
            if(doneTemp) {
                TEMP_PROBE1.setSnippet("TempF: " + Double.toString(tempF));//+" \n"+"Kelvin: " + Double.toString(tempF+273.15));
                System.out.println("updating tempMarker");
                TEMP_PROBE1.showInfoWindow();

                if(lat == 0.0){
                    temp_probe_pos = new LatLng(olat, olng);

                }
                else temp_probe_pos = new LatLng(lat, lng);
                TEMP_PROBE1.setPosition(temp_probe_pos);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp_probe_pos, 18));


            }

            mHandler.postDelayed(updateTempMarker, 1000);
        }
    };
    //mHandler.post(updateTempMarker);


    private static final int UDP_SERVER_PORT = 11000;

    DatagramPacket UDPPacket;
    DatagramSocket Socket;


    Runnable Server = new  Runnable() {

        //bool done = false;
        @Override
        public void run() {

            System.out.println("HELLO");
            try{
                InetAddress broadcastIP = InetAddress.getByName("192.168.8.100");
                Socket = new DatagramSocket(UDP_SERVER_PORT, broadcastIP);

            }
            catch(Exception e){
                System.out.println("Socket Bind error");
            }
            try {
                //InetAddress serverAddr = InetAddress.getByName("192.168.8.100");
                System.out.println("\nServer: Start connecting\n");
                byte[] buf = new byte[32];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                DatagramSocket socket = new DatagramSocket(UDP_SERVER_PORT);

                System.out.println("Server: Receiving\n");
                socket.receive(packet);

                System.out.println("Server: Message received: ‘" + new String(packet.getData()) + "’\n");
                System.out.println("Server: Succeed!\n");
            } catch (Exception e) {
                System.out.println("Server: Error!\n");
            }
           // updateStatus();
            mHandler.postDelayed(Server, 100);
        }

    };

    void startRepeatingTask(){
        Server.run();

    }

    public class UDPServer implements Runnable {

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
                System.out.println("Server: Message received: ‘" + new String(packet.getData()) + ":" + "’\n");
                System.out.println("Server: Succeed!\n");
            } catch (Exception e) {
                System.out.println("Server: Error!\n");
            }
        }
    };


    //=================================================

    public class UDPListenerService extends Service {
        //static String UDP_BROADCAST = "UDPBroadcast";

        //Boolean shouldListenForUDPBroadcast = false;
        DatagramSocket socket;

        private void listenAndWaitAndThrowIntent(InetAddress broadcastIP, Integer port) throws Exception {
            byte[] recvBuf = new byte[32];
            if (socket == null || socket.isClosed()) {
                socket = new DatagramSocket(port, broadcastIP);
                socket.setBroadcast(true);
            }
            //socket.setSoTimeout(1000);
            DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
            Log.e("UDP", "Waiting for UDP broadcast");
            System.out.println("udp waiting for broadcast");
            socket.receive(packet);

            String senderIP = packet.getAddress().getHostAddress();
            String message = new String(packet.getData()).trim();
            parseInput(recvBuf);
            System.out.print("temperature in F: ");
            System.out.println(tempF);
            Log.e("UDP", "Got UDB broadcast from " + senderIP + ", message: " + message);

            broadcastIntent(senderIP, message);
            socket.close();
        }

        private void broadcastIntent(String senderIP, String message) {
            //Intent intent = new Intent(UDPListenerService.UDP_BROADCAST);
            //intent.putExtra("sender", senderIP);
           // intent.putExtra("message", message);
            //sendBroadcast(intent);
        }

        Thread UDPBroadcastThread;

        void startListenForUDPBroadcast() {
            UDPBroadcastThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        InetAddress broadcastIP = InetAddress.getByName("192.168.8.255"); //172.16.238.42 //192.168.1.255
                        Integer port = 11000;
                        while (shouldRestartSocketListen) {
                            listenAndWaitAndThrowIntent(broadcastIP, port);
                        }
                        //if (!shouldListenForUDPBroadcast) throw new ThreadDeath();
                    } catch (Exception e) {
                        Log.i("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
                        System.out.println("UDP no longer listening for udp broadcasts");
                        run();
                    }
                }
            });
            UDPBroadcastThread.start();
        }

        private Boolean shouldRestartSocketListen=true;

        void stopListen() {
            shouldRestartSocketListen = false;
            socket.close();
        }

        @Override
        public void onCreate() {

        };

        @Override
        public void onDestroy() {
            stopListen();
        }


        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            shouldRestartSocketListen = true;
            startListenForUDPBroadcast();
            Log.i("UDP", "Service started");
            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        public void parseInput(byte[] input){

            int currPos = 0;
            char[] tempBuff = new char[6];
            for(int i = 0; i < 3; i++){
                if(input[i] == '='){
                    System.out.println("found = !!!!");
                }
            }

            for(int i = 3; i < 9; i++){
                if(input[i]==',')
                    break;
                tempBuff[i-3] = (char)input[i];
                currPos = i;
            }
            currPos+=2;
            char[] latBuff = new char[15];
            System.out.print("lat: ");
            int counter = 0;
            for(int i = currPos; i < 22; i++) {

                if (input[i] == ',')
                    break;
                latBuff[counter] = (char)input[i];
                currPos = i;
                counter++;
                System.out.print(latBuff[i-currPos]);
            }
            currPos+=2;

            char[] lngBuff = new char[15];
            counter = 0;
            System.out.print("lng: ");
            for(int i = currPos; i < 32; i++) {
                System.out.print((char)input[i]);
                if (input[i] == ';')
                    break;
                lngBuff[counter] = (char)input[i];
                currPos = i;
                counter++;
                System.out.print(lngBuff[i-currPos]);

            }

            System.out.println();
            //boolean isDigit = false;
            char[] tempBuff1 = new char[6];
            System.out.print("tempBuff: ");
            for(int i = 0; i < 6; i++){
                System.out.print(tempBuff[i]);
                if(Character.isDigit(tempBuff[i])){
                    System.out.print("is digit");
                    tempBuff1[i]= tempBuff[i];
                }
                if(tempBuff[i]=='.')
                    tempBuff1[i]= tempBuff[i];
            }
            System.out.println();

            char[] latBuff1 = new char[15];
            System.out.print("latBuff: ");
            for(int i = 0; i < latBuff.length; i++){
                System.out.print(latBuff[i]);
                if(Character.isDigit(latBuff[i])){
                    System.out.print("is digit");
                    latBuff1[i]= latBuff[i];
                }
                if(latBuff[i]=='.')
                    latBuff1[i]= latBuff[i];
            }
            System.out.println();

            char[] lngBuff1 = new char[15];
            System.out.print("lngBuff: ");
            for(int i = 0; i < lngBuff.length; i++){
                System.out.print(lngBuff[i]);
                if(Character.isDigit(lngBuff[i])){
                    System.out.print("is digit");
                    lngBuff1[i]= lngBuff[i];
                }
                if(lngBuff[i] == '-')
                    lngBuff1[i] = lngBuff[i];
                if(lngBuff[i]=='.')
                    lngBuff1[i]= lngBuff[i];
            }

            StringBuilder sb = new StringBuilder();
            sb.append(tempBuff1);
            tempF = Double.parseDouble(sb.toString());
            System.out.print("tempF: ");
            System.out.println(tempF);

            StringBuilder sb1 = new StringBuilder();
            sb1.append(latBuff1);
            lat = Double.parseDouble(sb1.toString());
            System.out.print("lat: ");
            System.out.println(lat);

            StringBuilder sb2 = new StringBuilder();
            sb2.append(lngBuff1);
            lng = Double.parseDouble(sb2.toString());
            System.out.print("lng: ");
            System.out.println(lng);
            //TEMP_PROBE1.setSnippet(Double.toString(tempF));

        }
    }


}
