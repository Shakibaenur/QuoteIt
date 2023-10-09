package com.shakibaenur.quoteapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakibaenur.quoteapplication.data.model.Quote
import com.shakibaenur.quoteapplication.data.service.FirebaseService
import com.shakibaenur.quoteapplication.utils.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Shakiba E Nur on 04,March,2023
 */
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {


    private val _quotesResponse = MutableLiveData<List<Quote>>()
    val quotesResponse: LiveData<List<Quote>> =
        _quotesResponse

    fun getQuotes() {
        viewModelScope.launch {
            val firestoreHelper = FirebaseService(AppConstant.COLLECTION_QUOTES, Quote::class.java)
            _quotesResponse.value = firestoreHelper.getAll()
        }
    }

    fun searchQuote(search: String?) {
        val firestoreHelper = FirebaseService(AppConstant.COLLECTION_QUOTES, Quote::class.java)
        if (search?.isEmpty() == true) {
            getQuotes()
        } else {
            viewModelScope.launch {
                _quotesResponse.value=firestoreHelper.getByFieldId(search!!)
            }
        }
    }

}