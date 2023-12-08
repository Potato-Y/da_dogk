package com.github.da_dogk.server.response

data class GroupAvgTimeResponse(
    val groupId : Int,
    val year : Int,
    val month : Int,
    val averageTime: Int
)
