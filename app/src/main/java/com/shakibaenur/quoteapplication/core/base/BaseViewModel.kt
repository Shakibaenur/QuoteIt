package com.shakibaenur.quoteapplication.core.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.shakibaenur.quoteapplication.data.repository.AppRepository
import com.shakibaenur.quoteapplication.utils.ApiResponseCode
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import javax.inject.Inject

/**
 * Created by Shakiba E Nur on 03,March,2023
 */
open class BaseViewModel :
    ViewModel() {

    protected val _showLoader = MutableLiveData(false)
    val showLoader: LiveData<Boolean> = _showLoader

    protected val _showMessage = MutableLiveData<String>()
    val showMessage: LiveData<String> = _showMessage

    protected val _isUnAuthorized = MutableLiveData(false)
    val isUnAuthorized: LiveData<Boolean> = _isUnAuthorized
    @Inject
    open lateinit var repository: AppRepository

    suspend fun <T> callService(api: suspend () -> T): T? {
        try {
            _isUnAuthorized.value = false
            val response = api.invoke()
            if (response is Response<*>) {
                if (response.code() == ApiResponseCode.ERROR) {
//                    val errorResponse: ErrorResponse =
//                        Gson().fromJson(
//                            response.errorBody()?.charStream(),
//                            ErrorResponse::class.java
//                        )
                    _isUnAuthorized.value = true
                   // _showMessage.value = errorResponse.message
                } else if (!response.isSuccessful) {
                  //  _showMessage.value = errorResponse.message
                }
                return response
            }
            return response
        } catch (e: HttpException) {
            handleErrorResponse(e)
            e.printStackTrace()
        } catch (ex: ConnectException) {
            _showMessage.value = "No Internet Connection!"
            ex.printStackTrace()
        } catch (ex: Exception) {
            //_showMessage.value = "Something went wrong! Please try again later"
            ex.printStackTrace()
        }
        return null
    }


    private fun handleErrorResponse(e: HttpException) {
        if (e.response()?.code() == 401) {
            _isUnAuthorized.value = true
            _showMessage.value = "Error"
        } else {
            _showMessage.value = "Error"
        }
    }
}