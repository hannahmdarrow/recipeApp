package com.example.recipeapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    //speech recognition stuff
    private lateinit var launcher : ActivityResultLauncher<String>
    private var permission : String = Manifest.permission.RECORD_AUDIO
    private lateinit var speechRecognizer : SpeechRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        listView = findViewById(R.id.suggestions)
        searchBar = findViewById(R.id.search_bar)
        searchResult = searchBar.query.toString()
        firebase = FirebaseDatabase.getInstance()
        pref = this.getSharedPreferences(this.packageName + "_preferences",
            Context.MODE_PRIVATE)
        editor = pref.edit()

        var reference: DatabaseReference = firebase.reference
        reference.addValueEventListener(DataListener())

        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var newRecipes = ArrayList<String>()
                for (recipe in recipes)
                    if (recipe.contains(newText!!, true))
                        newRecipes.add(recipe)
                displayList(newRecipes)
                return false
            }
        })

        var goBack: Button = findViewById<Button>(R.id.back)
        goBack.setOnClickListener {
            this.finish()
        }

        //more speech recognition stuff
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer( this )

        var permissionStatus : Int = checkSelfPermission( permission )
        if( permissionStatus == PackageManager.PERMISSION_GRANTED ) {
            listen( )
        } else {
            var contract : ActivityResultContracts.RequestPermission =
                ActivityResultContracts.RequestPermission( )
            var results : Results = Results( )
            launcher = registerForActivityResult( contract, results )
            launcher.launch( permission )

        }

        var task : ParseTask = ParseTask( this )
        task.start( )

    }

    fun listen( ) {
        // use the speech recognizer
        var speechResults : SpeechResults = SpeechResults( )
        speechRecognizer.setRecognitionListener( speechResults )
        var speechIntent : Intent = Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH )
        speechIntent.putExtra( RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US" )
        speechRecognizer.startListening( speechIntent )

    }

    inner class Results : ActivityResultCallback<Boolean> {
        override fun onActivityResult(result: Boolean) {
            if( result ) {
                // start using speech recognizer
                listen( )
            } else {
                Log.w( "MainActivity", "Permission not granted" )
            }
        }
    }

    inner class SpeechResults : RecognitionListener {
        override fun onReadyForSpeech(p0: Bundle?) {}

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(p0: Float) {}

        override fun onBufferReceived(p0: ByteArray?) {}

        override fun onEndOfSpeech() {}

        override fun onError(p0: Int) {
            listen( )
        }

        override fun onResults(p0: Bundle?) {
            if( p0 != null ) {
                var words: ArrayList<String>? =
                    p0.getStringArrayList( SpeechRecognizer.RESULTS_RECOGNITION )
                var scores : FloatArray? =
                    p0.getFloatArray( SpeechRecognizer.CONFIDENCE_SCORES )
                if( words != null ) {
                    var voiceSearch = words.joinToString(separator = " ")
                    searchBar.setQuery(voiceSearch, false)
                    searchBar.clearFocus()
                }
            }
            listen()
        }

        override fun onPartialResults(p0: Bundle?) {}

        override fun onEvent(p0: Int, p1: Bundle?) {}

    }

    fun displayList( recipes : ArrayList<String>? ) {
        if( recipes != null ) {
            var adapter : ArrayAdapter<String> =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, recipes)
            listView.adapter = adapter

            // set up event handling
            var lih = ListItemHandler()
            listView.onItemClickListener = lih
        } else {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_LONG).show()
        }
    }

    inner class ListItemHandler : AdapterView.OnItemClickListener {
        override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            var selectedRecipe : String = recipes[p2]
            var reference: DatabaseReference = firebase.reference.child(selectedRecipe)
            currentRecipeName = selectedRecipe
            reference.addListenerForSingleValueEvent (object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        currentIngredients =
                            snapshot.child("ingredients").value as ArrayList<String>
                        currentInstructions =
                            snapshot.child("instructions").value as ArrayList<String>

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
    companion object {
        var currentRecipeName: String = ""
        var currentIngredients: ArrayList<String> = ArrayList<String>()
        var currentInstructions: ArrayList<String> = ArrayList<String>()
    }
}
