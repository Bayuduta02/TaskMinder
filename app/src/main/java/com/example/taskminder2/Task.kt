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
    fun isSame(other: Task): Boolean {
        return this.title == other.title &&
                this.description == other.description &&
                this.date == other.date &&
                this.time == other.time &&
                this.event == other.event &&
                this.isComplete == other.isComplete
    }
    // Konstruktor tanpa argumen
    constructor() : this("", "", "", "", "", false,"")
}
