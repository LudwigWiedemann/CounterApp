package com.googletutorial.jcounter.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class CoroutineFactory {
    fun getIoContext() = Job() + Dispatchers.IO
}