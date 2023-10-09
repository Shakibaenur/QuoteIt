package com.shakibaenur.quoteapplication.data.repository

import com.shakibaenur.quoteapplication.data.service.NetworkService
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Shakiba E Nur on 03,March,2023
 */
class AppRepositoryImpl @Inject constructor(
    private val service: NetworkService
) : AppRepository {



}