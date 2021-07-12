package com.hzlgrn.pdxrail

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ChannelUnitTest {

    @ExperimentalCoroutinesApi
    @Test
    fun testChannel() {
        runBlocking {
            val channel = Channel<Int>()
            launch {
                for (signal in 1..10) {
                    channel.send(signal)
                }
                channel.close()
            }
            channel.consumeEach { println(it)  }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testProduce() {
        runBlocking {
            val channel = produce<Int> {
                (0..10).forEach {
                    send(it)
                }
            }
            channel.consumeEach { println(it)  }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testTrickleChannel() {
        runBlocking {

            fun <E> ReceiveChannel<E>.debounce(upstreamAfterMs: Long = 0): ReceiveChannel<E> =
                produce {
                    var lastUpstreamAt = System.currentTimeMillis()
                    consumeEach { upstream ->
                        val currentTime = System.currentTimeMillis()
                        lastUpstreamAt = if (currentTime - lastUpstreamAt > upstreamAfterMs) {
                            send(upstream)
                            currentTime // lastUpstreamAt = currentTime
                        } else {
                            lastUpstreamAt // lastUpstreamAt = lastUpstreamAt
                        }
                    }
                }

            val channel = produce<Int> {
                (0..100).forEach {
                    send(it)
                    delay(100)
                }
            }
            channel.debounce(1000).consumeEach { println("channelDebounce: $it: ${System.currentTimeMillis()/1000}")  }
        }
    }

}