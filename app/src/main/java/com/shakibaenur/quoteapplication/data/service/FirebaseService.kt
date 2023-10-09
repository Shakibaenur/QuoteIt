package com.shakibaenur.quoteapplication.data.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Created by Shakiba E Nur on 04,March,2023
 */
class FirebaseService<T : Any>(private val collectionName: String, private val clazz: Class<T>) {

    private val db = FirebaseFirestore.getInstance()
    private var list = listOf<T>()

    suspend fun getAll(): List<T> {
        val snapshot = db.collection(collectionName).get().await()
        return snapshot.toObjects(clazz)
    }

    suspend fun getById(id: String): T? {
        val document = db.collection(collectionName).document(id).get().await()
        return document.toObject(clazz)
    }

    suspend fun getByFieldId(id: String): List<T> {
        val snapshot = db.collection(collectionName)
        snapshot.whereEqualTo("author", id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    list= listOf(document.toObject(clazz))

                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting documents: ", exception)
            }.await()
        snapshot.whereEqualTo("category", id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    list= listOf(document.toObject(clazz))

                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error getting documents: ", exception)
            }.await()
        return list
    }

    suspend fun add(item: T): Boolean {
        return try {
            db.collection(collectionName).add(item).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun update(id: String, item: T): Boolean {
        return try {
            db.collection(collectionName).document(id).set(item).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun delete(id: String): Boolean {
        return try {
            db.collection(collectionName).document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}