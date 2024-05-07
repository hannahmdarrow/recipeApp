package com.example.recipeapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecipeActivity : AppCompatActivity() {
    private lateinit var currentRecipe: Recipe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        var nameView = findViewById<TextView>(R.id.recipeName)
        var ingredientsView = findViewById<TextView>(R.id.ingredients)
        var instructionsView = findViewById<TextView>(R.id.instructions)
        var name = SearchActivity.currentRecipeName
        var ingredients = SearchActivity.currentIngredients
        var instructions = SearchActivity.currentInstructions

        currentRecipe = Recipe(this)
        currentRecipe.setName(name)
        currentRecipe.setIngredients(ingredients)
        currentRecipe.setInstructions(instructions)

        nameView.text = "Recipe Selected: " + name
        var allIngredients = "Ingredients: "
        for (ingredient in ingredients) {
            allIngredients += ingredient
            if (ingredients[ingredients.size - 1] != ingredient)
                allIngredients += ", "
        }
        ingredientsView.text = allIngredients
        var allInstructions = "Instructions:\n"
        for (i in 0..instructions.size-1) {
            allInstructions += "${(i + 1)}. ${instructions[i]}\n"
        }
        instructionsView.text = allInstructions

        //set favorite recipe
        var favRecipe = findViewById<CheckBox>(R.id.checkbox_sample)

        //set rating
        var ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        ratingBar.onRatingBarChangeListener = RatingListener()

        //go back to homepage
        var goBack: Button = findViewById<Button>(R.id.back)
        goBack.setOnClickListener {
            if (favRecipe.isChecked)
                currentRecipe.setFavorite()
            this.finish()
        }

    }

    inner class RatingListener: OnRatingBarChangeListener {
        override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
            currentRecipe.setRating(rating)
        }
    }
}
