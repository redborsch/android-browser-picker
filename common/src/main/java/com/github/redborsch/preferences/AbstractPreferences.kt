package com.github.redborsch.preferences

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.annotation.BoolRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty

typealias PreferenceDelegate<T> = ReadWriteProperty<AbstractPreferences, T>

abstract class AbstractPreferences {

    protected abstract val appContext: Context
    protected abstract val sharedPreferences: SharedPreferences

    fun clear() {
        sharedPreferences.edit(commit = true) {
            clear()
        }
    }

    protected fun booleanPref(
        key: String,
        defaultValue: Boolean
    ): PreferenceDelegate<Boolean> =
        BooleanPreference(SimplePreferenceData(key, defaultValue))

    protected fun booleanPref(
        @StringRes key: Int,
        @BoolRes defaultValue: Int
    ): PreferenceDelegate<Boolean> =
        BooleanPreference(BooleanXmlPreference(key, defaultValue))

    protected fun stringOrNullPref(key: String): PreferenceDelegate<String?> =
        StringOrNullPreference(key)

    protected fun stringPref(
        @StringRes key: Int,
        @StringRes defaultValue: Int,
    ): PreferenceDelegate<String> =
        StringPreference(StringXmlPreference(key, defaultValue))

    protected fun intPref(
        @StringRes key: Int,
        @IntegerRes defaultValue: Int,
    ): PreferenceDelegate<Int> =
        IntegerPreference(IntegerXmlPreference(key, defaultValue))

    internal fun <T> readPreference(preference: AbstractPreference<T>): T =
        preference.run {
            sharedPreferences.read()
        }

    internal fun <T> writePreference(preference: AbstractPreference<T>, value: T) {
        preference.run {
            sharedPreferences.edit {
                write(value)
            }
        }
    }

    private abstract inner class AbstractXmlPreferenceData<T> : SharedPreferenceData<T> {

        override val key: String
            get() = appContext.getString(keyResId)
        override val defaultValue: T
            get() = appContext.resources.retrieveDefaultValue(defaultValueResId)

        @get:StringRes
        protected abstract val keyResId: Int
        protected abstract val defaultValueResId: Int

        protected abstract fun Resources.retrieveDefaultValue(resId: Int): T
    }

    private inner class BooleanXmlPreference(
        override val keyResId: Int,
        @param:BoolRes
        override val defaultValueResId: Int
    ) : AbstractXmlPreferenceData<Boolean>() {
        override fun Resources.retrieveDefaultValue(resId: Int) = getBoolean(resId)
    }

    private inner class StringXmlPreference(
        override val keyResId: Int,
        @param:StringRes
        override val defaultValueResId: Int
    ) : AbstractXmlPreferenceData<String>() {
        override fun Resources.retrieveDefaultValue(resId: Int) = getString(resId)
    }

    private inner class IntegerXmlPreference(
        override val keyResId: Int,
        @param:IntegerRes
        override val defaultValueResId: Int
    ) : AbstractXmlPreferenceData<Int>() {
        override fun Resources.retrieveDefaultValue(resId: Int) = getInteger(resId)
    }
}
