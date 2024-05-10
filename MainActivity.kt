package com.example.recipeapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var intent : Intent
    private lateinit var database : FirebaseDatabase
    private lateinit var adView : AdView

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

        //MobileAds.initialize(this) {}
        // create an AdView
        adView = AdView( this )
        var adSize : AdSize = AdSize( AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT )
        adView.setAdSize( adSize )
        var adUnitId : String = "ca-app-pub-3940256099942544/6300978111"
        adView.adUnitId = adUnitId
        // create an AdRequest
        var builder : AdRequest.Builder = AdRequest.Builder( )
        builder.addKeyword( "food" ).addKeyword( "recipe" )
        var request : AdRequest = builder.build()
        // put the AdView in the LinearLayout
        var adLayout : LinearLayout = findViewById( R.id.ad_view )
        adLayout.addView( adView )
        // load the ad
        adView.loadAd( request )
        adView.bringToFront()
    }

    inner class SpinnerListener : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            val options = resources.getStringArray(R.array.Options)

            if (options[p2] == "Search for recipe") {
                intent = Intent(this@MainActivity, SearchActivity::class.java)
            }

            else if (options[p2] == "Favorite recipe") {

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

                }

                else {
                    Toast.makeText(this@MainActivity,
                        "No favorite recipe. Please pick another option.",
                        Toast.LENGTH_LONG).show()
                }
            }
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {}

    }

    override fun onPause( ) {
        if( adView != null )
            adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if( adView != null )
            adView.resume()
    }

    override fun onDestroy() {
        if( adView != null )
            adView.destroy( )
        super.onDestroy()
    }
}
