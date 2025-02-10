package com.example.proiectPOS.persistance.repositories

import com.example.proiectPOS.persistance.CycleStudy
import com.example.proiectPOS.persistance.data.Student
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface StudentsRepositoryInterface: JpaRepository<Student, Long> {
    fun findStudentByEmail(email: String): Optional<Student>
    fun findStudentByLastNameAndGroupNameAndYearDegreeAndStudyCycle(lastName: String, groupName: String, yearDegree: Int, cycleStudy: CycleStudy, page: Pageable): Page<Student>
    fun findStudentByLastNameAndGroupNameAndYearDegree(lastName: String, groupName: String, yearDegree: Int, page: Pageable): Page<Student>
    fun findStudentByLastNameAndGroupNameAndStudyCycle(lastName: String, groupName: String, cycleStudy: CycleStudy, page: Pageable): Page<Student>
    fun findStudentByLastNameAndStudyCycleAndYearDegree(lastName: String, study: CycleStudy, yearDegree: Int, page: Pageable): Page<Student>
    fun findStudentByGroupNameAndStudyCycleAndYearDegree(groupName: String, study: CycleStudy, yearDegree: Int, page: Pageable): Page<Student>
    fun findStudentByLastNameAndGroupName(lastName: String, groupName: String, page: Pageable): Page<Student>
    fun findStudentByLastNameAndStudyCycle(lastName: String, study: CycleStudy, page: Pageable): Page<Student>
    fun findStudentByLastNameAndYearDegree(lastName: String, yearDegree: Int, page: Pageable):Page<Student>
    fun findStudentByGroupNameAndStudyCycle(groupName: String, study: CycleStudy, page: Pageable):Page<Student>
    fun findStudentByGroupNameAndYearDegree(groupName: String, yearDegree: Int, page: Pageable): Page<Student>
    fun findStudentByStudyCycleAndYearDegree(study: CycleStudy, yearDegree: Int, page: Pageable): Page<Student>
    fun findStudentByLastName(lastName: String, page: Pageable): Page<Student>
    fun findStudentByGroupName(groupName: String, page: Pageable): Page<Student>
    fun findStudentByStudyCycle(study: CycleStudy, page: Pageable): Page<Student>
    fun findStudentByYearDegree(yearDegree: Int, page: Pageable): Page<Student>

}