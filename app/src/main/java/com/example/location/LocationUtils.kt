package com.example.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.Priority.*
import com.google.android.gms.maps.model.LatLng
import java.util.Locale


//It is a Helper class that allows us to check whether we have a permission
//(whether the user/phone granted the permission) in a context
class LocationUtils(val context:Context)
{
    //check whether permission granted
    fun hasLocationPermission(context:Context):Boolean
    {
        return ContextCompat.checkSelfPermission(context ,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(context ,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED

    }


    //this allows to get the latitude and longitude of the user
    private val _fusedLocationClient: FusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(context)

    //this function will gives us the actual location and give it to location data then update the viewmodel
    @SuppressLint("MissingPermission")   //we say don't worry about missing permission we will take care of it ourselves
    fun requestLocationUpdates(viewModel: MainViewModel)
    {
        //this is just a call back it will never going to do anything unless we tell do it
        val locationCallback= object:LocationCallback()
        {
            //we can directly override inside the object without inheriting
            override fun onLocationResult(locationResult:LocationResult)
            {
                super.onLocationResult(locationResult)
                    locationResult.lastLocation?.let{
                        //unpack this location and give(feed) to the location data
                        val location=LocationData(Latitude=it.latitude,Longitude=it.longitude)
                        //update viewmodel
                        viewModel.updateLocation(location)    //location as new location
                    }
            }
        }
        //telling how to do that
        val locationRequest=LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build()
        _fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
        //to use the above feature we should have permission that we ask but we have managed ourself so we use @SuppressLint
    }

    //geo decoder that converts the location(in terms of latitude and longitude) to address(human readable)
    fun geoDecoder(location:LocationData):String
    {
        val geocoder=Geocoder(context, Locale.getDefault())  //Locale:how to write the address(Language)
        val coordinates=LatLng(location.Latitude,location.Longitude) //getting the coordinates
        val addresses:MutableList<Address>? =geocoder.getFromLocation(coordinates.latitude,coordinates.longitude,1)
        //can be multiple address that fit to a certain location ,i.e why mutable list
        return if(addresses?.isNotEmpty()==true)
        {
             addresses[0].getAddressLine(0)
            //give the first address inside of the address list
        }
        else{
            return "Address Not Found!"
        }
    }

}
