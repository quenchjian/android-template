package me.quenchjian

import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp(MultiDexApplication::class)
class App: Hilt_App() {
}
