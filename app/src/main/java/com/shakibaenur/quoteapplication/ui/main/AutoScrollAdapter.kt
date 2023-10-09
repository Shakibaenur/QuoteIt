package com.shakibaenur.quoteapplication.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.shakibaenur.quoteapplication.data.model.Quote
import com.shakibaenur.quoteapplication.databinding.ItemAutoScrollBinding


class AutoScrollAdapter(var items: ArrayList<Quote>, private val viewPager2: ViewPager2) :
    RecyclerView.Adapter<AutoScrollAdapter.ServiceListAdapterViewHolder>() {
    var listener: ServiceListAdapterListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServiceListAdapterViewHolder {
        val binding =
            ItemAutoScrollBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceListAdapterViewHolder(
            binding
        )

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ServiceListAdapterViewHolder, position: Int) {
        listener?.let {
            holder.bind(it, items[position], position)
        }
    }

    interface ServiceListAdapterListener {
        fun onItemClick(model: Quote, position: Int)
    }

    private val runnable = Runnable {
        items.addAll(items)
        notifyDataSetChanged()
    }

    inner class ServiceListAdapterViewHolder(
        private val binding: ItemAutoScrollBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: ServiceListAdapterListener, model: Quote, position: Int) {
            binding.tvTitle.text = model.quote
            binding.tvSubTitle.text = model.author
            if (position == items.size - 1) {
                viewPager2.post(runnable)
            }
            binding.cvMain.setOnClickListener() {
                listener.onItemClick(model, position)
            }
        }
    }
}