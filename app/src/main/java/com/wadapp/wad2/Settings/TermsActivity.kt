package com.wadapp.lsm.wad.Settings


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wadapp.lsm.wad.R
import kotlinx.android.synthetic.main.snippet_top_usernamebar.*

class TermsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)


        ivBackArrow.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

    }

}