package com.habitflow.feature.onboarding

import org.junit.Test
import org.junit.Assert.assertEquals

class OnboardingNavTest {

    @Test
    fun `OnboardingDestinations constants have correct values`() {
        assertEquals("onboarding", OnboardingDestinations.ROOT)
        assertEquals("onb_welcome", OnboardingDestinations.WELCOME)
        assertEquals("onb_goals", OnboardingDestinations.GOALS)
        assertEquals("onb_sleep", OnboardingDestinations.SLEEP)
        assertEquals("onb_routine", OnboardingDestinations.ROUTINE)
        assertEquals("onb_meals", OnboardingDestinations.MEALS)
        assertEquals("onb_exercise", OnboardingDestinations.EXERCISE)
        assertEquals("onb_hydration", OnboardingDestinations.HYDRATION)
        assertEquals("onb_quiet", OnboardingDestinations.QUIET)
        assertEquals("onb_preview", OnboardingDestinations.PREVIEW)
    }
}
