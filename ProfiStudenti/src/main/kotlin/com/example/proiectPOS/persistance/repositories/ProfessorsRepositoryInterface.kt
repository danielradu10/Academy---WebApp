package com.example.proiectPOS.persistance.repositories

import com.example.proiectPOS.persistance.Degree
import com.example.proiectPOS.persistance.data.Professor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProfessorsRepositoryInterface: JpaRepository<Professor, Long> {
    fun findByEmailProfessor(email: String): Optional<Professor>
    fun findAllByDegree(degree: Degree, page: Pageable): Page<Professor>
    fun findAllByLastName(lastName: String, page: Pageable): Page<Professor>
    fun findAllByFirstNameAndLastName(firstName: String, lastName: String, page: Pageable): Page<Professor>
    fun findAllByFirstNameAndLastNameAndDegree(firstName: String, lastName: String, degree: Degree, page: Pageable): Page<Professor>
    fun findAllByLastNameAndDegree(lastName: String, degree: Degree, page: Pageable): Page<Professor>

}