package com.example.todonotesapp.data

import com.example.todonotesapp.screens.Event

class DataOrException<T,Boolean,E : Exception> (
    var data : T? = null,
    var loading : Boolean? = null,
    var e : E? = null
)
