package com.hzlgrn.pdxrail

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FlowUnitTest {

    // Intentionally using Int to keep things light
    // and lower the risk of using time/date on a delay.
    fun <T> Flow<T>.delayFlow(delayMs: Int = 0): Flow<T> = flow {
        collect { upstream ->
            if (delayMs > 0) delay(delayMs.toLong())
            emit(upstream)
        }

    }

    // Intentionally using Int to keep things light
    // and lower the risk of using time/date on a delay.
    fun <T> Flow<T>.delayAfterFlow(delayMs: Int = 0): Flow<T> = flow {
        collect { upstream ->
            emit(upstream)
            if (delayMs > 0) delay(delayMs.toLong())
        }
    }

    // intentionally using Int to keep things light
    // and lower the risk of using time/date for a delay.
    fun <T> Flow<T>.trickleFlow(upstreamAfterMs: Int): Flow<T> = flow {
        var lastUpstreamAt = System.currentTimeMillis()
        collect { upstream ->
            val currentTime = System.currentTimeMillis()
            lastUpstreamAt = if (currentTime - lastUpstreamAt > upstreamAfterMs) {
                emit(upstream)
                currentTime // lastUpstreamAt = currentTime
            } else {
                lastUpstreamAt // lastUpstreamAt = lastUpstreamAt
            }
        }
    }

    // intentionally using Int to keep things light
    // and lower the risk of using time/date for a delay.
    fun <T> Flow<T>.trickleAfterFlow(upstreamAfterMs: Int): Flow<T> = flow {
        var lastUpstreamAt = 0L
        collect { upstream ->
            lastUpstreamAt = if (lastUpstreamAt == 0L) {
                emit(upstream)
                System.currentTimeMillis() // lastUpstreamAt = System.currentTimeMillis()
            } else {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastUpstreamAt > upstreamAfterMs) {
                    emit(upstream)
                    currentTime // lastUpstreamAt = currentTime
                } else {
                    lastUpstreamAt // lastUpstreamAt = lastUpstreamAt
                }
            }
        }
    }

    @Test
    fun testFlow() {
        println("concurrency test(START)")
        runBlocking  {
            flow {
                for (test in 1..10) {
                    println("concurrency emit($test)")
                    emit(test)
                }
            }.collect { data -> println("concurrency collect($data)") }
        }
    }


    @Test
    fun testDelayFlow() {
        println("delayFlow test(START): ${System.currentTimeMillis()/1000}")
        runBlocking  {
            flow {
                for (test in 1..10) {
                    println("delayFlow emit($test): ${System.currentTimeMillis()/1000}")
                    emit(test)
                }
            }.delayFlow(1000).collect { data ->
                println("delayFlow collect($data): ${System.currentTimeMillis()/1000}")
            }
        }
    }

    @Test
    fun testDelayAfterFlow() {
        println("delayAfterFlow test(START): ${System.currentTimeMillis()/1000}")
        runBlocking  {
            flow {
                for (test in 1..10) {
                    println("delayAfterFlow emit($test): ${System.currentTimeMillis()/1000}")
                    emit(test)
                }
            }.delayAfterFlow(1000).collect { data ->
                println("delayAfterFlow collect($data): ${System.currentTimeMillis()/1000}")
            }
        }
    }

    @Test
    fun testTrickleFlow() {
        println("trickleFlow test(START): ${System.currentTimeMillis()/1000}")
        runBlocking  {
            flow {
                for (test in 1..10) {
                    if (test == 1) {
                        println("trickleFlow emit($test): ${System.currentTimeMillis()/1000}")
                        emit(test)
                    } else delay(1000)
                    if (test > 1) {
                        println("trickleFlow emit($test): ${System.currentTimeMillis()/1000}")
                        emit(test)
                    } else delay(1000)
                }
            }.trickleFlow(3000).collect { data ->
                println("trickleFlow collect($data): ${System.currentTimeMillis()/1000}")
            }
        }
    }

    @Test
    fun testTrickleAfterFlow() {
        println("trickleAfterFlow test(START): ${System.currentTimeMillis()/1000}")
        runBlocking  {
            flow {
                for (test in 1..10) {
                    if (test == 1) {
                        println("trickleAfterFlow emit($test): ${System.currentTimeMillis()/1000}")
                        emit(test)
                    } else delay(1000)
                    if (test > 1) {
                        println("trickleAfterFlow emit($test): ${System.currentTimeMillis()/1000}")
                        emit (test)
                    } else delay(1000)
                }
            }.trickleAfterFlow(3000).collect { data ->
                println("trickleAfterFlow collect($data): ${System.currentTimeMillis()/1000}")
            }

        }
    }

}