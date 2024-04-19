package com.github.swent.echo.data.model

interface Section

interface EPFLSection : Section

enum class BachelorSection : EPFLSection {
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
    SIE
}

enum class MasterSection : EPFLSection
