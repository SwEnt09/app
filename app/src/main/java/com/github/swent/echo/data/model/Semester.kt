package com.github.swent.echo.data.model

interface Semester

enum class SemesterEPFL : Semester {
    BA1,
    MAN,
    BA2,
    BA3,
    BA4,
    BA5,
    BA6,
    MA1,
    MA2,
    MA3,
    MA4,
}

fun String.toSemesterEPFL(): SemesterEPFL? {
    return try {
        SemesterEPFL.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}
