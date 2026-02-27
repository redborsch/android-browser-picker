package com.github.redborsch.browserpicker.data

import com.github.redborsch.browserpicker.R

class BrowserPickerRepository {
    val nonBrowserAppEntry = InternalBrowserData(
        "non-browser-apps",
        R.string.internal_entry_non_browser_apps,
        R.drawable.outline_question_mark_24
    )

    val handlers = buildList(3) {
        add(CopyActionHandler("copy"))
        add(ShareActionHandler("share"))
        // Must always be the last one
        add(InstalledBrowserHandler())
    }

    val handlersActions = handlers
        .asSequence()
        .mapNotNull {
            it.browserData
        }
}
