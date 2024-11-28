package com.example.mealmate.auth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.R
import com.example.mealmate.models.Ingredient

class GroceryAdapter(
    private val groceryItems: List<Ingredient>,
    private val onEditClick: (Ingredient) -> Unit,
    private val onDeleteClick: (Ingredient) -> Unit,
    private val onItemChecked: (Ingredient, Boolean) -> Unit // Callback for checkbox state changes
) : RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder>() {

    inner class GroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val groceryItemTitle: TextView = itemView.findViewById(R.id.groceryItemTitle)
        private val editButton: ImageButton = itemView.findViewById(R.id.btnEditGroceryItem)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDeleteGroceryItem)
        private val purchasedCheckBox: CheckBox = itemView.findViewById(R.id.cbPurchased) // Checkbox for purchased state

        fun bind(item: Ingredient) {
            groceryItemTitle.text = item.name
            purchasedCheckBox.isChecked = item.isPurchased // Reflect the current state of purchased

            // Set checkbox change listener
            purchasedCheckBox.setOnCheckedChangeListener { _, isChecked ->
                onItemChecked(item, isChecked)
            }

            // Set edit button click listener
            editButton.setOnClickListener {
                onEditClick(item)
            }

            // Set delete button click listener
            deleteButton.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grocery, parent, false)
        return GroceryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        holder.bind(groceryItems[position])
    }

    override fun getItemCount(): Int = groceryItems.size
}
