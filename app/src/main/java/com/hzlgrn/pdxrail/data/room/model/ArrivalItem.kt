package com.hzlgrn.pdxrail.data.room.model

import androidx.room.Relation
import com.hzlgrn.pdxrail.data.room.entity.BlockPositionEntity

class ArrivalItem(
        val estimated: Long,
        val scheduled: Long,
        val shortSign: String? = null,
        val blockPositionId: Long) {

    @Relation(
        parentColumn = "blockPositionId",
        entityColumn = "id",
        entity = BlockPositionEntity::class)
    var blockPosition: List<BlockPositionEntity> = emptyList()

}