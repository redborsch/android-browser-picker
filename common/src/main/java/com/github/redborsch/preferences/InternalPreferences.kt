package com.github.redborsch.preferences

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface SharedPreferenceData<T> {
    val key: String
    val defaultValue: T
}

class SimplePreferenceData<T>(
    override val key: String,
    override val defaultValue: T
) : SharedPreferenceData<T>

abstract class AbstractPreference<T> : ReadWriteProperty<AbstractPreferences, T>, SharedPreferenceData<T> {

    abstract fun SharedPreferences.read(): T
    abstract fun SharedPreferences.Editor.write(value: T)

    final override fun getValue(
        thisRef: AbstractPreferences,
        property: KProperty<*>
    ): T = thisRef.readPreference(this)

    final override fun setValue(
        thisRef: AbstractPreferences,
        property: KProperty<*>,
        value: T
    ) {
        thisRef.writePreference(this, value)
    }
}

class BooleanPreference(data: SharedPreferenceData<Boolean>) :
    AbstractPreference<Boolean>(),
    SharedPreferenceData<Boolean> by data {

    override fun SharedPreferences.read(): Boolean =
        getBoolean(key, defaultValue)

    override fun SharedPreferences.Editor.write(value: Boolean) {
        putBoolean(key, value)
    }
}

class StringOrNullPreference(
    override val key: String,
) : AbstractPreference<String?>() {

    override val defaultValue: String? get() = null

    override fun SharedPreferences.read(): String? =
        getString(key, defaultValue)

    override fun SharedPreferences.Editor.write(value: String?) {
        putString(key, value)
    }
}

class StringPreference(data: SharedPreferenceData<String>) :
    AbstractPreference<String>(),
    SharedPreferenceData<String> by data {

    override fun SharedPreferences.read(): String =
        // According to the contract, when default value is non-null - we should get non-null result
        getString(key, defaultValue)!!

    override fun SharedPreferences.Editor.write(value: String) {
        putString(key, value)
    }
}
