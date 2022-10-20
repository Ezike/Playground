import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MenuTest {

    @Test
    fun selected_recipes() {
        val recipe = Recipe(1, "Menu", listOf("hot"))
        val menu = Menu(
            recipes = listOf(recipe),
            subscription = Subscription(1, 1, false)
        )
        val result = menu.selectRecipe(1)
        assertTrue(result.isSuccess)
        assertEquals(1, menu.selectedRecipes.size)
        assertEquals(listOf(recipe), menu.selectedRecipes)
    }

    @Test(expected = RecipeSelectedException::class)
    fun selected_recipes_throws() {
        val recipe = Recipe(1, "Menu", listOf("hot"))
        val menu = Menu(
            recipes = listOf(recipe),
            subscription = Subscription(1, 1, false)
        )
        menu.selectRecipe(1).getOrThrow()
        menu.selectRecipe(1).getOrThrow()
    }

    @Test
    fun selected_recipes_family() {
        val recipes = List(5) {
            Recipe(it, "Menu", listOf("hot"))
        }
        val menu = Menu(
            recipes = recipes,
            subscription = Subscription(1, 1, true)
        )
        menu.selectRecipe(0, 1, 2, 3, 4).getOrThrow()
        assertEquals(listOf(0, 1, 2, 3, 4), menu.selectedRecipes.map { it.id })
    }

    @Test(expected = MinimumSelectionException::class)
    fun selected_recipes_family_throws() {
        val recipes = List(5) {
            Recipe(it, "Menu", listOf("hot"))
        }
        val menu = Menu(
            recipes = recipes,
            subscription = Subscription(1, 1, true)
        )
        menu.selectRecipe(0, 1, 2, 3, 4, 5).getOrThrow()
    }

    @Test
    fun recipes_with_tag() {
        val recipe = Recipe(1, "Menu", listOf("hot"))
        val menu = Menu(
            recipes = listOf(recipe),
            subscription = Subscription(1, 1, false)
        )
        assertEquals(listOf(recipe), menu.getRecipesWithTag("hot"))
    }
}