package jp.ac.ncc.ae2002.mygreatgooglemap;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 位置情報の取得開始
        MapsActivityPermissionsDispatcher.startLocationWithPermissionCheck(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // 位置情報の取得終了
        endLocation();
    }


    // パーミッションのリクエスト結果が戻されるメソッド
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // PermissionsDispatcherによって自動生成されるクラスにパーミッション管理を委譲する
        MapsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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

        // Add a marker in Mt.Fuji and move the camera
        LatLng NCC = new LatLng(37.923194, 139.044655);
        mMap.addMarker(new MarkerOptions().position(NCC).title("Marker in NCC"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NCC,  15));
    }

    //位置情報取得処理をスタート
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void startLocation() {
        if(locationManager == null){
            locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,10,this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,10,this);
        }else{
            Log.d("LOCATION","位置情報の使用が許可されていません");
        }

    }

    //位置情報取得処理を終了
    void endLocation() {
        locationManager.removeUpdates(this);
    }
    // 許諾されなかった時の処理
    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void onLocationDenied() {
        Toast toast = Toast.makeText(this,
                "本アプリでは位置情報を利用するので許可をお願いします",
                Toast.LENGTH_LONG);
        toast.show();
    }


    // 許諾が必要な根拠を表示する処理
    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForLocation(final PermissionRequest request) {

    }

    // 二度と表示しないを選択されてしまったときの処理
    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void onLocationNeverAskAgain() {
        Toast toast = Toast.makeText(this,
                "本アプリでは位置情報を利用するので許可をお願いします",
                Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude =location.getLatitude();
        double longitude= location.getLongitude();
        Log.d("LOCATION","onLocationChanged 緯度:" + latitude + " 経度:" + longitude);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}