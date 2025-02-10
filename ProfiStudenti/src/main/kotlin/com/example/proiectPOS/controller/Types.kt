package com.example.proiectPOS.controller

import com.example.proiectPOS.persistance.DisciplineCategory
import com.example.proiectPOS.persistance.DisciplineType
import com.example.proiectPOS.persistance.ExaminationType
import com.example.proiectPOS.persistance.data.Discipline
import com.example.proiectPOS.persistance.data.Professor
import com.example.proiectPOS.persistance.data.Student
import org.springframework.data.domain.Page


// DisciplineRequest - represents the request for inserting a new Discipline
data class DisciplineInsertRequest(
    val disciplineCode: String,
    val disciplineName: String,
    val professorMail: String,
    val yearDegree: Int,
    val disciplineType: DisciplineType,
    val disciplineCategory: DisciplineCategory,
    val examType: ExaminationType
)

// DisciplineDeleteResponse - represents the response for deleting a discipline
data class DisciplineDeleteResponse(
    val message: String,
    var _links: Links? = null
)

// DisciplineResponse - represents the response for inserting a new Discipline
data class DisciplineResponse(
    val message: String,
    val discipline: Discipline?,
    var _links: DisciplineLinks?
)

data class DisciplineLinks(
    val self: Link?,
    val parent: Link?,
    val linkToProfessorTitular: Link?,
    val linkToDeleteDiscipline: Link?,
    val linkToRemoveDisciplineFromStudents: Link?
)

// PatchStudentDisciplinesRequest - represents the request for patching a new Discipline into students list
data class PatchStudentDisciplinesRequest(
    val studentId: Int,
    val disciplineCodes: List<String>
)

// DeleteDisciplineFromStudents - represents the response of deleting a discipline from all students
data class DeleteDisciplineFromStudents(
    val message: String,
    val _links: Links?
)

// PatchStudentDisciplinesResponse - represents the request for patching a new Discipline into students list
data class PatchStudentDisciplinesResponse(
    val message: String,
    var _links: PatchStudentDisciplinesResponseLinks? = null
)

data class PatchStudentDisciplinesResponseLinks(
    val linkToStudent: Link?,
    val linkToStudentDisciplines: Link?
)

data class DisciplinesResponse(
    val message: String,
    val disciplines: List<DisciplineResponse>,
    val _links: DisciplinesResponseLinks?
)

data class DisciplinesResponseLinks(
    val nextPage: Link?,
    val previousPage: Link?
)

// ProfessorResponse - represents the response for searching a professor by id
data class ProfessorResponse(
    val professor: Professor?,
    val message: String,
    var _links: ProfessorLinks? = null
)

data class ProfessorLinks(
    val professorStudentsLink: Link?,
    val professorDisciplinesLink: Link,
    val professorDelete: Link
)


// StudentResponse - represents the response for searching  a student by id / email
data class StudentResponse(
    val student: Student?,
    val message: String,
    var _links: StudentLinks? = null
)

// StudentLinks - defines the links of a student response
data class StudentLinks(
    val studentDisciplines: Link?,
    val removeStudent: Link?
)

// StudentsResponse - represents the response of searching more students by different queries
data class StudentsResponse(
    val message: String,
    val students: List<StudentResponse>,
    var _links: StudentsResponseLinks?
)

data class StudentsResponseLinks(
    val nextPage: Link?,
    val previousPage: Link?
)

// GetProfessorsResponse - represents the response for searching professors
data class GetProfessorsResponse(
    val professors: Page<Professor>,
    val message: String,
    var _links: GetProfessorsLinks?
)

data class GetProfessorsLinks(
    val linkToNextPage: Link?,
    val linkToPreviousPage: Link?
)

// InsertProfessorResponse - represents the response after inserting a new professor
data class InsertProfessorResponse(
    val message: String,
    val idProfessor: Int = -1,
    var _links: InsertProfessorLinks? = null
)

data class InsertProfessorLinks(
    val linkToProfessor: Link?,
    val linkToProfessorDisciplines: Link?,
    val linkToProfessorStudents: Link?,
    val linkToDeleteProfessor: Link,
)

data class DeleteProfessorResponse(
    val message: String,
    var _links: Links? = null
)

// InsertStudentResponse - represents the response after inserting a new student
data class InsertStudentResponse(
    val message: String,
    var studentId: Int = -1,
    var _links: InsertStudentLinks? = null
)

data class InsertStudentLinks(
    val linkToStudent: Link?,
    val linkToStudentDisciplines: Link?,
    val linkToDeleteStudent: Link?
)

// Link - represents a link object
data class Link(
    val href: String,
    val method: String = "GET"
)

// Links - represents an object containing more than one link
data class Links(
    val self: Link?,
    val parent: Link?,
    val nextPage: Link? = null
)
