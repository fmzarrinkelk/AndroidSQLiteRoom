package com.example.slideshow_roomandsqlite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		val peopleFragment = this.supportFragmentManager.findFragmentById(R.id.fragment_persons)
		if ( peopleFragment == null ) {
			
			val fragment = PeopleFragment()
			this.supportFragmentManager
				.beginTransaction()
				.add(R.id.fragment_persons, fragment)
				.commit()
		}
	}
}
