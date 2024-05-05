package com.example.recipeapp

import android.content.Context
import android.content.SharedPreferences

class Recipe {
    private var name: String = ""
    private var ingredients: ArrayList<String> = ArrayList<String>()
    private var instructions: ArrayList<String> = ArrayList<String>()
    private var context: Context
    private var pref: SharedPreferences
    private var editor: SharedPreferences.Editor
    constructor(context: Context) {
        this.context = context
        pref = context.getSharedPreferences(context.packageName + "_preferences",
            Context.MODE_PRIVATE)
        editor = pref.edit()
        editor.putInt("rating", 0)
        editor.putString("favRecipe", "")
        editor.commit()
    }
    fun setName(name: String) {
        this.name = name
    }
    fun getName() : String {
        return name
    }
    fun addIngredient(ingredient: String) {
        ingredients.add(ingredient)
    }
    fun getIngredients(): ArrayList<String> {
        return ingredients
    }
    fun addStep(step: String) {
        instructions.add(step)
    }
    fun getInstructions(): ArrayList<String> {
        return instructions
    }
    fun setRating(rating: Int) {
        editor.putInt("rating", rating)
        editor.commit()
    }
    fun getRating() : Int {
        return pref.getInt("rating", 0)
    }
    fun setFavorite() {
        editor.putString("favRecipe", name)
        editor.commit()
    }
    fun isFavorite(): Boolean {
        return pref.getString("favRecipe", "") == name
    }
}
