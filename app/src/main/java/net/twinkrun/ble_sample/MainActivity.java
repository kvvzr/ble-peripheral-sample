package net.twinkrun.ble_sample;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 25252;
    private final static int REQUEST_COARSE_LOCATION = 83025;

    private BluetoothAdapter mBleAdapter;
    private BluetoothLeAdvertiser mBleAdvertiser;

    private String mDeviceName;
    private String mUuidString;

    @OnClick(R.id.sugoi_button)
    public void onClicked() {
        setupBle("hello", getString(R.string.advertise_uuid));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    private void setupBle(String deviceName, String uuidString) {
        mDeviceName = deviceName;
        mUuidString = uuidString;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
        } else {
            setupAdvertise();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (REQUEST_COARSE_LOCATION == requestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupAdvertise();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && mBleAdapter != null && mBleAdapter.isEnabled()) {
            startAdvertise();
        }
    }

    private void setupAdvertise() {
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBleAdapter = manager.getAdapter();

        if (mBleAdapter == null || !mBleAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        } else {
            startAdvertise();
        }
    }

    private void startAdvertise() {
        mBleAdapter.setName(mDeviceName);

        mBleAdvertiser = mBleAdapter.getBluetoothLeAdvertiser();
        mBleAdvertiser.startAdvertising(makeAdvertiseSetting(), makeAdvertiseData(mUuidString), new EmptyAdvertiseCallback());
    }

    private static AdvertiseSettings makeAdvertiseSetting() {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        builder.setConnectable(false);
        return builder.build();
    }

    private static AdvertiseData makeAdvertiseData(String uuidString) {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.addServiceUuid(new ParcelUuid(UUID.fromString(uuidString)));
        builder.setIncludeDeviceName(true);
        return builder.build();
    }

    private static final class EmptyAdvertiseCallback extends AdvertiseCallback {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
        }
    }
}
