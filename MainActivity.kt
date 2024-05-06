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
import java.lang.reflect.Type


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spinner: Spinner = findViewById<Spinner>(R.id.spinner)
        var start = findViewById<Button>(R.id.start)
        val adapter: ArrayAdapter<*> =
            ArrayAdapter.createFromResource(this, R.array.Options, R.layout.spinner_item)
        spinner.adapter = adapter
        var myIntent: Intent = Intent(this, UploadActivity::class.java)
        var firebase = FirebaseDatabase.getInstance()
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                val options = resources.getStringArray(R.array.Options)
                if (options[position] == "Search for recipe") {
                    myIntent = Intent(this@MainActivity, SearchActivity::class.java)
                } else if (options[position] == "Favorite recipe") {
                    val pref = this@MainActivity.getSharedPreferences(
                        this@MainActivity.packageName + "_preferences", Context.MODE_PRIVATE)
                    if (pref.getString("favRecipe", "") != "") {
                        var recipeName = pref.getString("favRecipe", "")
                        var recipeChild = firebase.reference.child(recipeName!!)
                        RecipeActivity.currentRecipe.setName(recipeName)
                        recipeChild.addListenerForSingleValueEvent (object : ValueEventListener {
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
                        myIntent = Intent(this@MainActivity, RecipeActivity::class.java)
                    } else {
                        Toast.makeText(this@MainActivity,
                            "No favorite recipe. Please pick another option.",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        start.setOnClickListener {
            startActivity(myIntent)
        }
    }
}
