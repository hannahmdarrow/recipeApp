package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {

    private lateinit var listView : ListView
    private lateinit var searchBar : SearchView
    private lateinit var searchResult: String
    private var recipes : ArrayList<String> = ArrayList<String>()
    private lateinit var firebase : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        listView = findViewById(R.id.suggestions)
        searchBar = findViewById(R.id.search_bar)
        searchResult = searchBar.query.toString()
        firebase = FirebaseDatabase.getInstance()
        var reference: DatabaseReference = firebase.reference
        reference.addValueEventListener(DataListener())
        var goBack: Button = findViewById<Button>(R.id.back)
        goBack.setOnClickListener {
            this.finish()
        }
        var task : ParseTask = ParseTask( this )
        task.start( )
    }

    fun displayList( recipes : ArrayList<String>? ) {
        if( recipes != null ) {
            var adapter : ArrayAdapter<String> =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, recipes)
            listView.adapter = adapter

            // set up event handling
            var lih : ListItemHandler = ListItemHandler()
            listView.onItemClickListener = lih
        } else {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_LONG).show()
        }
    }

    inner class ListItemHandler : AdapterView.OnItemClickListener {
        override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            var selectedRecipe : String = recipes.get( p2 )
            var reference: DatabaseReference = firebase.reference.child(selectedRecipe)
            RecipeActivity.currentRecipe.setName(selectedRecipe)
            reference.addListenerForSingleValueEvent (object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        RecipeActivity.currentRecipe.setIngredients(
                            snapshot.child("ingredients").value as ArrayList<String>)
                        RecipeActivity.currentRecipe.setInstructions(
                            snapshot.child("instructions").value as ArrayList<String>)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
            var myIntent: Intent = Intent(this@SearchActivity,
                RecipeActivity::class.java)
            startActivity(myIntent)
        }
    }

    inner class ParseTask : Thread {
        private lateinit var activity : SearchActivity
        constructor( activity : SearchActivity ) {
            this.activity = activity
        }
        override fun run() {
            super.run()
            //get list of all recipes depending on what was typed in the search bar
            activity.runOnUiThread { activity.displayList( recipes ) }
        }
    }

    inner class DataListener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (recipe in snapshot.children) {
                if (!recipes.contains(recipe.key))
                    recipes.add(recipe.key!!)
            }
        }
        override fun onCancelled(error: DatabaseError) {

        }
    }
}
