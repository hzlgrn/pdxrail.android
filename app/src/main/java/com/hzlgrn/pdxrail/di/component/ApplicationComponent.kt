package com.hzlgrn.pdxrail.di.component

import com.hzlgrn.pdxrail.activity.common.ApplicationActivity
import com.hzlgrn.pdxrail.activity.railsystem.RailSystemStopActivity
import com.hzlgrn.pdxrail.task.TaskTrimetWsV1Stops
import com.hzlgrn.pdxrail.task.TaskTrimetWsV2Arrivals

interface ApplicationComponent {
    fun inject(activity: ApplicationActivity)
    fun inject(coroutine: TaskTrimetWsV1Stops)
    fun inject(coroutine: TaskTrimetWsV2Arrivals)
    fun inject(activity: RailSystemStopActivity)
}