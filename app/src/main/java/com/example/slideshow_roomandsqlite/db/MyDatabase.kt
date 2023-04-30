package com.example.slideshow_roomandsqlite.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
	entities = [
		Person::class
	],
	version=4
)
@TypeConverters(MyTypeConverters::class)
abstract class MyDatabase : RoomDatabase()
{
	abstract fun myDao(): MyDataAccessObject
}

