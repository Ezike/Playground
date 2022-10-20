import Menu.Configuration

interface Menu {
    val recipes: List<Recipe>
    val selectedRecipes: List<Recipe>
    val selectedRecipeCount: Int
    fun selectRecipe(vararg ids: Int): Result<String>
    fun unselectRecipe(vararg ids: Int): Result<String>
    fun getRecipesWithTag(tag: String): List<Recipe>

    interface Configuration {
        val minimumSelection: Int

        companion object
    }

    companion object {
        operator fun invoke(
            recipes: List<Recipe>,
            subscription: Subscription
        ): Menu = DefaultMenu(recipes, subscription)
    }
}

fun Configuration.Companion.Default(isFamily: Boolean): Configuration =
    object : Configuration {
        override val minimumSelection: Int
            get() = if (isFamily) 5 else 3
    }

interface MenuOperation : Menu {

    companion object {
        operator fun invoke(
            recipes: List<Recipe>,
            subscription: Subscription
        ): MenuOperation {
            val configuration = Configuration.Default(subscription.isFamily)
            return MenuInteractor(recipes, RecipeValidatorImpl(configuration))
        }
    }
}

private data class DefaultMenu(
    override val recipes: List<Recipe>,
    private val subscription: Subscription
) : Menu by MenuOperation(recipes, subscription)

fun main() {
    val menu = Menu(
        recipes = listOf(Recipe(1, "Menu", listOf("hot"))),
        subscription = Subscription(1, 1, true)
    )
    println(menu)
    println(menu.selectRecipe(1))
    println(menu.selectRecipe(1, 2, 3, 4))
    println(menu.selectedRecipes)
    println(menu.unselectRecipe(1))
    println(menu.unselectRecipe(1))
    println(menu.getRecipesWithTag("hot"))
    println(menu.selectedRecipes)
}

class MenuInteractor(
    override val recipes: List<Recipe>,
    private val validator: RecipeValidator
) : MenuOperation {

    private data class RecipeModel(
        val recipe: Recipe,
        val isSelected: Boolean
    )

    private val recipeMap = recipes
        .associate { recipe -> recipe.id to RecipeModel(recipe, false) }
        .toMutableMap()

    override val selectedRecipes: List<Recipe>
        get() = recipeMap.values.filter { it.isSelected }.map { it.recipe }

    override val selectedRecipeCount: Int
        get() = recipeMap.values.count { it.isSelected }

    override fun selectRecipe(vararg ids: Int): Result<String> =
        validator
            .validateSelection(
                ids = ids.toList(),
                recipeIds = recipes.map { it.id },
                unselectedRecipeIds = recipeMap.values.filter {
                    !it.isSelected
                }.map { it.recipe.id }
            ).mapCatching { select(*ids) }

    override fun unselectRecipe(vararg ids: Int): Result<String> =
        validator
            .validateUnSelection(
                ids = ids.toList(),
                recipeIds = recipes.map { it.id },
                selectedRecipeIds = recipeMap.values.filter {
                    it.isSelected
                }.map { it.recipe.id }
            ).mapCatching { unselect(*ids) }

    override fun getRecipesWithTag(tag: String): List<Recipe> =
        recipeMap.values.map { it.recipe }.filter { it.tags.contains(tag) }

    private fun select(vararg ids: Int): String {
        ids.forEach { recipeId ->
            recipeMap[recipeId] =
                recipeMap[recipeId]!!.copy(isSelected = true)
        }
        return success(ids.size, "Selected")
    }

    private fun unselect(vararg ids: Int): String {
        ids.forEach { recipeId ->
            recipeMap[recipeId] =
                recipeMap[recipeId]!!.copy(isSelected = false)
        }
        return success(ids.size, "Unselected")
    }

    private fun success(count: Int, status: String): String {
        val text = if (count > 1) "recipes" else "recipe"
        return "$status $count $text"
    }
}

interface RecipeValidator {

    fun validateSelection(
        ids: List<Int>,
        recipeIds: List<Int>,
        unselectedRecipeIds: List<Int>
    ): Result<String>

    fun validateUnSelection(
        ids: List<Int>,
        recipeIds: List<Int>,
        selectedRecipeIds: List<Int>
    ): Result<String>
}

class RecipeValidatorImpl(
    private val configuration: Configuration
) : RecipeValidator {

    override fun validateSelection(
        ids: List<Int>,
        recipeIds: List<Int>,
        unselectedRecipeIds: List<Int>
    ): Result<String> = kotlin.runCatching {
        checkEmpty(ids)
        checkSize(ids, configuration.minimumSelection)
        checkUnknown(ids, recipeIds)
        checkSelected(ids, unselectedRecipeIds)
        return Result.success("Success")
    }

    override fun validateUnSelection(
        ids: List<Int>,
        recipeIds: List<Int>,
        selectedRecipeIds: List<Int>
    ): Result<String> = kotlin.runCatching {
        checkEmpty(ids)
        checkUnknown(ids, recipeIds)
        checkUnselected(ids, selectedRecipeIds)
        return Result.success("Success")
    }

    private fun checkEmpty(ids: List<Int>) {
        if (ids.isEmpty()) {
            throw EmptyRecipeException()
        }
    }

    private fun checkSize(ids: List<Int>, minSize: Int) {
        if (ids.size > minSize) {
            throw MinimumSelectionException(minSize)
        }
    }

    private fun checkUnknown(ids: List<Int>, recipeIds: List<Int>) {
        val unKnownRecipes = ids.subtract(recipeIds)
        if (unKnownRecipes.isNotEmpty()) {
            throw UnknownRecipeException(unKnownRecipes.joinToString())
        }
    }

    private fun checkSelected(ids: List<Int>, unselectedRecipes: List<Int>) {
        val items = ids.intersect(unselectedRecipes)
        if (items.isEmpty()) {
            throw RecipeSelectedException(ids.joinToString())
        }
    }

    private fun checkUnselected(ids: List<Int>, selectedRecipes: List<Int>) {
        val items = ids.intersect(selectedRecipes)
        if (items.isEmpty()) {
            throw RecipeUnSelectedException(ids.joinToString())
        }
    }
}

data class Recipe(
    val id: Int,
    val title: String,
    val tags: List<String>
) {
    override fun equals(other: Any?): Boolean =
        if (other == null || other !is Recipe) {
            false
        } else this.id == other.id

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + tags.hashCode()
        return result
    }
}

data class Subscription(
    val id: Int,
    val delivery: Int,
    val isFamily: Boolean
)

class MinimumSelectionException(count: Int) : IllegalArgumentException("Can't select more than $count recipes")
class EmptyRecipeException : IllegalArgumentException("ids cannot be empty")
class UnknownRecipeException(ids: String) : IllegalArgumentException("Recipe with id ($ids) not found")
class RecipeSelectedException(ids: String) : IllegalArgumentException("Recipe with id ($ids) is already selected")
class RecipeUnSelectedException(ids: String) : IllegalArgumentException("Recipe with id ($ids) is already unselected")