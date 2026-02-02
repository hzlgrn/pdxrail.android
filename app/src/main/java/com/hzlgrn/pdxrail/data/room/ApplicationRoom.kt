package com.hzlgrn.pdxrail.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hzlgrn.pdxrail.BuildConfig
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.data.json.RailLineJson
import com.hzlgrn.pdxrail.data.json.RailStopJson
import com.hzlgrn.pdxrail.data.room.dao.ArrivalDao
import com.hzlgrn.pdxrail.data.room.dao.RailSystemDao
import com.hzlgrn.pdxrail.data.room.entity.ArrivalEntity
import com.hzlgrn.pdxrail.data.room.entity.BlockPositionEntity
import com.hzlgrn.pdxrail.data.room.entity.LocIdEntity
import com.hzlgrn.pdxrail.data.room.entity.RailLineEntity
import com.hzlgrn.pdxrail.data.room.entity.RailStopEntity
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader

@Database(
    version = 2,
    entities = [
        ArrivalEntity::class,
        BlockPositionEntity::class,
        LocIdEntity::class,
        RailLineEntity::class,
        RailStopEntity::class
               ],
    exportSchema = false
)
abstract class ApplicationRoom: RoomDatabase() {

    abstract fun arrivalDao(): ArrivalDao
    abstract fun railSystemDao(): RailSystemDao

    fun loadRailSystemData(context: Context, applicationRoom: ApplicationRoom) {
        val railStopEntities = readRailStopJson(context)?.rail_stops?.map { json ->
            RailStopEntity(
                    uniqueid = json.uniqueid,
                    station = json.station,
                    line = json.line,
                    type = json.type,
                    latitude = json.lat,
                    longitude = json.lon)
        } ?: emptyList()
        val railLineEntities = readRailLineJson(context)?.rail_lines?.map { json ->
            val polyline = json.polyline
            RailLineEntity(
                    line = json.line,
                    passage = json.passage,
                    type = json.type,
                    polylineString = polyline.fold("")
                    { total, item -> total + "${item.latitude},${item.longitude} " }.trimEnd())
        } ?: emptyList()
        val dao = applicationRoom.railSystemDao()
        dao.updateRailSystem(railStopEntities, railLineEntities)
        Timber.d("Saved ${railStopEntities.count()} rail stops and ${railLineEntities.count()} rail lines.")
    }

    private fun readRailStopJson(context: Context): RailStopJson? {
        val kotlinJsonAdapterFactory = KotlinJsonAdapterFactory()
        val moshi = Moshi.Builder()
            .add(kotlinJsonAdapterFactory)
            .build()

        val railStopsInputStream = context.resources.openRawResource(R.raw.init_data_rail_stops)
        val railStopsReader = BufferedReader(InputStreamReader(railStopsInputStream, "UTF8"))
        val railStopAdapter: JsonAdapter<RailStopJson> = moshi.adapter(RailStopJson::class.java)
        val railStopJsonString = railStopsReader.readText()
        val railStopJsonModel = railStopAdapter.fromJson(railStopJsonString)
        Timber.d("version: ${railStopJsonModel?.version} with ${railStopJsonModel?.rail_stops?.count() ?: 0} rail_stops")

        return railStopJsonModel
    }

    private fun readRailLineJson(context: Context): RailLineJson? {
        val kotlinJsonAdapterFactory = KotlinJsonAdapterFactory()
        val moshi = Moshi.Builder()
            .add(kotlinJsonAdapterFactory)
            .build()

        val railLineInputStream = context.resources.openRawResource(R.raw.init_data_rail_lines)
        val railLineReader = BufferedReader(InputStreamReader(railLineInputStream, "UTF8"))
        val railLineAdapter: JsonAdapter<RailLineJson> = moshi.adapter(RailLineJson::class.java)
        val railLineJsonString = railLineReader.readText()
        val railLineJsonModel = railLineAdapter.fromJson(railLineJsonString)
        Timber.d("version: ${railLineJsonModel?.version} with ${railLineJsonModel?.rail_lines?.count() ?: 0} rail_lines")

        return railLineJsonModel
    }
}