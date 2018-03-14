package com.thiernombd.arduinobluetoothcommand;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Thierno_M_B_DIALLO on 08/03/2018.
 */

public class BluetoothConnectionAdapter extends RecyclerView.Adapter<BluetoothConnectionAdapter.MyViewHolder> {
    private final List<_BluetoothDevice> devicesObjectList;
    public boolean paired = false;


    public BluetoothConnectionAdapter(List<_BluetoothDevice> devicesObjectList, boolean paired){
        this.devicesObjectList = devicesObjectList;
        this.paired = paired;
    }
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.devices_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        _BluetoothDevice currentAlarmView = devicesObjectList.get(position);
        holder.display(currentAlarmView,position);

    }

    @Override
    public int getItemCount() {
        return devicesObjectList.size();
    }



    ////////////////////////////////////////////////////////////////////

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView deviceName_tv ;
        private final TextView deviceMAC_tv ;
        private final TextView deviceClass_tv ;
        private final RelativeLayout deviceLayout;

        private _BluetoothDevice currentDevice;
        private final View currentItemView;
        private int position;


        public MyViewHolder(final View itemView) {
            super(itemView);


            this.currentItemView = itemView;
            this.deviceName_tv = (TextView) itemView.findViewById(R.id.deviceName_tv);
            this.deviceMAC_tv = (TextView) itemView.findViewById(R.id.deviceMAC_tv);
            this.deviceClass_tv = (TextView) itemView.findViewById(R.id.deviceClass_tv);

            this.deviceLayout = (RelativeLayout) itemView.findViewById(R.id.device_layout);

            deviceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.bSocket = null;
                    Toast.makeText(itemView.getContext(), "Connexion avec "+currentDevice.deviceName+"...", Toast.LENGTH_LONG).show();
                    BluetoothConnectionActivity.mBluetoothAdapter.cancelDiscovery();
                    BluetoothClient clientBluetooth = new BluetoothClient(currentDevice.device);
                    clientBluetooth.start();
                    if(MainActivity.bSocket == null) {
                        Toast.makeText(v.getContext(), "La connexion a echoué", Toast.LENGTH_SHORT).show();
                        clientBluetooth.cancel();
                    }
                }

            });
        }

        public void display(_BluetoothDevice currentAlarmView, int position) {
            this.currentDevice = currentAlarmView;
            this.position = position;
            deviceName_tv.setText(currentAlarmView.deviceName);
            deviceMAC_tv.setText(currentAlarmView.MAC);
            deviceClass_tv.setText(currentAlarmView.className);
        }
    }

    ///////////////////////////////////////////////////////////://///////

    public void removeDeviceIn_rv(int position){
        devicesObjectList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position,devicesObjectList.size());
    }

    public void addDeviceIn_rv(int position, _BluetoothDevice device){
        devicesObjectList.add(position,device);
        notifyItemInserted(position);
        notifyItemRangeRemoved(position, devicesObjectList.size());
    }

}
