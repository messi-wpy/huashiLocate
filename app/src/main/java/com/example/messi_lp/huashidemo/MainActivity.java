package com.example.messi_lp.huashidemo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class MainActivity extends Check implements PoiSearch.OnPoiSearchListener, AMap.OnMyLocationChangeListener, RouteSearch.OnRouteSearchListener {

    private MapView mMapView;
    private AMap aMap;
    private AMapLocationClient client;
    private AMapLocationListener listener;
    private AMapLocationClientOption option=new AMapLocationClientOption();
    private final static String TAG="GAODE";
    private LatLonPoint frome;
    private LatLonPoint to;
    private RouteSearch routeSearch;
    private WalkRouteOverlay walkRouteOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* listener=new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation!=null){
                    int a=aMapLocation.getErrorCode();
                    if (aMapLocation.getErrorCode()==0){
                        LatLng latLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("me").snippet("DefaultMarker"));
                        aMap. moveCamera(CameraUpdateFactory.changeLatLng(latLng));
                    }else {
                        Log.d(TAG, "onLocationChanged: fail"+aMapLocation.getErrorCode());
                    }
                }
            }
        };
        client=new AMapLocationClient(getApplicationContext());
        client.setLocationListener(listener);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setInterval(2000);
        client.setLocationOption(option);
        client.startLocation();
*/

        mMapView =  findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        aMap=mMapView.getMap();
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);
        aMap.setOnMyLocationChangeListener(this);
        searchHelp("华中师范大学九号楼");


        routeSearch=new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });



    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    public void searchHelp(String locate){
        PoiSearch.Query query=new PoiSearch.Query(locate,"","武汉");
        PoiSearch poiSearch=new PoiSearch(this,query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        Log.d(TAG, "onPoiSearched-result code: "+i);
        LatLonPoint latLonPoint=poiResult.getPois().get(0).getLatLonPoint();
        to=latLonPoint;
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        LatLng p=new LatLng(latLonPoint.getLatitude(),latLonPoint.getLongitude());
        aMap. moveCamera(CameraUpdateFactory.changeLatLng(p));
        Marker marker=aMap.addMarker(new MarkerOptions().position(p).title("huashi"));
        if (frome!=null){
            RouteSearch.WalkRouteQuery query=new RouteSearch.WalkRouteQuery(new RouteSearch.FromAndTo(frome,to));
            routeSearch.calculateWalkRouteAsyn(query);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onMyLocationChange(Location location) {
        frome=new LatLonPoint(location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        aMap.clear();
        if (i== AMapException.CODE_AMAP_SUCCESS){
            if (walkRouteResult!=null&&walkRouteResult.getPaths()!=null){
                WalkPath walkPath=walkRouteResult.getPaths().get(0);
                if (walkRouteOverlay!=null){
                    walkRouteOverlay.removeFromMap();
                }
                walkRouteOverlay=new WalkRouteOverlay(this,aMap,walkPath,
                        walkRouteResult.getStartPos(),walkRouteResult.getTargetPos());
                LatLonPoint l=walkRouteOverlay.getLastWalkPoint(walkPath.getSteps().get(0));
                Log.d(TAG, "onWalkRouteSearched: "+l.toString());

                walkRouteOverlay.addToMap();
                walkRouteOverlay.zoomToSpan();
            }
        }

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
}
