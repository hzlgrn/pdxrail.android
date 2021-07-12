package com.hzlgrn.pdxrail.task

import android.os.Bundle
import android.os.SystemClock
import com.hzlgrn.pdxrail.App
import com.hzlgrn.pdxrail.data.net.RailSystemService
import com.hzlgrn.pdxrail.data.room.ApplicationRoom
import com.hzlgrn.pdxrail.data.room.entity.ArrivalEntity
import com.hzlgrn.pdxrail.data.room.entity.BlockPositionEntity
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class TaskTrimetWsV2Arrivals: CoroutineScope {
    companion object {
        const val THROTTLE_ARRIVALS = 10000L // 10 seconds!
        private val MEMORY = Bundle()
    }

    @Inject
    lateinit var applicationRoom: ApplicationRoom

    @Inject
    lateinit var railSystemService: RailSystemService

    init { App.applicationComponent.inject(this) }


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineExceptionHandler
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
    }

    fun launchJob(locid: List<Long>, isStreetCar: Boolean): Job = launch(coroutineContext) {
        while(true) {
            fetchArrivals(locid, isStreetCar)
            delay(THROTTLE_ARRIVALS)
        }
    }

    private fun fetchArrivals(locid: List<Long>, isStreetCar: Boolean): List<ArrivalEntity> {
        val arrivals = mutableListOf<ArrivalEntity>()
        val memoryKey = "arrivals-$locid-updated"
        val now = SystemClock.elapsedRealtime()
        val lastArrivalFetch = MEMORY.getLong(memoryKey, 0L)
        if (now - lastArrivalFetch > THROTTLE_ARRIVALS) {
            try {

                val csvLocId = locid.let {
                    var buildCsv = ""
                    for (value in it) {
                        if (buildCsv.isEmpty()) buildCsv = value.toString()
                        else buildCsv += ",$value"
                    }
                    buildCsv
                }
                railSystemService.wsV2Arrivals(csvLocId, isStreetCar).also {
                    Timber.d(it.request().url.toString())
                }.execute().also { response ->

                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code: ${response.code()} message: ${response.message()}")
                    } else {

                        val blockPositions = mutableListOf<BlockPositionEntity>()

                        response.body()?.resultSet?.arrival?.let { arrivalResults ->

                            for (arrival in arrivalResults) {
                                val arrivalModel = ArrivalEntity(arrival)
                                if (isStreetCar) {
                                    val isValid = arrival.fullSign.contains("streetcar", true)
                                    if (isValid) {
                                        arrival.blockPosition?.let { blockPosition ->
                                            arrivalModel.blockPositionId = blockPosition.id
                                            blockPositions.add(BlockPositionEntity(blockPosition))
                                        }
                                        arrivals.add(arrivalModel)
                                    }
                                } else {
                                    val isValid = arrival.fullSign.contains("max", true)
                                            || arrival.fullSign.contains("wes", true)
                                    if (isValid) {
                                        arrival.blockPosition?.let { blockPosition ->
                                            arrivalModel.blockPositionId = blockPosition.id
                                            blockPositions.add(BlockPositionEntity(blockPosition))
                                        }
                                        arrivals.add(arrivalModel)
                                    }
                                }
                            }

                        }

                        applicationRoom.triMetDao().apply {
                            updateArrivals(locid.toList(), blockPositions, arrivals)
                        }
                        MEMORY.putLong(memoryKey, SystemClock.elapsedRealtime())
                    }
                }

            } catch (err: Exception) { Timber.e(err) }
        }

        return arrivals
    }

}