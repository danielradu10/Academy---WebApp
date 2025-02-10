package com.example.proiectPOS.persistance.repositories

import com.example.proiectPOS.persistance.DisciplineCategory
import com.example.proiectPOS.persistance.DisciplineType
import com.example.proiectPOS.persistance.data.Discipline
import com.example.proiectPOS.persistance.data.Professor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface DisciplineRepositoryInterface: JpaRepository<Discipline, Long> {
    fun findByProfessor(professor: Professor): List<Discipline>
    fun findByProfessor(professor: Professor, page: Pageable): Page<Discipline>
    fun findByDisciplineCode(disciplineCode: String): Optional<Discipline>
    fun findByDisciplineType(disciplineType: DisciplineType, page: Pageable): Page<Discipline>
    fun findByDisciplineCategory(disciplineCategory: DisciplineCategory, page: Pageable): Page<Discipline>
    fun findByDisciplineTypeAndDisciplineCategory(disciplineType: DisciplineType, disciplineCategory: DisciplineCategory, page: Pageable): Page<Discipline>
}