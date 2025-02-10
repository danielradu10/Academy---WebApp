package com.example.proiectPOS.interfaces

import com.example.proiectPOS.controller.*
import com.example.proiectPOS.persistance.CycleStudy
import com.example.proiectPOS.persistance.Degree
import com.example.proiectPOS.persistance.data.Discipline
import com.example.proiectPOS.persistance.data.Student
import java.util.Optional

interface StudentServiceInterface {
    fun getAllStudents(page: Optional<Int>, nrItems: Optional<Int>, lastName: Optional<String>, group: Optional<String>,
                       studyCycle: Optional<CycleStudy>, yearDegree: Optional<Int>): StudentsResponse
    fun getStudentByEmail(email: String): StudentResponse
    fun getStudentById(id: Long): StudentResponse?
    fun insertStudent(student: Student): InsertStudentResponse
    fun getDisciplinesForStudent(studentId: Int): DisciplinesResponse
    fun addDisciplinesForStudent(request: PatchStudentDisciplinesRequest): Pair<Boolean,String>
    fun removeDisciplinesForStudent(request: PatchStudentDisciplinesRequest): Pair<Boolean,String>
    fun removeDisciplineForAllStudents(disciplineCode: String): DeleteDisciplineFromStudents
}