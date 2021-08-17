package com.KotlinNinja.bookhub.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionManager {
    fun checkConnectivity(context: Context):Boolean {
        val connectivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // this will give information about currently active internet(type casted)
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo //checking active network

        if(activeNetwork?.isConnected!= null){
            return activeNetwork.isConnected
        } else {
            return false
        } //isConnected method is the class of NetworkInfo
        // check
    }
}