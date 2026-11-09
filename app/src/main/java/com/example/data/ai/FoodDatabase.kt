package com.example.data.ai

import com.example.data.model.FoodItem
import java.util.UUID

object FoodDatabase {
    data class FoodReference(
        val name: String,
        val standardPortion: Float,
        val unit: String,
        val caloriesPer100g: Int,
        val proteinPer100g: Float,
        val carbsPer100g: Float,
        val fatPer100g: Float,
        val fiberPer100g: Float = 0f,
        val sodiumPer100g: Float = 0f,
        val sugarPer100g: Float = 0f,
        val category: String,
        val barcode: String? = null
    ) {
        fun toFoodItem(portionGrams: Float = standardPortion): FoodItem {
            val factor = portionGrams / 100f
            return FoodItem(
                id = UUID.randomUUID().toString(),
                name = name,
                portionGrams = portionGrams,
                portionUnit = unit,
                calories = (caloriesPer100g * factor).toInt(),
                protein = (proteinPer100g * factor),
                carbs = (carbsPer100g * factor),
                fat = (fatPer100g * factor),
                fiber = (fiberPer100g * factor),
                sodium = (sodiumPer100g * factor),
                sugar = (sugarPer100g * factor),
                confidence = 0.98f
            )
        }
    }

    private val _customItems = mutableListOf<FoodReference>()
    val items: List<FoodReference> get() = defaultItems + _customItems

    fun addCustomFood(food: FoodReference) {
        _customItems.add(0, food)
    }

    private val defaultItems = listOf(
        // Proteins
        FoodReference("Grilled Chicken Breast", 150f, "g", 165, 31.0f, 0.0f, 3.6f, 0f, 74f, 0f, "Meat & Poultry", "012345678901"),
        FoodReference("Salmon Fillet (Pan-seared)", 170f, "g", 206, 22.0f, 0.0f, 12.0f, 0f, 60f, 0f, "Fish & Seafood", "012345678902"),
        FoodReference("Large Boiled Egg", 50f, "egg", 143, 12.6f, 0.7f, 9.5f, 0f, 142f, 0.4f, "Eggs & Dairy", "012345678903"),
        FoodReference("Scrambled Eggs (with butter)", 120f, "g", 148, 10.0f, 1.5f, 11.0f, 0f, 180f, 1.0f, "Eggs & Dairy"),
        FoodReference("Greek Yogurt (0% Fat)", 170f, "g", 59, 10.2f, 3.6f, 0.4f, 0f, 36f, 3.2f, "Eggs & Dairy", "012345678904"),
        FoodReference("Whey Protein Shake (1 scoop)", 30f, "scoop", 380, 78.0f, 6.0f, 4.0f, 1.5f, 140f, 3f, "Supplements", "012345678905"),
        FoodReference("Tofu (Firm)", 120f, "g", 83, 10.0f, 2.0f, 5.0f, 1.0f, 10f, 0.5f, "Plant Protein"),
        FoodReference("Lean Ground Beef 90/10", 150f, "g", 176, 26.0f, 0.0f, 8.0f, 0f, 65f, 0f, "Meat & Poultry"),
        FoodReference("Canned Tuna in Water", 110f, "can", 116, 26.0f, 0.0f, 1.0f, 0f, 300f, 0f, "Fish & Seafood"),

        // Carbohydrates & Grains
        FoodReference("Cooked Brown Rice", 150f, "g", 123, 2.7f, 25.6f, 1.0f, 1.8f, 5f, 0.2f, "Grains", "012345678906"),
        FoodReference("Cooked Jasmine White Rice", 150f, "g", 130, 2.4f, 28.2f, 0.3f, 0.4f, 1f, 0.1f, "Grains"),
        FoodReference("Rolled Oatmeal (Cooked)", 200f, "bowl", 71, 2.5f, 12.0f, 1.5f, 1.7f, 49f, 0.3f, "Grains"),
        FoodReference("Whole Wheat Bread", 40f, "slice", 247, 13.0f, 41.0f, 3.4f, 6.0f, 450f, 4.0f, "Bakery", "012345678907"),
        FoodReference("Sourdough Bread", 50f, "slice", 260, 9.0f, 52.0f, 1.5f, 2.5f, 500f, 1.0f, "Bakery"),
        FoodReference("Baked Sweet Potato", 180f, "medium", 90, 2.0f, 20.7f, 0.1f, 3.3f, 36f, 6.5f, "Vegetables"),
        FoodReference("Cooked Quinoa", 150f, "cup", 120, 4.4f, 21.3f, 1.9f, 2.8f, 7f, 0.9f, "Grains"),
        FoodReference("Whole Wheat Pasta", 140f, "cup", 124, 5.3f, 26.5f, 0.5f, 3.9f, 4f, 0.8f, "Grains"),

        // Vegetables
        FoodReference("Steamed Broccoli Florets", 100f, "g", 35, 2.4f, 7.2f, 0.4f, 2.6f, 33f, 1.4f, "Vegetables"),
        FoodReference("Cherry Tomatoes", 60f, "g", 18, 0.9f, 3.9f, 0.2f, 1.2f, 5f, 2.6f, "Vegetables"),
        FoodReference("Fresh Baby Spinach", 50f, "cup", 23, 2.9f, 3.6f, 0.4f, 2.2f, 79f, 0.4f, "Vegetables"),
        FoodReference("Roasted Asparagus", 100f, "g", 40, 2.5f, 4.5f, 2.0f, 2.0f, 90f, 1.5f, "Vegetables"),
        FoodReference("Sliced Cucumbers", 80f, "cup", 15, 0.7f, 3.6f, 0.1f, 0.5f, 2f, 1.7f, "Vegetables"),
        FoodReference("Mixed Green Salad", 75f, "bowl", 17, 1.2f, 3.2f, 0.2f, 1.8f, 25f, 1.2f, "Vegetables"),

        // Healthy Fats & Nuts
        FoodReference("Fresh Avocado Slices", 60f, "g", 160, 2.0f, 8.5f, 14.7f, 6.7f, 7f, 0.7f, "Healthy Fats"),
        FoodReference("Extra Virgin Olive Oil", 14f, "tbsp", 884, 0.0f, 0.0f, 100.0f, 0f, 2f, 0f, "Oils & Dressings"),
        FoodReference("Creamy Peanut Butter", 32f, "tbsp", 588, 25.0f, 20.0f, 50.0f, 6.0f, 420f, 9.0f, "Nuts & Spreads", "012345678908"),
        FoodReference("Raw Almonds", 28f, "handful", 579, 21.0f, 22.0f, 50.0f, 12.5f, 1f, 4.4f, "Nuts & Spreads"),
        FoodReference("Chia Seeds", 15f, "tbsp", 486, 17.0f, 42.0f, 31.0f, 34.0f, 16f, 0f, "Seeds"),

        // Fruits
        FoodReference("Medium Banana", 118f, "item", 89, 1.1f, 22.8f, 0.3f, 2.6f, 1f, 12.2f, "Fruits"),
        FoodReference("Fresh Blueberries", 80f, "cup", 57, 0.7f, 14.5f, 0.3f, 2.4f, 1f, 10.0f, "Fruits"),
        FoodReference("Crisp Gala Apple", 180f, "medium", 52, 0.3f, 13.8f, 0.2f, 2.4f, 1f, 10.4f, "Fruits"),
        FoodReference("Strawberries", 100f, "cup", 32, 0.7f, 7.7f, 0.3f, 2.0f, 1f, 4.9f, "Fruits"),

        // Prepared Meals & Dishes
        FoodReference("Chicken Burrito Bowl", 380f, "bowl", 165, 12.0f, 18.0f, 5.0f, 3.5f, 420f, 1.5f, "Prepared Meals"),
        FoodReference("Margherita Pizza Slice", 105f, "slice", 266, 11.0f, 33.0f, 10.0f, 2.3f, 590f, 3.6f, "Prepared Meals"),
        FoodReference("Cheeseburger (Single)", 180f, "burger", 270, 15.0f, 24.0f, 13.0f, 1.5f, 650f, 5.0f, "Fast Food"),
        FoodReference("Masala Dosa with Sambar", 220f, "serving", 180, 4.5f, 30.0f, 5.0f, 3.0f, 380f, 2.0f, "Indian Dishes"),
        FoodReference("Chicken Biryani (Dum)", 300f, "plate", 185, 9.5f, 24.0f, 5.5f, 1.8f, 480f, 1.2f, "Indian Dishes"),
        FoodReference("Paneer Tikka Masala", 200f, "serving", 210, 8.5f, 9.0f, 16.0f, 2.0f, 490f, 3.5f, "Indian Dishes"),
        FoodReference("Chicken Caesar Salad", 250f, "bowl", 130, 11.0f, 6.0f, 7.0f, 2.0f, 390f, 2.0f, "Prepared Meals"),
        FoodReference("Sushi Salmon Roll (6 pcs)", 180f, "roll", 155, 6.5f, 28.0f, 2.5f, 1.2f, 320f, 3.0f, "Japanese"),

        // Drinks
        FoodReference("Black Coffee (Americano)", 240f, "cup", 2, 0.1f, 0.0f, 0.0f, 0f, 5f, 0f, "Beverages"),
        FoodReference("Caffe Latte (Whole Milk)", 300f, "cup", 45, 2.8f, 3.8f, 2.0f, 0f, 45f, 3.8f, "Beverages"),
        FoodReference("Green Tea (Unsweetened)", 240f, "cup", 1, 0.0f, 0.2f, 0.0f, 0f, 2f, 0f, "Beverages")
    )
}
