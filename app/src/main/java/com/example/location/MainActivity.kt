package com.example.location

import android.content.Context
import android.os.Bundle
import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.location.ui.theme.LocationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel:MainViewModel =viewModel()
            LocationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                         App(viewModel)
                }
            }
        }
    }
}


@Composable
fun App(viewModel:MainViewModel)
{
    val context=LocalContext.current
    val locationUtils=LocationUtils(context)
    Display(locationUtils,context, viewModel)
}

@Composable
fun Display(locationUtils:LocationUtils,context:Context,viewModel: MainViewModel) {
    //permission launcher
    val permissionLauncher=rememberLauncherForActivityResult(contract=ActivityResultContracts.RequestMultiplePermissions(),
        onResult ={permissions->  //for every permission requested
            if(permissions[Manifest.permission.ACCESS_FINE_LOCATION]==true && permissions[Manifest.permission.ACCESS_COARSE_LOCATION]==true)
            {
                //Access
                locationUtils.requestLocationUpdates(viewModel)
            }
            else
            {
                //Ask for permission and let the user know why we want the location
                //we should show the permission rationale or not (the reason to access the permission) along with the launcher
                val rationaleRequired=ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity     //context should be inside the MainActivity
                    ,Manifest.permission.ACCESS_FINE_LOCATION)
                        ||
                        ActivityCompat.shouldShowRequestPermissionRationale(
                    context
                    ,Manifest.permission.ACCESS_COARSE_LOCATION)
                if(rationaleRequired)
                {
                    Toast.makeText(context,"Location is required to find you",Toast.LENGTH_LONG).show()
                }
                //when user ask every time and click on don't allow every time(x2),
                // when user do not use ask every time and click in don't allow once
                else
                {
                    Toast.makeText(context,"Location is required ,Enable it in settings",Toast.LENGTH_LONG).show()
                }
                //why we want to have the permission(reason),when user ask every time and click on don't allow for first time

            }

        } )
    
    
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center)
    {
        Text(text = "Where Are You?", fontSize = 30.sp, modifier=Modifier.padding(10.dp))
        //will display the location if i have a permission
        Button(onClick = {
            if(locationUtils.hasLocationPermission(context))
            {
                Toast.makeText(context,"You Have Allowed Permission Already",Toast.LENGTH_LONG).show()//can use enable parameter in button
                locationUtils.requestLocationUpdates(viewModel)
            //use and update location
            }
        else
        { //request permission with launcher
          permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION))
        //we have to pass the permissions so that it can check for that multiple permissions
        }})
        {
            Text(text = "Find Me!", fontSize = 20.sp, fontWeight= FontWeight.ExtraBold)
        }

        val loc=viewModel.location.value
        //getting the address by unpacking loc
        val address=loc?.let {
            locationUtils.geoDecoder(loc)  //geoDecoder takes location of Location Data Type i.e why unpacking is required
        }

        if(loc!=null)
        {
            Text("Latitude:${loc.Latitude}  Longitude:${loc.Longitude}" ,fontSize =17.sp ,fontWeight=FontWeight.ExtraBold ,fontFamily= FontFamily.Serif ,modifier=Modifier.padding(top=50.dp))
            Text(text ="Address:\n${address}" ,textAlign= TextAlign.Center ,fontSize =20.sp ,fontFamily=FontFamily.Serif,modifier=Modifier.padding(15.dp))
        }
    }
}
