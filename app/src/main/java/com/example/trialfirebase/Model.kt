package com.example.trialfirebase

import com.google.firebase.firestore.FirebaseFirestore
import android.os.Parcel
import android.os.Parcelable

data class container(
    var title: String = "",
    val id: Long = 0,
    val destinations: Long = 0,
    val cargo_type: String = "",
    val cargo_weight: Long = 0,
    val location: Long =0
)
    : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeLong(id)
        parcel.writeLong(destinations)
        parcel.writeString(cargo_type)
        parcel.writeLong(cargo_weight)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<container> {
        override fun createFromParcel(parcel: Parcel): container {
            return container(parcel)
        }

        override fun newArray(size: Int): Array<container?> {
            return arrayOfNulls(size)
        }
    }
}

//object Data {
//    fun fetchContainerData(documentIds: List<String>, callback: (List<container>) -> Unit) {
//        val db = FirebaseFirestore.getInstance()
//        val collectionRef = db.collection("containers") // Replace with your collection name
//
//        val containers = mutableListOf<container>()
//        var documentsProcessed = 0
//
//        documentIds.forEach { documentId ->
//            collectionRef.document(documentId).get()
//                .addOnSuccessListener { documentSnapshot ->
//                    if (documentSnapshot.exists()) {
//                        val id = documentSnapshot.getLong("id")
//                        val destinations = documentSnapshot.getLong("destinations")
//                        val cargoType = documentSnapshot.getString("cargo_type")
//                        val cargoWeight = documentSnapshot.getLong("cargo_weight")
//
//                        if (id != null && destinations != null && cargoType != null && cargoWeight != null) {
//                            val container = container(documentId, id, destinations, cargoType, cargoWeight)
//                            containers.add(container)
//                        }
//                    }
//                    documentsProcessed++
//                    if (documentsProcessed == documentIds.size) {
//                        callback(containers)
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    println("Error fetching container data: $exception")
//                    documentsProcessed++
//                    if (documentsProcessed == documentIds.size) {
//                        callback(containers)
//                    }
//                }
//        }
//    }
//}



