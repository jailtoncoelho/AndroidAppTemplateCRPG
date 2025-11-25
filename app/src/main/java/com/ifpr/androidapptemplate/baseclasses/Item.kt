package com.ifpr.androidapptemplate.baseclasses

data class Item(
    var endereco: String? = null,
    val base64Image: String? = null,
    val imageUrl: String? = null,

    var tipoTreinoEditText: String? = null,
    var dataTreinoEditText: String? = null,
    var obsTreinoEditText: String? = null,
    var recordTreinoEditText: String? = null
)