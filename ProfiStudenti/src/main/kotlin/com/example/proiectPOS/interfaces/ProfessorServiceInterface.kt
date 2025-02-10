package com.example.proiectPOS.interfaces

import com.example.proiectPOS.controller.*
import com.example.proiectPOS.persistance.Degree
import com.example.proiectPOS.persistance.data.Professor
import java.util.*

interface ProfessorServiceInterface {
    fun getProfessorById(id: Int): ProfessorResponse?
    fun getProfessorByEmail(email: String): ProfessorResponse?
    fun getAllProfessors(acadRank: Degree?, lastName: String?, firstName: String?, page: Optional<Int>, itemsPerPage: Optional<Int>): GetProfessorsResponse
    fun insertProfessor(professor: Professor): InsertProfessorResponse
    fun deleteProfessorById(id: Int): DeleteProfessorResponse
}