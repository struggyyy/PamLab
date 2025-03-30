package pl.wsei.pam.lab01.lab06.data

import java.time.LocalDate

interface CurrentDateProvider {
    val currentDate: LocalDate
}

class SystemCurrentDateProvider : CurrentDateProvider {
    override val currentDate: LocalDate
        get() = LocalDate.now()
}