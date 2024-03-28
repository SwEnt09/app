package com.github.swent.echo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// This is necessary to make Hilt work
@HiltAndroidApp class EchoApplication : Application()
