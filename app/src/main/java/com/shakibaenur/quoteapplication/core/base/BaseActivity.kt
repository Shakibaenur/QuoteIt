package com.shakibaenur.quoteapplication.core.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.shakibaenur.quoteapplication.R
import com.shakibaenur.quoteapplication.utils.AppConstant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by Shakiba E Nur on 03,March,2023
 */
abstract class BaseActivity<VM : ViewModel, VB : ViewBinding> : AppCompatActivity() {
    protected abstract val mViewModel: VM

    protected lateinit var mViewBinding: VB

    abstract fun getViewBinding(): VB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewBinding = getViewBinding()

        setUpObservers()

    }


    private fun setUpObservers() {


    }

    private fun showSnackBar(message: String) {
        runOnUiThread {
            Snackbar.make(mViewBinding.root, message, Snackbar.LENGTH_LONG).show()
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun hideKeyboard() {
        val imm: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mViewBinding.root.windowToken, 0)
    }

    protected fun clearCache() {
        val sharePref = applicationContext?.getSharedPreferences(
            AppConstant.PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sharePref?.edit()
        editor?.apply()
    }


    protected fun gotoNewActivity(activityClass: Class<*>) {
        if (this.javaClass != activityClass) {
            val intent = Intent(this, activityClass)
            startActivity(intent)
        }
    }

    fun gotoNewActivityWithClearActivity(activityClass: Class<*>) {
        if (this.javaClass != activityClass) {
            val intent = Intent(this, activityClass)
            startActivity(intent)
            finish()
        }
    }

    fun gotoNewActivityWithCleanAllActivity(activityClass: Class<*>) {
        if (this.javaClass != activityClass) {
            val intent = Intent(this, activityClass)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        or Intent.FLAG_ACTIVITY_NEW_TASK
            )
            startActivity(intent)
        }
    }



    protected fun getPreferenceData(key: String): String {
        val sharePref =
            applicationContext?.getSharedPreferences(
                AppConstant.PREFERENCE_NAME,
                Context.MODE_PRIVATE
            )
        return sharePref?.getString(key, "") ?: ""
    }

    protected fun setPreferenceData(key: String, value: String) {
        val sharePref =
            applicationContext?.getSharedPreferences(
                AppConstant.PREFERENCE_NAME,
                Context.MODE_PRIVATE
            )
        val editor = sharePref?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }



    private fun getLayoutBitmap(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas)
        else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss")
        return sdf.format(Date())
    }

    fun getLastSaturday(): String {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.WEEK_OF_YEAR, -2)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        val s = SimpleDateFormat("yyyy-MM-dd")
        return s.format(Date(calendar.timeInMillis))
    }

    fun getLastFriday(): String {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        val s = SimpleDateFormat("yyyy-MM-dd")
        return s.format(Date(calendar.timeInMillis))
    }

    fun getCurrentDateDeafault(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(Date())
    }

    private fun rignNotification() {
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mp: MediaPlayer = MediaPlayer.create(this, alarmSound)
        mp.start()

    }



    override fun onResume() {

        super.onResume()
    }
}