package com.shakibaenur.quoteapplication.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.shakibaenur.quoteapplication.R
import com.shakibaenur.quoteapplication.core.base.BaseActivity
import com.shakibaenur.quoteapplication.data.model.Quote
import com.shakibaenur.quoteapplication.databinding.ActivityMainBinding
import com.shakibaenur.quoteapplication.ui.main.AutoScrollAdapter
import com.shakibaenur.quoteapplication.utils.AppConstant
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Math.abs


@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override val mViewModel: MainViewModel by viewModels()
    private  var imageList= arrayListOf<Quote>()
    private lateinit var adapter: AutoScrollAdapter

    private lateinit var handler: Handler

    override fun getViewBinding(): ActivityMainBinding =
        ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)
        mViewBinding.topBar.tvTitle.text = "Home"
        handler = Handler(Looper.myLooper()!!)
        setObserver()
        initview()
        setUpTransformer()
        mViewBinding.rvQuote.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 2000)
            }
        })
    }

    private fun setObserver() {
        addItemsFromJSON()
    }
    private fun addItemsFromJSON() {
        try {
            val jsonDataString = readJSONDataFromFile()
            val jsonArray = JSONArray(jsonDataString)
            for (i in 0 until jsonArray.length()) {
                val itemObj = jsonArray.getJSONObject(i)
                val quote= Quote(id=i,author=itemObj.getString("author"),category=itemObj.getString("category"), quote =itemObj.getString("quote") ,isLiked=itemObj.getBoolean("isLiked"))
                imageList.add(quote)

            }
        } catch (e: JSONException) {
            Log.d("addQuote", "addItemsFromJSON: ", e)
        } catch (e: IOException) {
            Log.d("addQuote", "addItemsFromJSON: ", e)
        }
    }
    @Throws(IOException::class)
    private fun readJSONDataFromFile(): String {
        var inputStream: InputStream? = null
        val builder = StringBuilder()

        try {
            var jsonString: String?
            inputStream = resources.openRawResource(R.raw.quotesfinal)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))

            while (bufferedReader.readLine().also { jsonString = it } != null) {
                builder.append(jsonString)
            }
        } finally {
            inputStream?.close()
        }

        return builder.toString()
    }


    private fun initview() {
        adapter = AutoScrollAdapter(imageList, mViewBinding.rvQuote)

        mViewBinding.rvQuote.adapter = adapter
        adapter.listener = object : AutoScrollAdapter.ServiceListAdapterListener {
            override fun onItemClick(model: Quote, position: Int) {
                val intent = Intent(this@MainActivity, EditActivity::class.java)
                intent.putExtra(AppConstant.INTENT_QUOTE, model)
                startActivity(intent)
            }
        }
        mViewBinding.rvQuote.offscreenPageLimit = 3
        mViewBinding.rvQuote.clipToPadding = false
        mViewBinding.rvQuote.clipChildren = false
        mViewBinding.rvQuote.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

    }

    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()

        handler.postDelayed(runnable, 2000)
    }

    private val runnable = Runnable {
        mViewBinding.rvQuote.currentItem = mViewBinding.rvQuote.currentItem + 1
    }

    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }

        mViewBinding.rvQuote.setPageTransformer(transformer)
    }
}