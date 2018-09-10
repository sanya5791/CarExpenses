package com.carexpenses.akhutornoy.carexpenses.ui.list.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.carexpenses.akhutornoy.carexpenses.R
import kotlinx.android.synthetic.main.item_refill.view.*

class RefillListAdapter(var items: List<RefillItem>) : RecyclerView.Adapter<RefillListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_refill, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
            = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
            = holder.bind(items[position])

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var item: RefillItem

        fun bind(item: RefillItem) {
            this.item = item

            view.setOnClickListener {
                Toast.makeText(view.context, "Element $adapterPosition clicked.", Toast.LENGTH_LONG).show()
            }

            view.dateTextView.text = item.date
            view.filledTextView.text = item.litersCount.toString()
            view.consumptionTextView.text = item.consumption.toString()
            view.trafficModeTextView.text = item.trafficMode
            view.noteIcon.visibility =
                if(item.isNoteAvailable) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
        }
    }
}