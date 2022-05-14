package com.hzlgrn.pdxrail.di.component

import com.hzlgrn.pdxrail.activity.common.ApplicationActivity
import com.hzlgrn.pdxrail.activity.railsystem.RailSystemStopActivity
import com.hzlgrn.pdxrail.task.TaskWsV1Stops
import com.hzlgrn.pdxrail.task.TaskWsV2Arrivals

interface ApplicationComponent {
    fun inject(activity: ApplicationActivity)
    fun inject(coroutine: TaskWsV1Stops)
    fun inject(coroutine: TaskWsV2Arrivals)
    fun inject(activity: RailSystemStopActivity)
}