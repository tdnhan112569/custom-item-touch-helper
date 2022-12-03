package com.zhiliaoapp.musically.touchhelperexample

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class RecyclerViewHolder(private val context: Context,  private val listItems: LinkedList<String> = LinkedList())
    : RecyclerView.Adapter<RecyclerViewHolder.ViewHolder>() {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val textView: TextView = view.findViewById(R.id.tvTitle)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            textView.text = "Item $position"
            view.setOnClickListener {
                Log.e("ItemTouchHelperTest", "Item click $position")
                if(context is MainActivity) {
                    if(!context.clearCurrentSwipeView()) {
                        Toast.makeText(view.context, "Item click $position", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_item, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount()
        = listItems.size

}