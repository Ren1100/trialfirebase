package com.example.trialfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class containerAdapter(
    private val context: Context,
    private val containers: List<container>,
    private val onDeleteClickListener: OnDeleteClickListener
) : RecyclerView.Adapter<containerAdapter.containerViewHolder>() {

    private val selectedContainers = mutableSetOf<container>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): containerViewHolder {
        // Inflate the item layout for the RecyclerView
        val view = LayoutInflater.from(context).inflate(R.layout.containerlist, parent, false)
        return containerViewHolder(view)
    }

    override fun getItemCount(): Int {
        // Return the number of items in the list
        return containers.size
    }

    override fun onBindViewHolder(holder: containerViewHolder, position: Int) {
        // Bind data to the views in the ViewHolder
        val container = containers[position]
        holder.bind(container)
    }

    fun getSelectedContainers(): List<container> {
        // Return a list of selected containers
        return selectedContainers.toList()
    }


    inner class containerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // View references for the item layout
        private val txvTitle: AutoCompleteTextView = itemView.findViewById(R.id.txvTitle)
        private val txvID: TextView = itemView.findViewById(R.id.txvID)
        private val txvDestinations: TextView = itemView.findViewById(R.id.txvDestinations)
        private val txvCargoType: TextView = itemView.findViewById(R.id.txvCargoType)
        private val txvCargoWeight: TextView = itemView.findViewById(R.id.txvCargoWeight)

        fun bind(container: container) {
            // Bind container data to the views
            txvTitle.setText(container.title)
            txvID.setText(container.id.toString())
            txvDestinations.setText(container.destinations.toString())
            txvCargoType.setText(container.cargo_type)
            txvCargoWeight.setText(container.cargo_weight.toString())

            val adapter = object:ArrayAdapter<String>(
                context,
                android.R.layout.simple_dropdown_item_1line,
                container.getFirestoreData()
            )
            {
                override fun isEnabled(position: Int): Boolean {
                    // Disable selection for all items except the currently selected one
                    return position == container.getFirestoreData().indexOf(container.title)
                }
            }

            txvTitle.setAdapter(adapter)

            txvTitle.setText(container.title, false)

            itemView.setOnClickListener {
                // Toggle container selection on item click
                if (selectedContainers.contains(container)) {
                    selectedContainers.remove(container)
                    itemView.setBackgroundResource(android.R.color.transparent)
                } else {
                    selectedContainers.add(container)
                    itemView.setBackgroundResource(R.color.white)
                }
            }
        }

        private fun container.getFirestoreData(): List<String> {
            // Generate a list of Firestore data for the container
            val firestoreData = mutableListOf<String>()

            val idLabel = "ID: $id"
            val destinationsLabel = "Destinations: $destinations"
            val cargoTypeLabel = "Cargo Type: $cargo_type"
            val cargoWeightLabel = "Cargo Weight: $cargo_weight"

            firestoreData.add(idLabel)
            firestoreData.add(destinationsLabel)
            firestoreData.add(cargoTypeLabel)
            firestoreData.add(cargoWeightLabel)

            return firestoreData
        }

    }

    interface OnDeleteClickListener {
        fun onDeleteClick(selectedContainers: List<container>)
    }
}
