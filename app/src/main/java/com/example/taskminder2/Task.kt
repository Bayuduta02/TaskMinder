package com.example.taskminder2

data class Task(
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val event: String = "",
    var isComplete: Boolean = false,
    val id: String = "",
){
    // Konstruktor tanpa argumen
    constructor() : this("", "", "", "", "", false,"")
}
