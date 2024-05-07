package com.example.recipeapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var intent : Intent
    private lateinit var database : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spinner: Spinner = findViewById<Spinner>(R.id.spinner)
        var start = findViewById<Button>(R.id.start)
        val adapter: ArrayAdapter<*> =
            ArrayAdapter.createFromResource(this, R.array.Options, R.layout.spinner_item)
        spinner.adapter = adapter
        intent = Intent(this, UploadActivity::class.java)
        database = FirebaseDatabase.getInstance()
        spinner.onItemSelectedListener = SpinnerListener()
        start.setOnClickListener {
            startActivity(intent)
        }
    }

    inner class SpinnerListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            val options = resources.getStringArray(R.array.Options)
            if (options[p2] == "Search for recipe") {
                intent = Intent(this@MainActivity, SearchActivity::class.java)
            } else if (options[p2] == "Favorite recipe") {
                val pref = this@MainActivity.getSharedPreferences(
                    this@MainActivity.packageName + "_preferences", Context.MODE_PRIVATE)
                if (pref.getString("favRecipe", "") != "") {
                    var recipeName = pref.getString("favRecipe", "")
                    var recipeChild = database.reference.child(recipeName!!)
                    SearchActivity.currentRecipeName = recipeName
                    recipeChild.addListenerForSingleValueEvent (object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                SearchActivity.currentIngredients =
                                    snapshot.child("ingredients").value as ArrayList<String>
                                SearchActivity.currentInstructions =
                                    snapshot.child("instructions").value as ArrayList<String>
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                    intent = Intent(this@MainActivity, RecipeActivity::class.java)
                } else {
                    Toast.makeText(this@MainActivity,
                        "No favorite recipe. Please pick another option.",
                        Toast.LENGTH_LONG).show()
                }
            }
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {}

    }
}
