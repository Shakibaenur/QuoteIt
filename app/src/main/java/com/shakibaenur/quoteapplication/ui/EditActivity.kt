package com.shakibaenur.quoteapplication.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shakibaenur.quoteapplication.R
import com.shakibaenur.quoteapplication.core.base.BaseActivity
import com.shakibaenur.quoteapplication.data.model.Quote
import com.shakibaenur.quoteapplication.databinding.ActivityEditBinding
import com.shakibaenur.quoteapplication.utils.AppConstant
import top.defaults.colorpicker.ColorPickerPopup
import top.defaults.colorpicker.ColorPickerPopup.ColorPickerObserver

class EditActivity : BaseActivity<MainViewModel, ActivityEditBinding>() {

    private lateinit var quoteModel: Quote
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetDialogLine: BottomSheetDialog
    private lateinit var seekBar: SeekBar
    private lateinit var seekBarLine: SeekBar
    override val mViewModel: MainViewModel by viewModels()
    override fun getViewBinding(): ActivityEditBinding =
        ActivityEditBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)
        initView()
        setClickListener()
    }


    private fun initView() {
        mViewBinding.ltTopBar.tvTitle.text = "Edit Quote"
        mViewBinding.ltEditorImage.tvHashTag.text = "#devDroidQuotes"
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.layout_seek_bar)
        bottomSheetDialogLine = BottomSheetDialog(this)
        bottomSheetDialogLine.setContentView(R.layout.layout_seek_bar)
        seekBar = bottomSheetDialog.findViewById<SeekBar>(R.id.seekbar)!!
        seekBarLine = bottomSheetDialogLine.findViewById<SeekBar>(R.id.seekbar)!!
        if (getModel() != null) {
            quoteModel = getModel()!!
            val quote=quoteModel.quote+"\n\n"+ quoteModel.author
            mViewBinding.ltEditorImage.tvTitle.text = quote
        }

    }

    private fun getModel(): Quote? {
        val model = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                AppConstant.INTENT_QUOTE,
                Quote::class.java
            )
        } else {
            intent.getParcelableExtra<Quote>(AppConstant.INTENT_QUOTE)
        }
        return model
    }

    private fun setClickListener() {
        mViewBinding.ltBottomUp.apply {
            btnColorPalate.setOnClickListener { v ->
                setColor(v, mViewBinding.ltEditorImage.root, 0)
            }
            btnTextColor.setOnClickListener { v ->
                setColor(v, mViewBinding.ltEditorImage.tvTitle, 1)
            }
            btnSize.setOnClickListener {
                bottomSheetDialog.show()
            }
            btnHeight.setOnClickListener {
                bottomSheetDialogLine.show()
            }
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                mViewBinding.ltEditorImage.tvTitle.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    progress.toFloat()
                )

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        seekBarLine.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                mViewBinding.ltEditorImage.tvTitle.setLineSpacing(progress.toFloat(),1F)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setColor(view: View, viewSet: View, type: Int) {
        ColorPickerPopup.Builder(this@EditActivity)
            .initialColor(Color.RED) // Set initial color
            .enableBrightness(true) // Enable brightness slider or not
            .enableAlpha(false) // Enable alpha slider or not
            .okTitle("Done")
            .cancelTitle("Cancel")
            .showIndicator(true)
            .showValue(false)
            .build()
            .show(view, object : ColorPickerObserver() {
                override fun onColorPicked(color: Int) {
                    when (type) {
                        0 -> viewSet.setBackgroundColor(color)
                        1 -> {
                            viewSet as TextView
                            viewSet.setTextColor(color)
                        }
                    }
                }

                fun onColor(color: Int, fromUser: Boolean) {}
            })
    }
}