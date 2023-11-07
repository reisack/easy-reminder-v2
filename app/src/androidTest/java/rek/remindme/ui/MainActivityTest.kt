package rek.remindme.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule
import java.util.Locale

abstract class MainActivityTest {
    protected lateinit var testHelper: TestHelper

    // @rule properties and setup() method must be public
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        val lang = Locale.getDefault().language
        testHelper = TestHelper(composeTestRule, lang)
    }
}