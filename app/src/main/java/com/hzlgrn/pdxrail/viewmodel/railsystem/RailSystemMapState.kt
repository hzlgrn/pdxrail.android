package com.hzlgrn.pdxrail.viewmodel.railsystem

import kotlinx.collections.immutable.ImmutableList

/***
 * The RailSystemMap contains static map features and when they are made available by the
 * Database.
 */
sealed class RailSystemMapState {
    object Idle: RailSystemMapState()
    object Loading: RailSystemMapState()
    data class Display(val mapItems: ImmutableList<RailSystemMapItem>): RailSystemMapState()
}