package com.example.proiectPOS.services

import com.example.proiectPOS.controller.*
import com.example.proiectPOS.interfaces.ProfessorServiceInterface
import com.example.proiectPOS.persistance.Degree
import com.example.proiectPOS.persistance.data.Professor
import com.example.proiectPOS.persistance.repositories.ProfessorsRepositoryInterface
import com.google.common.annotations.VisibleForTesting
import jakarta.servlet.http.HttpServletRequest
import org.springdoc.core.customizers.ParameterCustomizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ProfessorService: ProfessorServiceInterface {
    @Autowired
    private lateinit var nullableKotlinRequestParameterCustomizer: ParameterCustomizer

    @Autowired
    private lateinit var professorRepository: ProfessorsRepositoryInterface

    companion object CONSTANTS{
        const val MAX_NAME_LENGTH = 30
        const val MAX_EMAIL_LENGTH = 50
        const val DEFAULT_PAGE = 0
        const val DEFAULT_ITEMS_PER_PAGE = 5
        const val MAX_ITEMS_PER_PAGE = 10
    }

    @VisibleForTesting
    fun initializeRepository(professorsRepositoryInterface: ProfessorsRepositoryInterface){
        this.professorRepository = professorsRepositoryInterface
    }

    override fun getProfessorById(id: Int): ProfessorResponse? {
        println("Searching for id: $id")
        if (id < 0){
            return ProfessorResponse(
                professor = null,
                message = "The id has to be greater than 0!",
                _links = null
            )
        }
        val result = professorRepository.findById(id.toLong())
        println("Result for id: $result")
        if (result.isPresent){
            return ProfessorResponse(
                professor = result.get(),
                message = "Professor found",
                _links = null
            )
        }
        else{
            return ProfessorResponse(
                professor = null,
                message = "Professor not found",
                _links = null
            )
        }
    }

    override fun getProfessorByEmail(email: String): ProfessorResponse? {
        println("Searching for email: $email")
        if (validateEmail(email) == false){
            return ProfessorResponse(
                professor = null,
                message = "Invalid email",
                _links = null
            )
        }
        val result = professorRepository.findByEmailProfessor(email)
        println("Result for id: $result")
        if (result.isPresent){
            return ProfessorResponse(
                professor = result.get(),
                message = "Professor found",
                _links = null
            )
        }
        else{
            return ProfessorResponse(
                professor = null,
                message = "Professor not found",
                _links = null
            )
        }
    }

    override fun getAllProfessors(acadRank: Degree?, lastName: String?, firstName: String?, page: Optional<Int>, itemsPerPage: Optional<Int>): GetProfessorsResponse {
        var nrPage = DEFAULT_PAGE
        var nrItemsPerPage = DEFAULT_ITEMS_PER_PAGE
        if (page.isPresent){
            nrPage = page.get()
        }
        if (itemsPerPage.isPresent){
            nrItemsPerPage = itemsPerPage.get()
            if (nrItemsPerPage > MAX_ITEMS_PER_PAGE){
                nrPage = MAX_ITEMS_PER_PAGE
            }
        }

        val nonValidFormat = validateQueryNameParams(firstName, lastName)
        if (nonValidFormat != null){
            return nonValidFormat
        }

        if (acadRank!= null && lastName != null && firstName != null){
            return GetProfessorsResponse(
                professors = professorRepository.findAllByFirstNameAndLastNameAndDegree(firstName, lastName, acadRank,  PageRequest.of(nrPage, nrItemsPerPage)),
                message = "Returned professors",
                _links = null,
            )
        }
        if (acadRank != null && lastName != null){
            return GetProfessorsResponse(
                professors = professorRepository.findAllByLastNameAndDegree(lastName, acadRank, PageRequest.of(nrPage, nrItemsPerPage)),
                message = "Returned professors",
                _links = null,
                )
        }
        if (acadRank != null){
            return GetProfessorsResponse(
                professors = professorRepository.findAllByDegree(acadRank, PageRequest.of(nrPage, nrItemsPerPage)),
                message = "Returned professors",
                _links = null,
                )
        }
        if (lastName != null && firstName != null){
            return GetProfessorsResponse(
                professors = professorRepository.findAllByFirstNameAndLastName(firstName, lastName, PageRequest.of(nrPage, nrItemsPerPage)),
                message = "Returned professors",
                _links = null,
                )
        }
        if (lastName != null){
            return GetProfessorsResponse(
                professors = professorRepository.findAllByLastName(lastName, PageRequest.of(nrPage, nrItemsPerPage)),
                message = "Returned professors",
                _links = null,
                )
        }

        return GetProfessorsResponse(
            professors = professorRepository.findAll(PageRequest.of(nrPage, nrItemsPerPage)),
            message = "Returned professors",
            _links = null,
            )

    }

    override fun insertProfessor(professor: Professor): InsertProfessorResponse{
        if (professor.lastName.length > MAX_NAME_LENGTH){
            return InsertProfessorResponse(
                message = "Too many characters!"
            )
        }

        if (professor.firstName.length > MAX_NAME_LENGTH){
            return InsertProfessorResponse(
                message = "Too many characters!"
            )
        }

        if (!validateEmail(email = professor.emailProfessor)){
            return InsertProfessorResponse(
                message = "Invalid email!"
            )
        }

        if (!validateName(name = professor.lastName) || !validateName(professor.firstName)){
            return InsertProfessorResponse(
                message = "Invalid first name or last name!"
            )
        }

       try{
           professorRepository.save(
               Professor(
                   professorId = professor.professorId,
                   lastName = professor.lastName,
                   firstName = professor.firstName,
                   emailProfessor = professor.emailProfessor,
                   degree = professor.degree,
                   associationType = professor.associationType,
                   affiliation = professor.affiliation
               )
           )
           val professorSaved = professorRepository.findByEmailProfessor(professor.emailProfessor)

           return InsertProfessorResponse(
               message = "Professor inserted successfully!",
               idProfessor = if (professorSaved.isPresent) professorSaved.get().professorId else -1
           )
       }
       catch (e: DataIntegrityViolationException){
           return InsertProfessorResponse(
               message = "The email already exists"
           )
       }
    }

    override fun deleteProfessorById(id: Int): DeleteProfessorResponse {
        val professor = this.professorRepository.findById(id.toLong())
        if (professor.isPresent){

            this.professorRepository.delete(professor.get())
            return DeleteProfessorResponse(
                message = "Professor deleted!"
            )
        }

        return DeleteProfessorResponse(
            message = "Professor does not exist!"
        )
    }



    fun validateName(name: String?): Boolean{
        val nameRegex = Regex("^[a-zA-ZăâîșțĂÂÎȘȚ ]+$")
        if (name != null && (!name.matches(nameRegex))) {
            return false
        }
        return true
    }

    fun validateEmail(email: String): Boolean{
        if (email.length > MAX_EMAIL_LENGTH){
            return false
        }
        val emailRegex = Regex("^[a-zA-Z0-9]+\\.[a-zA-Z0-9]+@academic\\.tuiasi\\.ro$")
        return email.matches(emailRegex)
    }

    fun validateQueryNameParams(firstName: String?, lastName: String?):  GetProfessorsResponse?{
        if (firstName != null && lastName == null){
            return GetProfessorsResponse(
                professors = Page.empty(),
                message = "Last name is mandatory!",
                _links = null,
            )
        }
        if (firstName != null && firstName.isEmpty()){
            return GetProfessorsResponse(
                professors = Page.empty(),
                message = "Do not pass empty data!",
                _links = null,
            )
        }
        if (lastName != null && lastName.isEmpty()){
            return GetProfessorsResponse(
                professors = Page.empty(),
                message = "Do not pass empty data!",
                _links = null,
            )
        }
        if (firstName != null && firstName.count() > MAX_NAME_LENGTH){
            return GetProfessorsResponse(
                professors = Page.empty(),
                message = "Too many characters for first name!",
                _links = null,
            )
        }
        if (lastName != null && lastName.count() > MAX_NAME_LENGTH){
            return GetProfessorsResponse(
                professors = Page.empty(),
                message = "Too many characters for last name!",
                _links = null,
            )
        }

        if (firstName != null && !validateName(firstName)){
            return GetProfessorsResponse(
                professors = Page.empty(),
                message = "Only alphabetic characters accepted",
                _links = null,
            )
        }
        if (lastName != null && !validateName(lastName)){
            return GetProfessorsResponse(
                professors = Page.empty(),
                message = "Only alphabetic characters accepted",
                _links = null,
            )
        }
        return null
    }

}