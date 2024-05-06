package com.example.recipeapp

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_activity)
        context = this
        var nameView = findViewById<TextView>(R.id.recipeName)
        var ingredientsView = findViewById<TextView>(R.id.ingredients)
        var instructionsView = findViewById<TextView>(R.id.instructions)
        var name = currentRecipe.getName()
        var ingredients = currentRecipe.getIngredients()
        var instructions = currentRecipe.getInstructions()
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
    }
    companion object {
        lateinit var context: Context
        var currentRecipe: Recipe = Recipe(context)
    }
}
