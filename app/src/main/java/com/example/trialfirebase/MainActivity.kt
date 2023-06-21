package com.example.trialfirebase

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity(), containerAdapter.OnDeleteClickListener {
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: containerAdapter
    private val containers: MutableList<container> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance() //initialize the firebase firestore

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = containerAdapter(this, containers, this)
        recyclerView.adapter = adapter

        //get the container data from the QR Scan
        val intent = intent
        if (intent.hasExtra("CONTAINERS_EXTRA")) {
            val receivedContainers = intent.getParcelableArrayListExtra<container>("CONTAINERS_EXTRA")
            if (receivedContainers != null) {
                for (container in receivedContainers) {
                    if (!containers.any { it.title == container.title }) {
                        containers.add(container)
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }

//        val documentIds = listOf(
//            "containers1",
//            "containers10",
//            "containers100"
//        )

//        Data.fetchContainerData(documentIds) { fetchedContainers ->
//            if (fetchedContainers.isNotEmpty()) {
//                containers.addAll(fetchedContainers)
//                adapter.notifyDataSetChanged()
//            } else {
//                println("Error fetching container data")
//            }
//        }

        val deleteButton: Button = findViewById(R.id.deleteBtn)
        deleteButton.setOnClickListener {
            val selectedContainers = adapter.getSelectedContainers()
            onDeleteClick(selectedContainers)
        }
        val btnScanQR: Button = findViewById(R.id.btnScanQR)
        btnScanQR.setOnClickListener {
            // Start the QRScanActivity when the button is clicked
            val intent = Intent(this, QRScanActivity::class.java)
            startActivityForResult(intent, SCAN_REQUEST_CODE)
        }
        val sendButton: Button = findViewById(R.id.btnSend)
        sendButton.setOnClickListener {
            val data = containers 

            // Create a new document in the "data" collection with the provided data
            db.collection("sample")
                .document("sample_pred")
                .set(mapOf("message" to data))
        }

        val getButton: Button = findViewById(R.id.btnGet)
        getButton.setOnClickListener{
            fetchDataAndNavigate()
        }

    }
    
    // Fetch data from Firestore and navigate to the predicted activity
    private fun fetchDataAndNavigate() {
        val db = FirebaseFirestore.getInstance()
        val containersRef = db.collection("results")

        containersRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Toast.makeText(this, "Data is not yet available", Toast.LENGTH_SHORT).show()
                } else {
                    val containerDataList = mutableListOf<Map<String, Any>>()
                    for (document in querySnapshot) {
                        val containerData = document.data
                        containerDataList.add(containerData)
                    }
                    navigateToDisplayActivity(containerDataList)
                }
            }
    }
    // Navigate to the predicted activity and pass container data as an intent extra
    private fun navigateToDisplayActivity(containerDataList: List<Map<String, Any>>) {
        val intent = Intent(this, predicted::class.java)
        intent.putExtra("containerDataList", containerDataList.toTypedArray())
        startActivity(intent)
    }

    override fun onDeleteClick(selectedContainers: List<container>) {
        containers.removeAll(selectedContainers)
        adapter.notifyDataSetChanged()
    }

    // Handle the result from QRScanActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Companion.SCAN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra("CONTAINERS_EXTRA")) {
                val receivedContainers = data.getParcelableArrayListExtra<container>("CONTAINERS_EXTRA")
                if (receivedContainers != null) {
                    for (container in receivedContainers) {
                        if (!containers.any { it.title == container.title }) {
                            containers.add(container)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }


    companion object {
        private const val SCAN_REQUEST_CODE = 123
    }

}
