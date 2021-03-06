package com.akhutornoy.carexpenses.ui.list.recyclerview

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.ui.list.model.RefillItem
import kotlinx.android.synthetic.main.item_refill.view.*

class RefillListAdapter(
        private val items: List<RefillItem>,
        private val listener: OnItemSelected<RefillItem>) : RecyclerView.Adapter<RefillListAdapter.ViewHolder>() {

    var fuelTypeVisibility = View.VISIBLE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_refill, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
            = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
            = holder.bind(fuelTypeVisibility, items[position], listener)

    class ViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        lateinit var item: RefillItem

        fun bind(fuelTypeVisibility: Int, item: RefillItem, listener: OnItemSelected<RefillItem>) {
            this.item = item

            view.setOnClickListener { listener.onItemSelected(item) }

            view.dateTextView.text = item.date
            view.filledTextView.text = item.litersCount.toString()
            val consumption = "%.2f".format(item.consumption)
            view.consumptionTextView.text = consumption
            view.trafficModeTextView.text = item.trafficMode
            view.fuelTypeTextView.visibility = fuelTypeVisibility
            view.fuelTypeTextView.text = item.fuelType
            view.noteIcon.visibility =
                if(item.isNoteAvailable) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
        }
    }

    interface OnItemSelected<T> {
        fun onItemSelected(item: T)
    }
}