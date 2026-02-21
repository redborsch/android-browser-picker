package com.github.redborsch.preferences

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal interface SharedPreferenceData<T> {
    val key: String
    val defaultValue: T
}

internal class SimplePreferenceData<T>(
    override val key: String,
    override val defaultValue: T
) : SharedPreferenceData<T>

internal abstract class AbstractPreference<T> : ReadWriteProperty<AbstractPreferences, T>, SharedPreferenceData<T> {

    abstract fun SharedPreferences.read(key: String, defaultValue: T): T
    abstract fun SharedPreferences.Editor.write(key: String, value: T)

    override fun getValue(
        thisRef: AbstractPreferences,
        property: KProperty<*>
    ): T = thisRef.readPreference(this)

    override fun setValue(
        thisRef: AbstractPreferences,
        property: KProperty<*>,
        value: T
    ) {
        thisRef.writePreference(this, value)
    }
}

internal class BooleanPreference(data: SharedPreferenceData<Boolean>) :
    AbstractPreference<Boolean>(),
    SharedPreferenceData<Boolean> by data {

    override fun SharedPreferences.read(key: String, defaultValue: Boolean): Boolean =
        getBoolean(key, defaultValue)

    override fun SharedPreferences.Editor.write(key: String, value: Boolean) {
        putBoolean(key, value)
    }
}

internal class StringOrNullPreference(
    override val key: String,
) : AbstractPreference<String?>() {

    override val defaultValue: String? get() = null

    override fun SharedPreferences.read(key: String, defaultValue: String?): String? =
        getString(key, defaultValue)

    override fun SharedPreferences.Editor.write(key: String, value: String?) {
        putString(key, value)
    }
}

internal class StringPreference(data: SharedPreferenceData<String>) :
    AbstractPreference<String>(),
    SharedPreferenceData<String> by data {

    override fun SharedPreferences.read(key: String, defaultValue: String): String =
        // According to the contract, when default value is non-null - we should get non-null result
        getString(key, defaultValue)!!

    override fun SharedPreferences.Editor.write(key: String, value: String) {
        putString(key, value)
    }
}
