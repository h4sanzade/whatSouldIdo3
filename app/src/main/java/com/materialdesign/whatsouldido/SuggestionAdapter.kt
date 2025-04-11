package com.materialdesign.whatsouldido

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView

class SuggestionsAdapter(
    private val context: Context,
    private val suggestions: MutableList<String>,
    private val onDelete: (Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = suggestions.size

    override fun getItem(position: Int): Any = suggestions[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false)

        val suggestionText = view.findViewById<TextView>(R.id.suggestionItemText)
        val deleteButton = view.findViewById<ImageButton>(R.id.deleteButton)

        suggestionText.text = suggestions[position]

        deleteButton.setOnClickListener {
            onDelete(position)
            notifyDataSetChanged()
        }

        return view
    }
}