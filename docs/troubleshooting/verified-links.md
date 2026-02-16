# Browser Picker doesn't show the browser menu when clicking on some links

The truth is, Browser Picker might not even be launched by the system for certain links. For example, for Reddit, when you have Reddit app installed. This is because Google allows apps to [verify links they handle](https://developer.android.com/training/app-links/verify-applinks). After doing that an app will be exclusively opening its verified links. Browser Picker is not even invoked in such cases. At least by default.

The good news is that this behavior can be easily disabled - please see the video below.

https://github.com/user-attachments/assets/ab4fed21-9ea1-44a2-9412-8a7e9b5110c4

Video not displaying? [Try this](https://github.com/user-attachments/assets/ab4fed21-9ea1-44a2-9412-8a7e9b5110c4).

Please note that you'd need to do this for each application that handles certain link types.

Step-by-step instruction:

1. Go to Android settings.
2. Find application settings (usually called **Apps**).
3. Choose your application from the list.
4. Select **Open by default**.
5. Choose **In your browser**.
