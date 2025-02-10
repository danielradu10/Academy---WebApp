package com.example.proiectPOS.interfaces

import com.example.proiectPOS.controller.DisciplineDeleteResponse
import com.example.proiectPOS.controller.DisciplineInsertRequest
import com.example.proiectPOS.controller.DisciplineResponse
import com.example.proiectPOS.persistance.DisciplineCategory
import com.example.proiectPOS.persistance.DisciplineType
import com.example.proiectPOS.persistance.data.Discipline
import com.example.proiectPOS.persistance.data.Professor
import org.springframework.data.domain.Page
import java.util.Optional

interface DisciplinesServiceInterface {
    fun getAllDisciplines(page: Optional<Int>, itemsPerPage: Optional<Int>, type: DisciplineType?, category:DisciplineCategory?): Page<Discipline>
    fun getDisciplineByCode(id: String): DisciplineResponse?
    fun getAllDisciplinesByIdTitular(professor: Professor): List<Discipline>
    fun getDisciplinesByIdTitular(professor: Professor, page: Optional<Int>, itemsPerPage: Optional<Int>): Page<Discipline>
    fun insertDiscipline(discipline: DisciplineInsertRequest): DisciplineResponse
    fun deleteDisciplineByCode(id: String): DisciplineDeleteResponse
}