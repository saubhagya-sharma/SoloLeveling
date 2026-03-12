package com.example.sololeveling

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SessionExerciseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<SessionDetailListItem>()

    fun submitItems(newItems: List<SessionDetailListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SessionDetailListItem.ExerciseHeader -> VIEW_TYPE_HEADER
            is SessionDetailListItem.SetRow -> VIEW_TYPE_SET
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return if (viewType == VIEW_TYPE_HEADER) HeaderViewHolder(view) else SetViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is SessionDetailListItem.ExerciseHeader -> (holder as HeaderViewHolder).bind(item)
            is SessionDetailListItem.SetRow -> (holder as SetViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    private class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(item: SessionDetailListItem.ExerciseHeader) {
            textView.text = item.title
            textView.setTypeface(textView.typeface, Typeface.BOLD)
        }
    }

    private class SetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(item: SessionDetailListItem.SetRow) {
            textView.text = if (item.minutes != null) {
                "  %.1f min".format(item.minutes)
            } else {
                val weight = item.weight ?: 0.0
                val reps = item.reps ?: 0
                "  ${weight}kg x $reps"
            }
            textView.setTypeface(textView.typeface, Typeface.NORMAL)
        }
    }

    private companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_SET = 1
    }
}

sealed class SessionDetailListItem {
    data class ExerciseHeader(val title: String) : SessionDetailListItem()
    data class SetRow(
        val reps: Int?,
        val weight: Double?,
        val minutes: Double?
    ) : SessionDetailListItem()
}
