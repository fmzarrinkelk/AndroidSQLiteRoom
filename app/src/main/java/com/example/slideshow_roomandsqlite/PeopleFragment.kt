package com.example.slideshow_roomandsqlite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.slideshow_roomandsqlite.db.MyDatabaseRepository
import com.example.slideshow_roomandsqlite.db.Person
import java.lang.Exception

private const val TAG = "PeopleFragment"

class PeopleFragment : Fragment()
{
	private lateinit var prevButton: Button
	private lateinit var createButton: Button
	private lateinit var updateButton: Button
	private lateinit var deleteButton: Button
	private lateinit var nextButton: Button
	
	private lateinit var personName: EditText
	private lateinit var personAge: EditText
	private lateinit var personFavoriteColor: EditText
	
	private lateinit var dbRepo: MyDatabaseRepository
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View?
	{
		Log.v(TAG, "People fragment: onCreateView()")
		
		//	Do this here instead of in onCreate
		this.dbRepo = MyDatabaseRepository(this)
		
		val view = inflater.inflate(R.layout.fragment_people, container, false)
		
		this.personName = view.findViewById(R.id.input_person_name)
		this.personAge = view.findViewById(R.id.input_person_age)
		this.personFavoriteColor = view.findViewById(R.id.input_person_favorite_color)
		
		this.prevButton = view.findViewById(R.id.button_prev_person)
		this.createButton = view.findViewById(R.id.button_create_person)
		this.updateButton = view.findViewById(R.id.button_update_person)
		this.deleteButton = view.findViewById(R.id.button_delete_person)
		this.nextButton = view.findViewById(R.id.button_next_person)
		
		this.setupCallbacks()
		this.setupObservers()
		
		return view
	}
	
	private fun viewsToPerson(): Person
	{
		val p = Person()
		
		p.name = this.personName.text.toString()
		try { p.age = this.personAge.text.toString().toInt() }
		catch(e: Exception){}
		p.color = this.personFavoriteColor.text.toString()
		
		Log.v(TAG, "viewsToPerson() - $p")
		
		return p
	}
	
	private fun personToViews(person: Person?)
	{
		if ( person == null ) {
			this.personName.setText("")
			this.personAge.setText("")
			this.personFavoriteColor.setText("")
		}
		else {
			this.personName.setText(person.name)
			this.personAge.setText(person.age.toString())
			this.personFavoriteColor.setText(person.color)
		}
	}
	
	private fun setupCallbacks()
	{
		this.prevButton.setOnClickListener {
			this.dbRepo.previousPerson()
		}
		this.nextButton.setOnClickListener {
			this.dbRepo.nextPerson()
		}
		
		this.createButton.setOnClickListener {
			val p = this.viewsToPerson()
			Log.v(TAG, "Creating a person: $p")
			this.dbRepo.addPerson(p)
		}
		
		this.updateButton.setOnClickListener {
			this.dbRepo.currentPersonID.value?.let { id ->
				val p = this.viewsToPerson()
				p.id = id
				Log.v(TAG, "Updating a person to: $p")
				this.dbRepo.updatePerson(p)
			}
		}
		
		this.deleteButton.setOnClickListener {
			
			Log.v(TAG, "Deleting current person")
			
			this.dbRepo.currentPersonID.value?.let { id ->
				this.dbRepo.removePerson(id)
			}
		}
	}
	
	private fun setupObservers()
	{
		this.dbRepo.currentPersonID.observe(this.viewLifecycleOwner) { idq ->
			
			Log.v(TAG, "The current person ID has changed: $idq")
			
			if ( idq == null ) {
				this.personToViews(null)
			}
			
			idq?.let { id ->
				
				val personLiveData = this.dbRepo.fetchPerson(id)
				personLiveData.observe(this.viewLifecycleOwner) { person ->
					
					Log.v(TAG,"New person: $person")
					
					this.personToViews(person)
				}
			}
		}
	}
}










