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
        editor.putFloat(name + "Rating", 0f)
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
    fun setIngredients(ingredients: ArrayList<String>) {
        this.ingredients = ingredients
    }
    fun getIngredients(): ArrayList<String> {
        return ingredients
    }
    fun addStep(step: String) {
        instructions.add(step)
    }
    fun setInstructions(instructions: ArrayList<String>) {
        this.instructions = instructions
    }
    fun getInstructions(): ArrayList<String> {
        return instructions
    }
    fun setRating(rating: Float) {
        editor.putFloat(name + "Rating", rating)
        editor.commit()
    }
    fun getRating() : Float {
        return pref.getFloat(name + "Rating", 0f)
    }
    fun setFavorite() {
        editor.putString("favRecipe", name)
        editor.commit()
    }
    fun isFavorite(): Boolean {
        return getFavorite() == name
    }
    fun getFavorite(): String {
        return pref.getString("favRecipe", "")!!
    }
}
