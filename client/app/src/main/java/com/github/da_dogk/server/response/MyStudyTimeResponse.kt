package com.github.da_dogk.server.response

data class MyStudyTimeResponse(
    var subject: MyStudyResponse,
    var startAt: String,
    var endAt: String
)
