package com.example.slideshow_roomandsqlite.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "PERSON")
data class Person(
	
	// False to autogenerate, because the UUID class will itself autogenerate for us
	@PrimaryKey(autoGenerate = false)
	@ColumnInfo(name = "id")
	var id: UUID = UUID.randomUUID(),	// Use VAR not VAL, despite the book. Val would give us "undefined setter" build errors
	
	@ColumnInfo(name = "name")
	var name: String = "",
	
	@ColumnInfo(name = "age")
	var age: Int = 0,
	
	@ColumnInfo(name = "color")
	var color: String = ""
)

