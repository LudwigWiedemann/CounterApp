package com.googletutorial.jcounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        // Initialize data.
//        val myDataset = Datasource().loadAffirmations()

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, CounterFragment.newInstance())
//                .commitNow()
//        }
    }
}