package com.habitflow.core.data.repository.impl

import com.habitflow.core.data.di.IoDispatcher
import com.habitflow.core.data.di.RoomTransactionRunner
import com.habitflow.core.data.mapper.toDomain
import com.habitflow.core.data.mapper.toEntity
import com.habitflow.core.data.source.remote.HydrationRemoteSync
import com.habitflow.core.database.HabitFlowDatabase
import com.habitflow.core.database.dao.HydrationDao
import com.habitflow.domain.model.HydrationEntry
import com.habitflow.domain.repository.HydrationRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Singleton
class DefaultHydrationRepository @Inject constructor(
    private val hydrationDao: HydrationDao,
    private val remote: HydrationRemoteSync,
    private val database: HabitFlowDatabase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val uuidProvider: () -> String,
    private val externalScope: CoroutineScope,
    @RoomTransactionRunner private val transactionRunner: @JvmSuppressWildcards suspend (suspend () -> Unit) -> Unit
) : HydrationRepository {

    private var currentUserId: String? = null
    private val dayFlows = mutableMapOf<String, Flow<List<HydrationEntry>>>()

    override fun observeDay(dayKey: String): Flow<List<HydrationEntry>> =
        dayFlows.getOrPut(dayKey) {
            hydrationDao.observeDay(dayKey).map { list -> list.map { it.toDomain() } }
        }

    override fun add(amountMl: Int, dateTime: String, source: String): HydrationEntry {
        val entry = HydrationEntry(
            id = uuidProvider(),
            amountMl = amountMl,
            dateTime = dateTime,
            source = source
        )
        externalScope.launch(ioDispatcher) {
            hydrationDao.upsert(entry.toEntity())
            remote.upsert(currentUserId, com.habitflow.core.data.source.remote.RemoteHydrationEntry(entry.id, amountMl, dateTime, source))
        }
        return entry
    }

    override fun delete(id: String) {
        externalScope.launch(ioDispatcher) {
            hydrationDao.delete(id)
            remote.delete(currentUserId, id)
        }
    }

    override fun onUserChanged(userId: String?) {
        currentUserId = userId
        if (!remote.isAvailable || userId == null) return
        val today = LocalDate.now().toString()
        remote.startListeningDay(externalScope, userId, today) { remoteList ->
            externalScope.launch(ioDispatcher) {
                transactionRunner {
                    // minimal merge: upsert all remote entries for the day
                    remoteList.forEach { r ->
                        hydrationDao.upsert(
                            com.habitflow.core.database.entity.HydrationEntryEntity(
                                id = r.id,
                                amountMl = r.amountMl,
                                dateTime = r.dateTime,
                                source = r.source
                            )
                        )
                    }
                }
            }
        }
    }
}

