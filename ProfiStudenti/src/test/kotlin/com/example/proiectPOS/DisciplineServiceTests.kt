package com.example.proiectPOS

import com.example.proiectPOS.controller.DisciplineInsertRequest
import com.example.proiectPOS.persistance.*
import com.example.proiectPOS.persistance.data.Discipline
import com.example.proiectPOS.persistance.data.Professor
import com.example.proiectPOS.persistance.repositories.DisciplineRepositoryInterface
import com.example.proiectPOS.persistance.repositories.ProfessorsRepositoryInterface
import com.example.proiectPOS.services.DisciplineService
import org.hibernate.query.Page
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*
import kotlin.test.Test

class DisciplineServiceTests {
    private lateinit var mockDisciplinesRepository: DisciplineRepositoryInterface
    private lateinit var mockProfessorsRepository: ProfessorsRepositoryInterface
    private lateinit var mockDisciplines: MutableList<Discipline>
    private lateinit var mockProfessors: MutableList<Professor>

    private lateinit var disciplineService : DisciplineService

    companion object CONSTANTS{
        val defaultPageNumber = 0
        val defaulteItemsPerPage = 1
        val specifiedType = DisciplineType.Mandatory
        val specifiedCategory = DisciplineCategory.Speciality
    }


    @BeforeEach
    fun setup() {
        mockDisciplinesRepository = mock<DisciplineRepositoryInterface>()
        mockProfessorsRepository = mock<ProfessorsRepositoryInterface>()
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

        mockProfessors = mutableListOf(Professor(
            professorId = 1,
            lastName = "Strugariu",
            firstName = "Radu",
            emailProfessor = "radu.strugariu@academic.tuiasi.ro",
            degree = Degree.Main,
            associationType = AssociationType.Main
        ), Professor(
            professorId = 1,
            lastName = "Roman",
            firstName = "Marcel",
            emailProfessor = "marcel.roman@academic.tuiasi.ro",
            degree = Degree.Main,
            associationType = AssociationType.Main
        ))

        disciplineService = DisciplineService()
        disciplineService.initializeRepositories(mockDisciplinesRepository, mockProfessorsRepository)
    }

    @Test
    fun `test getAllDisciplines with no params`() {
        `when`(mockDisciplinesRepository.findAll(PageRequest.of(defaultPageNumber, defaulteItemsPerPage))).thenReturn(PageImpl(mockDisciplines))
        val page = disciplineService.getAllDisciplines(Optional.empty(), Optional.empty(), null, null)
        assert(page.content == mockDisciplines)
    }

    @Test
    fun `test getAllDisciplines with page`() {
        val pageNumber = 3
        `when`(mockDisciplinesRepository.findAll(PageRequest.of(pageNumber, defaulteItemsPerPage))).thenReturn(PageImpl(mockDisciplines))
        val page = disciplineService.getAllDisciplines(Optional.of(pageNumber), Optional.empty(), null, null)
        assert(page.content == mockDisciplines)
    }

    @Test
    fun `test getAllDisciplines with page and itemsPerPage`() {
        val itemsPerPage = 2
        val pageNumber = 0
        `when`(mockDisciplinesRepository.findAll(PageRequest.of(pageNumber, itemsPerPage))).thenReturn(PageImpl(mockDisciplines))
        val page = disciplineService.getAllDisciplines(Optional.of(pageNumber), Optional.of(itemsPerPage), null, null)
        assert(page.content == mockDisciplines)
    }

    @Test
    fun `test getAllDisciplines with page and itemsPerPage greater than max`() {
        val maxPerPage = 35
        val itemsPerPage = 100
        val pageNumber = 0
        `when`(mockDisciplinesRepository.findAll(PageRequest.of(pageNumber, maxPerPage))).thenReturn(PageImpl(mockDisciplines))
        val page = disciplineService.getAllDisciplines(Optional.of(pageNumber), Optional.of(itemsPerPage), null, null)
        assert(page.content == mockDisciplines)
    }

    @Test
    fun `test getAllDisciplines with type`() {
        `when`(mockDisciplinesRepository.findByDisciplineType(specifiedType, PageRequest.of(defaultPageNumber, defaulteItemsPerPage))).thenReturn(PageImpl(mockDisciplines))
        val page = disciplineService.getAllDisciplines(Optional.empty(), Optional.empty(), specifiedType, null)
        assert(page.content == mockDisciplines)
    }

    @Test
    fun `test getAllDisciplines with type and page`() {
        val specifiedPage = 3
        `when`(mockDisciplinesRepository.findByDisciplineType(specifiedType, PageRequest.of(specifiedPage, defaulteItemsPerPage))).thenReturn(PageImpl(mockDisciplines))
        val page = disciplineService.getAllDisciplines(Optional.of(specifiedPage), Optional.empty(), specifiedType, null)
        assert(page.content == mockDisciplines)
    }

    @Test
    fun `test getAllDisciplines with type, page and items per page`() {
        val specifiedPage = 3
        val itemsPerPage = 3
        `when`(mockDisciplinesRepository.findByDisciplineType(specifiedType, PageRequest.of(specifiedPage, itemsPerPage))).thenReturn(PageImpl(mockDisciplines))
        val page = disciplineService.getAllDisciplines(Optional.of(specifiedPage), Optional.of(itemsPerPage), specifiedType, null)
        assert(page.content == mockDisciplines)
    }

    @Test
    fun `test getAllDisciplines with category`() {
        `when`(mockDisciplinesRepository.findByDisciplineCategory(specifiedCategory, PageRequest.of(defaultPageNumber, defaulteItemsPerPage))).thenReturn(PageImpl(mockDisciplines))
        val page = disciplineService.getAllDisciplines(Optional.empty(), Optional.empty(), null, specifiedCategory)
        assert(page.content == mockDisciplines)
    }

    @Test
    fun `test getAllDisciplines with category and page`() {
        val specifiedPage = 3
        `when`(mockDisciplinesRepository.findByDisciplineCategory(specifiedCategory, PageRequest.of(specifiedPage, defaulteItemsPerPage))).thenReturn(PageImpl(mockDisciplines))
        val page = disciplineService.getAllDisciplines(Optional.of(specifiedPage), Optional.empty(), null, specifiedCategory)
        assert(page.content == mockDisciplines)
    }

    @Test
    fun `test getAllDisciplines with category, page and items per page`() {
        val specifiedPage = 3
        val itemsPerPage = 3
        `when`(mockDisciplinesRepository.findByDisciplineCategory(specifiedCategory, PageRequest.of(specifiedPage, itemsPerPage))).thenReturn(PageImpl(mockDisciplines))
        val page = disciplineService.getAllDisciplines(Optional.of(specifiedPage), Optional.of(itemsPerPage), null, specifiedCategory)
        assert(page.content == mockDisciplines)
    }

    @Test
    fun `test getAllDisciplines with category and type`() {
        `when`(mockDisciplinesRepository.findByDisciplineTypeAndDisciplineCategory(specifiedType, specifiedCategory,PageRequest.of(defaultPageNumber, defaulteItemsPerPage))).thenReturn(PageImpl(mockDisciplines))
        val page = disciplineService.getAllDisciplines(Optional.empty(), Optional.empty(), specifiedType, specifiedCategory)
        assert(page.content == mockDisciplines)
    }

    @Test
    fun `test getAllDisciplines with category, type, pageNumber`() {
        val specifiedPage = 3
        `when`(mockDisciplinesRepository.findByDisciplineTypeAndDisciplineCategory(specifiedType, specifiedCategory,PageRequest.of(specifiedPage, defaulteItemsPerPage))).thenReturn(PageImpl(mockDisciplines))
        val page = disciplineService.getAllDisciplines(Optional.of(specifiedPage), Optional.empty(), specifiedType, specifiedCategory)
        assert(page.content == mockDisciplines)
    }

    @Test
    fun `test getAllDisciplines with category, type, page number and items per page`() {
        val specifiedPage = 3
        val itemsPerPage = 3
        `when`(mockDisciplinesRepository.findByDisciplineTypeAndDisciplineCategory(specifiedType, specifiedCategory,PageRequest.of(specifiedPage, itemsPerPage))).thenReturn(PageImpl(mockDisciplines))
        val page = disciplineService.getAllDisciplines(Optional.of(specifiedPage), Optional.of(itemsPerPage), specifiedType, specifiedCategory)
        assert(page.content == mockDisciplines)
    }


    @Test
    fun `test insertDiscipline should return successful`() {
        val professorEmail = "radu.strugariu@academic.tuiasi.ro"
        val disciplineRequest = DisciplineInsertRequest(
            disciplineCode = "SPD",
            disciplineName = "Statistica si prelucararea datelor",
            professorMail = professorEmail,
            yearDegree = 2,
            disciplineType = DisciplineType.Mandatory,
            disciplineCategory = DisciplineCategory.Speciality,
            examType = ExaminationType.Exam
        )
        `when`(mockProfessorsRepository.findByEmailProfessor(professorEmail)).thenReturn(Optional.of(mockProfessors[0]))

        val expectedReturned = Discipline(
            disciplineCode = "SPD",
            professor =  mockProfessors[0],
            disciplineName = "Statistica si prelucararea datelor",
            yearDegree = 2,
            disciplineType = DisciplineType.Mandatory,
            disciplineCategory = DisciplineCategory.Speciality,
            examType = ExaminationType.Exam,
            students = mutableListOf()
        )
        val result = disciplineService.insertDiscipline(disciplineRequest)
        assert(result.message == "Discipline inserted with success!")
        compareDisciplines(result.discipline!!, expectedReturned)
    }

    @Test
    fun `test insertDiscipline should return professor not exist`() {
        val professorEmail = "not a valid mail"
        `when`(mockProfessorsRepository.findByEmailProfessor(professorEmail)).thenReturn(Optional.empty())

        val disciplineRequest = DisciplineInsertRequest(
            disciplineCode = "SPD",
            disciplineName = "Statistica si prelucararea datelor",
            professorMail = professorEmail,
            yearDegree = 2,
            disciplineType = DisciplineType.Mandatory,
            disciplineCategory = DisciplineCategory.Speciality,
            examType = ExaminationType.Exam
        )
        val result = disciplineService.insertDiscipline(disciplineRequest)

        assert(result.message == "The professor does not exist!")
        assert(result.discipline == null)
    }

    @Test
    fun `test getDisciplineById should work`() {
        val disciplineCode = "AM"
        `when`(mockDisciplinesRepository.findByDisciplineCode(disciplineCode)).thenReturn(Optional.of(mockDisciplines[0]))

        val receivedDiscipline = disciplineService.getDisciplineByCode(disciplineCode)
        assert(receivedDiscipline != null)
        compareDisciplines(receivedDiscipline!!.discipline!!, mockDisciplines[0])
    }

    @Test
    fun `test getDisciplinesByIdTitular should work`() {
        val professor = mockProfessors[0]
        `when`(mockDisciplinesRepository.findByProfessor(
            professor,
            page = PageRequest.of(1, 1)
        )).thenReturn(PageImpl(mutableListOf(mockDisciplines[0])))

        val receivedDiscipline = disciplineService.getDisciplinesByIdTitular(professor, Optional.of(1), Optional.of(1))
        assert(receivedDiscipline.count() == 1)
        compareDisciplines(receivedDiscipline.content[0], mockDisciplines[0])
    }

    @Test
    fun `test getDisciplineById should return null`() {
        val disciplineCode = "invalid code"
        `when`(mockDisciplinesRepository.findByDisciplineCode(disciplineCode)).thenReturn(Optional.empty())

        val receivedDiscipline = disciplineService.getDisciplineByCode(disciplineCode)
        assert(receivedDiscipline!!.message == "Too many characters!")
    }
    private fun compareDisciplines(discipline1: Discipline, discipline2: Discipline){
        assert(discipline1.disciplineName == discipline2.disciplineName)
        assert(discipline1.disciplineType == discipline2.disciplineType)
        assert(discipline1.disciplineCategory == discipline2.disciplineCategory)
        assert(discipline1.disciplineCode == discipline2.disciplineCode)
        assert(discipline1.examType == discipline2.examType)
        assert(discipline1.yearDegree == discipline2.yearDegree)
        assert(discipline1.professor.emailProfessor == discipline2.professor.emailProfessor)
        assert(discipline1.students.count() == discipline2.students.count())
    }
}