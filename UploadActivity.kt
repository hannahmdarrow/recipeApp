package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UploadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        //current recipe
        var recipe = Recipe(this)

        //edit texts
        var nameEdit = findViewById<EditText>(R.id.recipeName)
        var ingredientEdit = findViewById<EditText>(R.id.ingredient)
        var instructionEdit = findViewById<EditText>(R.id.instruction)

        //buttons
        var addIngredient = findViewById<Button>(R.id.addIngredient)
        var addInstruction = findViewById<Button>(R.id.addInstruction)
        var submit = findViewById<Button>(R.id.submit)
        var back = findViewById<Button>(R.id.back)

        //add info to recipe
        addIngredient.setOnClickListener {
            var ingredient = ingredientEdit.text.toString()
            recipe.addIngredient(ingredient)
            ingredientEdit.setText("", TextView.BufferType.EDITABLE)
        }
        addInstruction.setOnClickListener {
            var instruction = instructionEdit.text.toString()
            recipe.addStep(instruction)
            instructionEdit.setText("", TextView.BufferType.EDITABLE)
        }

        //go back to homepage
        back.setOnClickListener { this.finish() }

        //submit recipe
        submit.setOnClickListener {
            //firebase reference
            var name = nameEdit.text.toString()
            recipe.setName(name)
            var database = FirebaseDatabase.getInstance()
            var reference : DatabaseReference = database.reference
            reference.child(name).child("ingredients").setValue(recipe.getIngredients())
            reference.child(name).child("instructions").setValue(recipe.getInstructions())
            this.finish()
        }
    }
}
