package com.github.swent.echo.data.model

interface EPFLSemester

enum class BachelorSemester : EPFLSemester {
    BA1,
    MAN,
    BA2,
    BA3,
    BA4,
    BA5,
    BA6,
}

enum class MasterSemester : EPFLSemester {
    MA1,
    MA2,
    MA3,
    MA4
}
