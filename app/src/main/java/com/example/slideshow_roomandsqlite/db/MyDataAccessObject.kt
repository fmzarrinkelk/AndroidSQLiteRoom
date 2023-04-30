package com.example.slideshow_roomandsqlite.db

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.UUID


@Dao // Data Access Object
interface MyDataAccessObject
{
	/**
	 * Person
	 */
	@Query("SELECT `id` FROM PERSON ORDER BY `name` ASC")
	fun fetchPersonIDs(): LiveData<List<UUID>>
	
	@Query("SELECT * FROM PERSON")
	fun fetchPersons(): LiveData<List<Person>>
	
	@Query("SELECT * FROM PERSON WHERE id=(:id)")
	fun fetchPerson(id: UUID): LiveData<Person?>
	
	@Insert
	fun addPerson(person: Person)
	
	@Update
	fun updatePerson(person: Person)
	
	@Query("DELETE FROM PERSON WHERE id=(:id)")
	fun removePerson(id: UUID)
}

