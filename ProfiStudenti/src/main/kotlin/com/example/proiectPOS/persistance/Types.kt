package com.example.proiectPOS.persistance

import com.fasterxml.jackson.annotation.JsonValue

enum class CycleStudy(@JsonValue val value: String){
    Bachelor("Bachelor"),
    Master("Master")
}


enum class DisciplineType(@JsonValue val value: String){
    Mandatory("Mandatory"),
    Optional("Optional"),
    FreeChoice("FreeChoice")
}

enum class DisciplineCategory(@JsonValue val value: String){
    Domain("Domain"),
    Speciality("Speciality"),
    Adjacency("Adjacency")
}

enum class ExaminationType(@JsonValue val value: String){
    Exam("Exam"),
    Colocviu("Colocviu"),
}

enum class Degree(@JsonValue val value: String){
    Assistant("Assistant"),
    Main("Main"),
    Conf("Conf"),
    Professor("Professor")
}

enum class AssociationType(@JsonValue val value: String){
    Main("Main"),
    Associate("Associate"),
    Extern("Extern")
}

