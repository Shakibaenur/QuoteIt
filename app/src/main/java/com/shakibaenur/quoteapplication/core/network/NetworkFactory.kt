package com.shakibaenur.quoteapplication.core.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.shakibaenur.quoteapplication.R
import com.shakibaenur.quoteapplication.utils.AppConstant
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.shakibaenur.quoteapplication.BuildConfig
import com.shakibaenur.quoteapplication.core.network.NetworkFactory.BASE_URL
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object NetworkFactory {
    const val BASE_URL = ""
    const val TIME_OUT = 180L

    private lateinit var retrofit: Retrofit
    private lateinit var okHttpClient: OkHttpClient
    private val gson by lazy { GsonBuilder().create() }

    /**
     * @return new service class instance with base url and the converter factories
     */
    fun <Service> createService(
        context: Context,
        serviceClass: Class<Service>,
        timeOut: Long = TIME_OUT,
        enableTokenInterceptor: Boolean = true
    ): Service {
        return getRetrofit(
            context,
            timeOut = timeOut,
            enableTokenInterceptor = enableTokenInterceptor
        ).create(serviceClass)
    }



    /**
     * @return new retrofit instance with the base url and the converter factories
     */
    fun getRetrofit(
        context: Context,
        timeOut: Long = TIME_OUT,
        enableTokenInterceptor: Boolean = true
    ): Retrofit {

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(getOkHttpClient(context, timeOut, enableTokenInterceptor))
            .build()

        return retrofit
    }



    /**
     * Creates OkHttpClient and add interceptors
     */
    private fun getOkHttpClient(
        context: Context,
        timeOut: Long = TIME_OUT,
        enableTokenInterceptor: Boolean = true
    ): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()
            .readTimeout(timeOut, TimeUnit.SECONDS)
            .writeTimeout(timeOut, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpBuilder.addInterceptor(
                LoggingInterceptor.Builder()
                    .loggable(BuildConfig.DEBUG)
                    .setLevel(Level.BASIC)
                    .log(Platform.INFO)
                    .tag(context.getString(R.string.app_name))
                    .request("Request")
                    .response("Response")
                    .executor(Executors.newSingleThreadExecutor())
                    .build()
            )
        }
        if (enableTokenInterceptor) {
            val token = getToken(context)
            okHttpBuilder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                   // .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)

            }
        }
        okHttpClient = okHttpBuilder.build()

        return okHttpClient
    }

    private fun getToken(context: Context): String {
//        val sharedPref =
//            context.getSharedPreferences(AppConstant.PREFERENCE_NAME, Context.MODE_PRIVATE)
//
//        return sharedPref?.getString(AppConstant.AUTH_TOKEN, "") ?: ""
        return ""
    }

    private fun getUnsafeClient(
        context: Context,
        timeOut: Long = TIME_OUT,
        enableTokenInterceptor: Boolean = true
    ): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        val interceptorBody = HttpLoggingInterceptor()
        interceptorBody.level = HttpLoggingInterceptor.Level.BODY

        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        val builder = OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .readTimeout(timeOut, TimeUnit.SECONDS)
            .writeTimeout(timeOut, TimeUnit.SECONDS)
            .addInterceptor(
                LoggingInterceptor.Builder()
                    .loggable(BuildConfig.DEBUG)
                    .setLevel(Level.BASIC)
                    .log(Platform.INFO)
                    .request("Request")
                    .response("Response")
                    .executor(Executors.newSingleThreadExecutor())
                    .build()
            )
        if (enableTokenInterceptor)
            builder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("AuthenticationToken", getToken(context))
                    .build()
                chain.proceed(request)
            }
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(interceptor).addInterceptor(interceptorBody)
        }

        return builder.build()
    }
}