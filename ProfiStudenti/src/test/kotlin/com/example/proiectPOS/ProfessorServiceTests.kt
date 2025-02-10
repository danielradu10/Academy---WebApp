package com.example.proiectPOS

import com.example.proiectPOS.persistance.*
import com.example.proiectPOS.persistance.data.Professor
import com.example.proiectPOS.persistance.repositories.ProfessorsRepositoryInterface
import com.example.proiectPOS.services.ProfessorService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*
import kotlin.test.Test

class ProfessorServiceTests {
    private lateinit var professorService : ProfessorService
    private lateinit var mockProfessors: MutableList<Professor>
    private lateinit var mockProfessorsRepository: ProfessorsRepositoryInterface


    @BeforeEach
    fun setup() {
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

        professorService = ProfessorService()
        mockProfessorsRepository = mock()
        this.professorService.initializeRepository(professorsRepositoryInterface = mockProfessorsRepository)
    }


    @Test
    fun `test getProfessorById should return null`(){
        val id = 0
        `when`(mockProfessorsRepository.findById(id.toLong())).thenReturn(Optional.empty())
        val returnedVal = this.professorService.getProfessorById(id)
        assert(returnedVal!!.message == "Professor not found")
    }

    @Test
    fun `test getProfessorById should return professor`(){
        val id = 0
        `when`(mockProfessorsRepository.findById(id.toLong())).thenReturn(Optional.of(mockProfessors[0]))
        val returnedVal = this.professorService.getProfessorById(id)
        assert(returnedVal!!.professor == mockProfessors[0])
    }


    @Test
    fun `test getAllProfessors without parameters`(){
        `when`(mockProfessorsRepository.findAll(PageRequest.of(1, 1))).thenReturn(PageImpl(
            mockProfessors))
        val returnedVal = this.professorService.getAllProfessors(
            null, null, null,
            page = Optional.of(1),
            itemsPerPage = Optional.of(1)
        )
        assert(returnedVal.professors.content == mockProfessors)
    }

    @Test
    fun `test getAllProfessors with acadRank, lastname, firstname`(){
        val firstName = "Marcel"
        val lastName =  "Roman"
        val acadRank = Degree.Main

        `when`(mockProfessorsRepository.findAllByFirstNameAndLastNameAndDegree(
            firstName, lastName, acadRank,
            page = PageRequest.of(1, 1)
        )).thenReturn(
            PageImpl(mutableListOf(mockProfessors[1])))
        val returnedVal = this.professorService.getAllProfessors(
            acadRank, lastName, firstName,
            page = Optional.of(1),
            itemsPerPage = Optional.of(1)
        )
        assert(returnedVal.professors.content == mutableListOf(mockProfessors[1]))
    }

    @Test
    fun `test getAllProfessors with acadRank, lastname`(){
        val lastName =  "Roman"
        val acadRank = Degree.Main

        `when`(mockProfessorsRepository.findAllByLastNameAndDegree(
            lastName, acadRank,
            page = PageRequest.of(1, 1)
        )).thenReturn(PageImpl(mutableListOf(mockProfessors[1])))
        val returnedVal = this.professorService.getAllProfessors(
            acadRank, lastName, null,
            page = Optional.of(1),
            itemsPerPage = Optional.of(1)
        )
        assert(returnedVal.professors.content == mutableListOf(mockProfessors[1]))
    }


    @Test
    fun `test getAllProfessors with acadRank`(){
        val acadRank = Degree.Main

        `when`(mockProfessorsRepository.findAllByDegree(
            acadRank,
            page = PageRequest.of(1, 1)
        )).thenReturn(
            PageImpl(mutableListOf(mockProfessors[1])))
        val returnedVal = this.professorService.getAllProfessors(
            acadRank, null, null,
            page = Optional.of(1),
            itemsPerPage = Optional.of(1)
        )
        assert(returnedVal.professors.content == mutableListOf(mockProfessors[1]))
    }

    @Test
    fun `test getAllProfessors with lastName, firstName`(){
        val firstName = "Marcel"
        val lastName =  "Roman"

        `when`(mockProfessorsRepository.findAllByFirstNameAndLastName(
            firstName, lastName,
            page = PageRequest.of(1, 1)
        )).thenReturn(
            PageImpl(mutableListOf(mockProfessors[1])))
        val returnedVal = this.professorService.getAllProfessors(
            null, lastName, firstName,
            page = Optional.of(1),
            itemsPerPage = Optional.of(1)
        )
        assert(returnedVal.professors.content == mutableListOf(mockProfessors[1]))
    }

    @Test
    fun `test getAllProfessors with lastName`(){
        val lastName =  "Roman"

        `when`(mockProfessorsRepository.findAllByLastName(
            lastName,
            page = PageRequest.of(1, 1)
        )).thenReturn(PageImpl(
            mutableListOf(mockProfessors[1])))
        val returnedVal = this.professorService.getAllProfessors(
            null, lastName, null,
            page = Optional.of(1),
            itemsPerPage = Optional.of(1)
        )
        assert(returnedVal.professors.content == mutableListOf(mockProfessors[1]))
    }
}