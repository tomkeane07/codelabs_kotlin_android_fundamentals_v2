/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.overview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import javax.security.auth.callback.Callback

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()

    // The external immutable LiveData for the response String
    val response: LiveData<String>
        get() = _response

    // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
    // with new values
    private val _property = MutableLiveData<MarsProperty>()
    // The external LiveData interface to the property is immutable,
    //so only this class can modify
    val property: LiveData<MarsProperty>
        get() = _property

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main
    )

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties()
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties() {
        coroutineScope.launch{

            var getPropertiesDeferred = MarsApi.retrofitService.getProperties()
            try {
                // Await the completion of our Retrofit request
                var listResult = getPropertiesDeferred.await()
                _response.value = "Success: ${listResult.size} Mars properties retrieved"
                Log.d("imgSrcUrl", "aaa" )
                if(listResult.isNotEmpty()){
                    _property.value = listResult[0]
                    Log.d("imgSrcUrl", property.value?.imgSrcUrl )
                    Log.d("imgSrcUrl", "aaa" )

                }
            }catch(e: Exception){
                _response.value = "Failure: ${e.message}"
            }
        }
    }
    override fun onCleared(){
        super.onCleared()
        viewModelJob.cancel()
    }
}
