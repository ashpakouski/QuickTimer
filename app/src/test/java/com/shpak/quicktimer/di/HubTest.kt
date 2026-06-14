package com.shpak.quicktimer.di

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class HubTest {

    private interface Dependency
    private class DependencyImpl : Dependency
    private class Service
    private class TrackedInstance(val creationIndex: Int)
    private class NeverRegistered

    @Test
    fun `addInstance returns the same instance every time`() {
        val instance = Service()
        Hub.addInstance(instance)

        assertSame(instance, Hub.get<Service>())
        assertSame(instance, Hub.get<Service>())
    }

    @Test
    fun `addInstance under an interface type is resolvable by the interface`() {
        val impl = DependencyImpl()
        Hub.addInstance<Dependency>(impl)

        assertSame(impl, Hub.get<Dependency>())
    }

    @Test
    fun `addFactory returns a new instance on each get`() {
        Hub.addFactory { Service() }

        val first = Hub.get<Service>()
        val second = Hub.get<Service>()

        assertNotSame(first, second)
    }

    @Test
    fun `addLazyInstance does not invoke the factory until the first get`() {
        val creations = AtomicInteger(0)
        Hub.addLazyInstance { TrackedInstance(creations.incrementAndGet()) }

        assertEquals(0, creations.get())

        Hub.get<TrackedInstance>()

        assertEquals(1, creations.get())
    }

    @Test
    fun `addLazyInstance creates once and caches the same instance`() {
        val creations = AtomicInteger(0)
        Hub.addLazyInstance { TrackedInstance(creations.incrementAndGet()) }

        val first = Hub.get<TrackedInstance>()
        val second = Hub.get<TrackedInstance>()

        assertSame(first, second)
        assertEquals(1, creations.get())
    }

    @Test
    fun `get of an unregistered type throws IllegalStateException`() {
        val error = assertThrows(IllegalStateException::class.java) {
            Hub.get<NeverRegistered>()
        }

        assertTrue(error.message!!.contains("NeverRegistered"))
    }

    @Test
    fun `get operator with a class token resolves the instance`() {
        val instance = Service()
        Hub.addInstance(instance)

        assertSame(instance, Hub[Service::class.java])
    }

    @Test
    fun `re-registering a type overwrites the previous entry`() {
        val first = Service()
        val second = Service()

        Hub.addInstance(first)
        Hub.addInstance(second)

        assertSame(second, Hub.get<Service>())
    }

    @Test
    fun `addLazyInstance creates exactly one instance under concurrent access`() {
        val creations = AtomicInteger(0)
        Hub.addLazyInstance { TrackedInstance(creations.incrementAndGet()) }

        val threadCount = 64
        val startLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(threadCount)
        val results = Collections.synchronizedList(mutableListOf<TrackedInstance>())

        repeat(threadCount) {
            Thread {
                startLatch.await()
                results.add(Hub.get<TrackedInstance>())
                doneLatch.countDown()
            }.start()
        }

        startLatch.countDown()
        assertTrue(doneLatch.await(5, TimeUnit.SECONDS))

        assertEquals(1, creations.get())
        assertEquals(threadCount, results.size)
        val singleton = results.first()
        assertTrue(results.all { it === singleton })
    }
}