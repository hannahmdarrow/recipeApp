package com.example.recipeapp

import android.app.SearchManager
import android.content.Intent
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {

    private lateinit var database : FirebaseDatabase
    private lateinit var ref : DatabaseReference
    private lateinit var searchbar: SearchView
    private var search : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        database = FirebaseDatabase.getInstance()
        searchbar = findViewById<SearchView>(R.id.search_bar)
        var listener : QueryListener = QueryListener()
        searchbar.setOnQueryTextListener(listener)
    }

    fun listen() {
        ref = database.getReference(search)
        var listener : DataListener = DataListener()
        ref.addValueEventListener( listener )
    }

    fun displayRecipe() {
        var intent : Intent = Intent(this, RecipeActivity::class.java)
        startActivity(intent)
    }

    fun saveTitle() {

    }

    inner class QueryListener : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(p0: String): Boolean {
            search = p0
            listen()
            return false
        }

        override fun onQueryTextChange(p0: String): Boolean {
            return false
        }
    }

    inner class DataListener : ValueEventListener {
        override fun onDataChange(snapshot : DataSnapshot) {
            var key : String? = snapshot.key
            var valueObject : Any? = snapshot.value
            if(valueObject != null) {
                Toast.makeText(this@SearchActivity, "Accessing $key recipe", Toast.LENGTH_LONG).show()
                displayRecipe()
            } else {
                Toast.makeText(this@SearchActivity, "Not found", Toast.LENGTH_LONG).show()
            }
        }

        override fun onCancelled( error : DatabaseError ) {
            Toast.makeText(this@SearchActivity, "Not found", Toast.LENGTH_LONG).show()
        }
    }
}