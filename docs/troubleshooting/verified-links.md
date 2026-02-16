# Browser Picker doesn't show the browser menu when clicking on some links

The truth is, Browser Picker might not even launch for certain links. For example, for Reddit, when you have Reddit app installed. This is because Google allows apps to [verify links they handle](https://developer.android.com/training/app-links/verify-applinks) and this makes verified app the only choice to open its links. Browser Picker is not even invoked in such cases.

The good news is that it can be easily disabled - please see the video below.



Please note that you'd need to do this for each application that handles certain link types.