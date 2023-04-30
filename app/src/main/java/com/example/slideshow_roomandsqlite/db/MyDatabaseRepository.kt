package com.example.slideshow_roomandsqlite.db

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import java.util.*
import java.util.concurrent.Executors

private const val TAG = "MyDatabaseRepository"
private const val DATABASE_NAME = "my-cool-database"

class MyDatabaseRepository constructor(private val fragment: Fragment)
{
	private val database: MyDatabase = Room.databaseBuilder(
		fragment.requireContext().applicationContext,
		MyDatabase::class.java,
		DATABASE_NAME
	)
		.fallbackToDestructiveMigration()
		.build()
	
	//	Data Access Object
	private val myDao = database.myDao()
	
	//	Executor makes it easier to run stuff in a background thread
	private val executor = Executors.newSingleThreadExecutor()
	
	//	Keep track of person IDs
	private var currentPersonIndex: Int = 0
	var currentPersonID: MutableLiveData<UUID?> = MutableLiveData<UUID?>()
	val personIDs: LiveData<List<UUID>> = this.fetchPersonIDs()
	var currentPerson: LiveData<Person?> = MutableLiveData<Person?>()
	
	init {
		this.watchStuff()
	}
	
	private fun watchStuff()
	{
		//
		Log.v(TAG, "watchPersonIDs()")
		
		//
		this.personIDs.observe(this.fragment.viewLifecycleOwner) { ids ->
			Log.v(TAG, "Loaded person IDs: ${ids}")
			this.updateCurrentPersonID()
			this.updateCurrentPerson()
		}
		
		//
		this.currentPerson.observe(this.fragment.viewLifecycleOwner) { person ->
			Log.v(TAG, "Loaded person: $person")
		}
	}
	
	private fun keepCurrentPersonIndexInBounds()
	{
		Log.v(TAG, "keepCurrentPersonIndexInBounds() - Start value: ${this.currentPersonIndex}")
		
		if ( this.personIDs.value == null ) {
			this.currentPersonIndex = 0
		}
		else {
			val ids = this.personIDs.value
			if ( this.currentPersonIndex < 0 ) {
				this.currentPersonIndex = ids!!.size - 1
			}
			else if ( this.currentPersonIndex >= ids!!.size ) {
				this.currentPersonIndex = 0
			}
		}
		
		Log.v(TAG, "keepCurrentPersonIndexInBounds() - End value: ${this.currentPersonIndex}")
	}
	
	fun previousPerson(): Unit
	{
		this.adjustPersonIndex(-1)
	}
	fun nextPerson(): Unit
	{
		this.adjustPersonIndex(1)
	}
	private fun adjustPersonIndex(adjustment: Int)
	{
		this.currentPersonIndex += adjustment
		this.keepCurrentPersonIndexInBounds()
		this.updateCurrentPersonID()
	}
	
	fun updateCurrentPersonID()
	{
		Log.v(TAG, "updateCurrentPersonID()")
		
		this.keepCurrentPersonIndexInBounds()
		
		this.personIDs.value?.let {
			
			if ( this.currentPersonIndex >= 0 && this.currentPersonIndex < it.size ) {
				this.currentPersonID.value = it[this.currentPersonIndex]
				this.updateCurrentPerson()
			}
		}
	}
	
	fun updateCurrentPerson()
	{
		Log.v(TAG, "updateCurrentPerson()")
		
		this.currentPersonID.value?.let { pid ->
			this.currentPerson = this.myDao.fetchPerson(pid)
			this.currentPerson.observe(this.fragment.viewLifecycleOwner) { person ->
				Log.v(TAG, "Loaded person: $person")
			}
		}
	}
	
	fun fetchPersonIDs(): LiveData<List<UUID>> = myDao.fetchPersonIDs()
	fun fetchPersons(): LiveData<List<Person>> = myDao.fetchPersons()
	fun fetchPerson(id: UUID): LiveData<Person?> = myDao.fetchPerson(id)
	fun addPerson(person: Person)
	{
		this.executor.execute {
			this.myDao.addPerson(person)
		}
	}
	fun updatePerson(person: Person)
	{
		this.executor.execute {
			this.myDao.updatePerson(person = person)
		}
	}
	fun removePerson(id: UUID)
	{
		this.executor.execute {
			this.myDao.removePerson(id)
		}
	}
}




