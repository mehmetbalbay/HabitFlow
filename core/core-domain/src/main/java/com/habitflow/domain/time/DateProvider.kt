package com.habitflow.domain.time

import java.time.LocalDate

interface DateProvider {
    fun today(): LocalDate
}

