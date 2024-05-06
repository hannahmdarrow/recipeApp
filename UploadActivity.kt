package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class UploadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upload_activity)

        //current recipe
        var recipe = Recipe(this)

        //edit texts
        var recipeEdit = findViewById<EditText>(R.id.recipeName)
        var ingredientEdit = findViewById<EditText>(R.id.ingredient)
        var instructionEdit = findViewById<EditText>(R.id.instruction)

        //buttons
        var addIngredient = findViewById<Button>(R.id.addIngredient)
        var addInstruction = findViewById<Button>(R.id.addInstruction)
        var submit = findViewById<Button>(R.id.submit)
        var back = findViewById<Button>(R.id.back)

        //recipe info
        var name = recipeEdit.text.toString()
        var ingredient = ingredientEdit.text.toString()
        var instruction = instructionEdit.text.toString()

        //add info to recipe
        recipe.setName(name)
        addIngredient.setOnClickListener {
            recipe.addIngredient(ingredient)
            ingredientEdit.setText("", TextView.BufferType.EDITABLE)
        }
        addInstruction.setOnClickListener {
            recipe.addStep(instruction)
            instructionEdit.setText("", TextView.BufferType.EDITABLE)
        }

        //go back to homepage
        back.setOnClickListener { this.finish() }

        //submit recipe
        submit.setOnClickListener {
            //firebase reference
            var recipeReference = FirebaseDatabase.getInstance(
                "https://recipeapp-bbdaf-default-rtdb.firebaseio.com/").reference.child(name)
            recipeReference.child("ingredients").setValue(recipe.getIngredients())
            recipeReference.child("instructions").setValue(recipe.getInstructions())
            this.finish()
        }
    }
}
