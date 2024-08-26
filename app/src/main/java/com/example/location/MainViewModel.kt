package com.example.location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class MainViewModel: ViewModel()
{
    private val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> =_location

    fun updateLocation(newLocation:LocationData)
    {
        _location.value=newLocation
    }
//view model and these variable can update the latitude and longitude in the form of value(having latitude and longitude) because these are state
//they give the data in the packed form
}