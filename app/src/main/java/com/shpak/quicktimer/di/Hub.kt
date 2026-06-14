package com.shpak.quicktimer.di

object Hub {
    private val registry = mutableMapOf<Class<*>, Entry<*>>()
    private val lock = Any()

    inline fun <reified T : Any> addInstance(instance: T) = addInstance(T::class.java, instance)
    fun <T : Any> addInstance(type: Class<T>, instance: T) {
        synchronized(lock) {
            registry[type] = Entry.Instance(instance)
        }
    }

    inline fun <reified T : Any> addFactory(noinline factory: () -> T) = addFactory(T::class.java, factory)
    fun <T : Any> addFactory(type: Class<T>, factory: () -> T) {
        synchronized(lock) {
            registry[type] = Entry.Factory(factory)
        }
    }

    inline fun <reified T : Any> addLazyInstance(noinline factory: () -> T) = addLazyInstance(T::class.java, factory)
    fun <T : Any> addLazyInstance(type: Class<T>, factory: () -> T) {
        synchronized(lock) {
            registry[type] = Entry.LazyInstance(factory)
        }
    }

    inline fun <reified T : Any> get(): T = get(T::class.java)
    operator fun <T : Any> get(type: Class<T>): T {
        val entry = synchronized(lock) {
            registry[type]
        } ?: throw IllegalStateException("${type.simpleName} not found in ${Hub::class.simpleName}")
        @Suppress("UNCHECKED_CAST")
        return (entry as Entry<T>).resolve()
    }

    private sealed class Entry<T> {
        class Instance<T>(val instance: T) : Entry<T>()
        class Factory<T>(val provide: () -> T) : Entry<T>()
        class LazyInstance<T>(val provide: () -> T) : Entry<T>() {
            @Volatile
            private var instance: T? = null

            fun get(): T {
                return instance ?: synchronized(this) {
                    instance ?: provide().also { instance = it }
                }
            }
        }

        fun resolve(): T = when (this) {
            is Instance -> instance
            is Factory -> provide()
            is LazyInstance -> get()
        }
    }
}