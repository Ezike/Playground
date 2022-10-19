import MenuOperation.Configuration

interface Menu : MenuOperation {

    companion object {
        fun create(
            recipes: List<Recipe>,
            subscription: Subscription
        ): Menu {
            val configuration = Configuration.Default(subscription.isFamily)
            val operation: MenuOperation = MenuInteractor(recipes, configuration)
            return DefaultMenu(operation)
        }
    }
}

fun main() {
    val menu = Menu.create(
        recipes = listOf(Recipe(1, "Menu", listOf("hot"))),
        subscription = Subscription(1, 1, true)
    )
    println(menu)
    println(menu.selectRecipe(1))
    println(menu.selectRecipe(1, 2, 3, 4))
    println(menu.selectedRecipes)
    println(menu.unselectRecipe(1))
    println(menu.getRecipesWithTag("hot"))
    println(menu.selectedRecipes)
}

interface MenuOperation {
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
}

fun Configuration.Companion.Default(isFamily: Boolean): Configuration =
    object : Configuration {
        override val minimumSelection: Int
            get() = if (isFamily) 5 else 3
    }

private data class DefaultMenu(
    private val operation: MenuOperation
) : Menu, MenuOperation by operation

class MenuInteractor(
    override val recipes: List<Recipe>,
    private val configuration: Configuration
) : MenuOperation {

    private data class SelectedRecipe(
        val recipe: Recipe,
        val isSelected: Boolean
    )

    private val menuMap = recipes.associate { it.id to SelectedRecipe(it, false) }.toMutableMap()

    override val selectedRecipes: List<Recipe>
        get() = menuMap.values.filter { it.isSelected }.map { it.recipe }

    override val selectedRecipeCount: Int
        get() = menuMap.values.count { it.isSelected }

    override fun selectRecipe(vararg ids: Int): Result<String> {
        if (ids.isEmpty()) {
            return Result.failure(EmptyRecipeException())
        }
        if (ids.size > configuration.minimumSelection) {
            return Result.failure(MaxSelectedRecipeException(configuration.minimumSelection))
        }
        var count = 0
        for (recipeId in ids) {
            menuMap[recipeId] =
                menuMap[recipeId]?.copy(isSelected = true) ?: continue
            count++
        }
        return success(count, "Selected")
    }

    override fun unselectRecipe(vararg ids: Int): Result<String> {
        if (ids.isEmpty()) {
            return Result.failure(EmptyRecipeException())
        }
        var count = 0
        for (recipeId in ids) {
            menuMap[recipeId] =
                menuMap[recipeId]?.copy(isSelected = false) ?: continue
            count++
        }
        return success(count, "Unselected")
    }

    override fun getRecipesWithTag(tag: String): List<Recipe> =
        menuMap.values.map { it.recipe }.filter { it.tags.contains(tag) }

    private fun success(count: Int, status: String): Result<String> {
        val text = if (count > 1) "recipes" else "recipe"
        return Result.success("$status $count $text")
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

class MaxSelectedRecipeException(count: Int) : IllegalArgumentException("Can't select more than $count recipes")
class EmptyRecipeException() : IllegalArgumentException("ids cannot be empty")