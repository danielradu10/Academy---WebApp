package com.example.proiectPOS

import com.example.proiectPOS.controller.DisciplineResponse
import com.example.proiectPOS.controller.DisciplinesResponse
import com.example.proiectPOS.controller.PatchStudentDisciplinesRequest
import com.example.proiectPOS.controller.StudentsResponse
import com.example.proiectPOS.persistance.*
import com.example.proiectPOS.persistance.data.Discipline
import com.example.proiectPOS.persistance.data.Professor
import com.example.proiectPOS.persistance.data.Student
import com.example.proiectPOS.persistance.repositories.DisciplineRepositoryInterface
import com.example.proiectPOS.persistance.repositories.StudentsRepositoryInterface
import com.example.proiectPOS.services.StudentsService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.util.*
import kotlin.test.Test

class StudentsServiceTests {
    private lateinit var studentsService: StudentsService
    private lateinit var mockStudents: MutableList<Student>
    private lateinit var mockStudentRepository: StudentsRepositoryInterface

    private lateinit var mockDisciplines: MutableList<Discipline>
    private lateinit var mockDisciplinesRepository: DisciplineRepositoryInterface


    @BeforeEach
    fun setup() {
        mockDisciplines = mutableListOf(Discipline(
            disciplineCode = "AM",
            disciplineCategory = DisciplineCategory.Speciality,
            disciplineType = DisciplineType.Mandatory,
            disciplineName = "Analiza matematica", examType = ExaminationType.Exam,
            yearDegree = 1,
            professor = Professor(
                professorId = 1,
                lastName = "Strugariu",
                firstName = "Radu",
                emailProfessor = "radu.strugariu@academic.tuiasi.ro",
                degree = Degree.Main,
                associationType = AssociationType.Main
            )
        ), Discipline(
            disciplineCode = "ALG",
            disciplineCategory = DisciplineCategory.Speciality,
            disciplineType = DisciplineType.Mandatory,
            disciplineName = "Algebra", examType = ExaminationType.Exam,
            yearDegree = 1,
            professor = Professor(
                professorId = 1,
                lastName = "Roman",
                firstName = "Marcel",
                emailProfessor = "marcel.roman@academic.tuiasi.ro",
                degree = Degree.Main,
                associationType = AssociationType.Main
            )
        ))

        mockStudents = mutableListOf(
            Student(
                studentId = 0,
                lastName = "Radu",
                firstName = "Daniel",
                email = "daniel.radu@student.tuiasi.ro",
                studyCycle = CycleStudy.Bachelor,
                groupName = "1409A",
                yearDegree = 4,
                disciplines = mutableListOf(Discipline(
                    disciplineCode = "AM",
                    professor = Professor(
                        professorId = 1,
                        lastName = "Strugariu",
                        firstName = "Radu",
                        emailProfessor = "radu.strugariu@academic.tuiasi.ro",
                        degree = Degree.Main,
                        associationType = AssociationType.Main,
                        affiliation = null,
                    ),
                    disciplineName = "Analiza matematica",
                    yearDegree = 1,
                    disciplineType = DisciplineType.Mandatory,
                    disciplineCategory = DisciplineCategory.Speciality,
                    examType = ExaminationType.Exam,
                    students = mutableListOf()
                ))
        )
        )

        mockStudentRepository = mock()
        mockDisciplinesRepository = mock()
        studentsService = StudentsService()
        studentsService.initRepositories(mockStudentRepository, mockDisciplinesRepository)
    }

    @Test
    fun `test getAllStudents should work`(){
        val id = 0
        `when`(mockStudentRepository.findAll(PageRequest.of(0,2))).thenReturn(Page.empty())
        val returnedVal = this.studentsService.getAllStudents(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty())
        assert(returnedVal == StudentsResponse(
            message = "Found students.",
            students = mutableListOf(),
            _links = null
        ))
    }

    @Test
    fun `test getStudentById should return null`(){
        val id = 0
        `when`(mockStudentRepository.findById(id.toLong())).thenReturn(Optional.empty())
        val returnedVal = this.studentsService.getStudentById(id.toLong())
        assert(returnedVal!!.message == "Student not found")
    }

    @Test
    fun `test getStudentById should return student `(){
        val id = 0
        `when`(mockStudentRepository.findById(id.toLong())).thenReturn(Optional.of(mockStudents[0]))
        val returnedVal = this.studentsService.getStudentById(id.toLong())
        assert(returnedVal!!.student == mockStudents[0])
    }

    @Test
    fun `test getDisciplinesForStudent should return student `(){
        val id = 0
        `when`(mockStudentRepository.findById(id.toLong())).thenReturn(Optional.of(mockStudents[0]))
        val returnedVal = this.studentsService.getDisciplinesForStudent(id)
        println(returnedVal)
        val expectedVal = DisciplinesResponse(
            message = "Found disciplines",
            disciplines = mutableListOf<DisciplineResponse>(
                DisciplineResponse(
                    message = "Discipline found",
                    discipline = mockStudents[0].disciplines[0],
                    _links = null
                )
            )
                    ,
            _links =  null
        )
        println(expectedVal)
        assert(returnedVal == expectedVal)
    }

    @Test
    fun `test getDisciplinesForStudent should return emptyList `(){
        val id = 0
        `when`(mockStudentRepository.findById(id.toLong())).thenReturn(Optional.empty())
        val returnedVal = this.studentsService.getDisciplinesForStudent(id)
        assert(returnedVal.disciplines == mutableListOf<Discipline>())
    }

    @Test
    fun `test addDisciplinesForStudent should return student not present `(){
        val id = 0
        val request = PatchStudentDisciplinesRequest(
            studentId = 0,
            disciplineCodes = mutableListOf("AM")
        )
        `when`(mockStudentRepository.findById(id.toLong())).thenReturn(Optional.empty())
        val returnedVal = this.studentsService.addDisciplinesForStudent(request)
        assert(returnedVal.first == false)
        assert(returnedVal.second == "Student not present")
    }

    @Test
    fun `test addDisciplinesForStudent should return not all disciplines were added `(){
        val id = 0
        val request = PatchStudentDisciplinesRequest(
            studentId = 0,
            disciplineCodes = mutableListOf("AM")
        )
        `when`(mockStudentRepository.findById(id.toLong())).thenReturn(Optional.of(mockStudents[0]))
        `when`(mockDisciplinesRepository.findByDisciplineCode("AM")).thenReturn(Optional.empty())

        val returnedVal = this.studentsService.addDisciplinesForStudent(request)
        assert(returnedVal.first == false)
        assert(returnedVal.second == "Not all disciplines were added: AM ")
    }


    @Test
    fun `test addDisciplinesForStudent should return true `(){
        val id = 0
        val request = PatchStudentDisciplinesRequest(
            studentId = 0,
            disciplineCodes = mutableListOf("ALG")
        )
        `when`(mockStudentRepository.findById(id.toLong())).thenReturn(Optional.of(mockStudents[0]))
        `when`(mockDisciplinesRepository.findByDisciplineCode("ALG")).thenReturn(Optional.of(mockDisciplines[1]))

        val returnedVal = this.studentsService.addDisciplinesForStudent(request)
        assert(returnedVal.first == true)
        assert(returnedVal.second == "All disciplines were added")
    }

}