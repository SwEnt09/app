package com.github.swent.echo.data.model

interface Section {
    val name: String
}

enum class SectionEPFL : Section {
    UNKNOWN,
    // School of Basic Sciences
    MA,
    CGC,
    PH,

    // School of Computer Science and Communication Sciences
    IN,
    SC,

    // School of Engineering
    EL,
    GM,
    MT,
    MX,

    // School of Life Sciences
    SV,

    // School of Architecture, Civil and Environmental Engineering
    AR,
    GC,
    SIE,
}

fun String.toSectionEPFL(): SectionEPFL? {
    return try {
        SectionEPFL.valueOf(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}
