package com.habitflow.core.data.source.remote

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class RemoteHydrationEntry(
    val id: String,
    val amountMl: Int,
    val dateTime: String,
    val source: String
)

interface HydrationRemoteSync {
    val isAvailable: Boolean
    fun startListeningDay(scope: CoroutineScope, userId: String, dayPrefix: String, onRemote: suspend (List<RemoteHydrationEntry>) -> Unit)
    suspend fun upsert(userId: String?, entry: RemoteHydrationEntry)
    suspend fun delete(userId: String?, id: String)
}

class FirebaseHydrationSync(context: Context) : HydrationRemoteSync {
    private val firebaseApp = FirebaseApp.initializeApp(context) ?: FirebaseApp.getApps(context).firstOrNull()
    private val firestore = firebaseApp?.let { FirebaseFirestore.getInstance(it) }
    override val isAvailable: Boolean = firestore != null

    override fun startListeningDay(scope: CoroutineScope, userId: String, dayPrefix: String, onRemote: suspend (List<RemoteHydrationEntry>) -> Unit) {
        if (!isAvailable) return
        scope.launch(Dispatchers.IO) {
            hydrationCollection(userId)
                ?.whereGreaterThanOrEqualTo("dateTime", dayPrefix)
                ?.whereLessThan("dateTime", dayPrefix + "~")
                ?.addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener
                    val list = snapshot.documents.mapNotNull { doc ->
                        val data = doc.data ?: return@mapNotNull null
                        val amount = (data["amountMl"] as? Number)?.toInt() ?: return@mapNotNull null
                        val dateTime = data["dateTime"] as? String ?: return@mapNotNull null
                        val source = data["source"] as? String ?: "quick"
                        RemoteHydrationEntry(doc.id, amount, dateTime, source)
                    }
                    scope.launch { onRemote(list) }
                }
        }
    }

    override suspend fun upsert(userId: String?, entry: RemoteHydrationEntry) {
        val uid = userId ?: return
        if (!isAvailable) return
        hydrationCollection(uid)?.document(entry.id)?.set(
            mapOf(
                "amountMl" to entry.amountMl,
                "dateTime" to entry.dateTime,
                "source" to entry.source
            )
        )?.await()
    }

    override suspend fun delete(userId: String?, id: String) {
        val uid = userId ?: return
        if (!isAvailable) return
        hydrationCollection(uid)?.document(id)?.delete()?.await()
    }

    private fun hydrationCollection(userId: String) =
        firestore?.collection("users")?.document(userId)?.collection("hydration")
}

