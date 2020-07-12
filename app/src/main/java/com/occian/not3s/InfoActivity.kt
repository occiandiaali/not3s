package com.occian.not3s

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val bundle:Bundle? = intent.extras
        val actionBar = supportActionBar

        val infoLabel = bundle?.getString("info")
        actionBar?.title = infoLabel
    } // on create
} // class
