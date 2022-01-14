package com.googletutorial.jcounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.googletutorial.jcounter.common.DatabaseHelper
import com.googletutorial.jcounter.common.ItemAdapter
import com.googletutorial.jcounter.counter.CounterFragment

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