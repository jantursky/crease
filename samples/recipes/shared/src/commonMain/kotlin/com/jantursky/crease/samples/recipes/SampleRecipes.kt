package com.jantursky.crease.samples.recipes

val sampleRecipes: List<Recipe> =
    listOf(
        Recipe(
            title = "Tomato spaghetti",
            shortTitle = "Tomato pasta",
            summary = "Serves 4 · 30 minutes",
            isNew = false,
            ingredients =
                listOf(
                    Ingredient("400 g", "spaghetti"),
                    Ingredient("800 g", "ripe tomatoes, or two tins"),
                    Ingredient("4", "garlic cloves, sliced"),
                    Ingredient("3 tbsp", "olive oil"),
                    Ingredient("1 tsp", "sugar"),
                    Ingredient("1 bunch", "fresh basil"),
                    Ingredient("50 g", "Parmesan"),
                ),
            steps =
                listOf(
                    Step(
                        "Boil the water",
                        "Fill a large pot with four litres of water and bring it to a rolling boil. Salt it until it tastes like the sea.",
                        8,
                    ),
                    Step(
                        "Start the sauce",
                        "Warm the oil over medium heat and add the garlic. Cook until pale gold, about a minute, and don't let it brown.",
                        2,
                    ),
                    Step(
                        "Add the tomatoes",
                        "Crush the tomatoes by hand into the pan, add the sugar and a pinch of salt, then simmer until thick.",
                        12,
                    ),
                    Step(
                        "Cook the pasta",
                        "Drop the spaghetti into the boiling water. Lift it out two minutes before the packet says it's done.",
                        8,
                    ),
                    Step(
                        "Marry the two",
                        "Move the pasta into the sauce with a splash of its water and toss over the heat until glossy.",
                        2,
                    ),
                    Step("Serve", "Tear in the basil, grate the Parmesan over the top and eat while it's hot."),
                ),
        ),
        Recipe(
            title = "Lemon ricotta pancakes",
            shortTitle = "Lemon pancakes",
            summary = "Serves 3 · 35 minutes",
            isNew = true,
            ingredients =
                listOf(
                    Ingredient("250 g", "ricotta"),
                    Ingredient("3", "eggs, separated"),
                    Ingredient("100 g", "plain flour"),
                    Ingredient("2 tbsp", "sugar"),
                    Ingredient("1", "lemon, zest and juice"),
                    Ingredient("1 tsp", "baking powder"),
                    Ingredient("2 tbsp", "butter, for the pan"),
                    Ingredient("to serve", "honey"),
                ),
            steps =
                listOf(
                    Step(
                        "Mix the base",
                        "Stir the ricotta, yolks, sugar, lemon zest and juice together, then fold in the flour and baking powder.",
                    ),
                    Step(
                        "Whip the whites",
                        "Beat the egg whites with a pinch of salt until they hold a soft peak.",
                        3,
                    ),
                    Step(
                        "Fold gently",
                        "Fold the whites into the batter in two goes. Keep as much air in as you can.",
                    ),
                    Step("Let it rest", "Leave the batter alone so the flour can drink up the liquid.", 10),
                    Step(
                        "Cook the first side",
                        "Melt a little butter in a pan over medium-low heat and spoon in the batter. Wait for bubbles at the edge.",
                        3,
                    ),
                    Step("Flip and finish", "Turn the pancakes once and cook until they spring back when pressed.", 2),
                ),
        ),
        Recipe(
            title = "Roasted carrot soup",
            shortTitle = "Carrot soup",
            summary = "Serves 4 · 1 hour",
            isNew = false,
            ingredients =
                listOf(
                    Ingredient("1 kg", "carrots, in chunks"),
                    Ingredient("1", "onion, chopped"),
                    Ingredient("3 tbsp", "olive oil"),
                    Ingredient("2 tsp", "cumin seeds"),
                    Ingredient("1 litre", "vegetable stock"),
                    Ingredient("4 tbsp", "yoghurt"),
                ),
            steps =
                listOf(
                    Step("Heat the oven", "Set the oven to 210 °C and put a tray inside to heat up.", 10),
                    Step(
                        "Roast the carrots",
                        "Toss the carrots with half the oil, the cumin and salt, then roast until the edges catch.",
                        30,
                    ),
                    Step("Soften the onion", "Cook the onion in the rest of the oil without colour.", 6),
                    Step("Simmer", "Add the carrots and the stock and let it bubble gently.", 8),
                    Step("Blend", "Blend until smooth and thin with hot water if needed."),
                    Step("Serve", "Ladle into bowls and swirl a spoon of yoghurt on each."),
                ),
        ),
    )
