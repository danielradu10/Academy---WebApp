package com.example.proiectPOS.services

import com.example.proiectPOS.controller.DisciplineDeleteResponse
import com.example.proiectPOS.controller.DisciplineInsertRequest
import com.example.proiectPOS.controller.DisciplineResponse
import com.example.proiectPOS.interfaces.DisciplinesServiceInterface
import com.example.proiectPOS.persistance.DisciplineCategory
import com.example.proiectPOS.persistance.DisciplineType
import com.example.proiectPOS.persistance.data.Discipline
import com.example.proiectPOS.persistance.data.Professor
import com.example.proiectPOS.persistance.repositories.DisciplineRepositoryInterface
import com.example.proiectPOS.persistance.repositories.ProfessorsRepositoryInterface
import com.google.common.annotations.VisibleForTesting
import org.springframework.data.domain.Page
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class DisciplineService: DisciplinesServiceInterface {
    @Autowired
    private lateinit var disciplinesRepository: DisciplineRepositoryInterface

    @Autowired
    private lateinit var professorsRepository: ProfessorsRepositoryInterface


    companion object CONSTANTS{
        const val DEFAULT_ITEMS_PER_PAGE = 1
        const val DEFAULT_PAGE = 0
        const val MAX_ITEM_PER_PAGE = 35
        const val MAX_CHARACTERS_FOR_DISC_CODE = 5
        const val MAX_CHARACTERS_FOR_DISC_NAME = 50
    }

    @VisibleForTesting
    fun initializeRepositories(disciplineRepository: DisciplineRepositoryInterface, professorsRepository: ProfessorsRepositoryInterface){
        this.disciplinesRepository  = disciplineRepository
        this.professorsRepository = professorsRepository
    }

    override fun getAllDisciplines(page: Optional<Int>, itemsPerPage: Optional<Int>, type: DisciplineType?, category: DisciplineCategory?): Page<Discipline> {
        var items  = 0
        if (itemsPerPage.isPresent) {
            items = itemsPerPage.get()
            if (items > MAX_ITEM_PER_PAGE){
                items = MAX_ITEM_PER_PAGE
            }
        }
        if (type == null && category == null){
            if (page.isPresent){
                if (itemsPerPage.isPresent){
                    return disciplinesRepository.findAll(PageRequest.of(page.get(), items))
                }
                return disciplinesRepository.findAll(PageRequest.of(page.get(), DEFAULT_ITEMS_PER_PAGE ))
            }
            return disciplinesRepository.findAll(PageRequest.of(DEFAULT_PAGE, DEFAULT_ITEMS_PER_PAGE ))
        }
        else if(type != null && category == null){
            if (page.isPresent){
                if (itemsPerPage.isPresent){
                    return disciplinesRepository.findByDisciplineType(type, PageRequest.of(page.get(), items))
                }
                return disciplinesRepository.findByDisciplineType(type, PageRequest.of(page.get(), DEFAULT_ITEMS_PER_PAGE ))
            }
            return disciplinesRepository.findByDisciplineType(type, PageRequest.of(DEFAULT_PAGE, DEFAULT_ITEMS_PER_PAGE ))
        }
        else if(type == null && category != null){
            if (page.isPresent){
                if (itemsPerPage.isPresent){
                    return disciplinesRepository.findByDisciplineCategory(category, PageRequest.of(page.get(), items))
                }
                return disciplinesRepository.findByDisciplineCategory(category, PageRequest.of(page.get(), DEFAULT_ITEMS_PER_PAGE ))
            }
            return disciplinesRepository.findByDisciplineCategory(category, PageRequest.of(DEFAULT_PAGE, DEFAULT_ITEMS_PER_PAGE ))
        }
        else
        {
            if (page.isPresent){
                if (itemsPerPage.isPresent){
                    return disciplinesRepository.findByDisciplineTypeAndDisciplineCategory(type!!, category!!, PageRequest.of(page.get(), items))
                }
                return disciplinesRepository.findByDisciplineTypeAndDisciplineCategory(type!!, category!!, PageRequest.of(page.get(), DEFAULT_ITEMS_PER_PAGE ))
            }
            return disciplinesRepository.findByDisciplineTypeAndDisciplineCategory(type!!, category!!, PageRequest.of(DEFAULT_PAGE, DEFAULT_ITEMS_PER_PAGE ))
        }
    }

    // insertDiscipline searches for the existence of a professor and only on that path adds the discipline
    override fun insertDiscipline(discipline: DisciplineInsertRequest): DisciplineResponse{
        if (discipline.disciplineCode.length > MAX_CHARACTERS_FOR_DISC_CODE ||
            discipline.disciplineName.length > MAX_CHARACTERS_FOR_DISC_NAME){
            return DisciplineResponse("Too many characters!", null, null)
        }
        if (discipline.yearDegree <= 0 || discipline.yearDegree > 4){
            return DisciplineResponse("Invalid year degree!", null, null)

        }
        val professor = professorsRepository.findByEmailProfessor(discipline.professorMail)
        if (professor.isPresent){
            val disciplineToAdd = Discipline(
                disciplineCode = discipline.disciplineCode,
                professor = professor.get(),
                disciplineName = discipline.disciplineName,
                disciplineCategory = discipline.disciplineCategory,
                disciplineType = discipline.disciplineType,
                yearDegree = discipline.yearDegree,
                examType = discipline.examType
            )
            disciplinesRepository.save(disciplineToAdd)
            return DisciplineResponse("Discipline inserted with success!", disciplineToAdd, null)
        }
        return DisciplineResponse("The professor does not exist!", null, null)
    }

    override fun deleteDisciplineByCode(id: String): DisciplineDeleteResponse {
        val discipline = disciplinesRepository.findByDisciplineCode(id)
        if (discipline.isPresent){
            val foundDiscipline = discipline.get()
            if(foundDiscipline.students.isNotEmpty()){
                return DisciplineDeleteResponse(
                    message = "Students are enrolled for this discipline."
                )
            }
            disciplinesRepository.delete(foundDiscipline)
            return DisciplineDeleteResponse(
                message = "Discipline deleted!"
            )
        }
        else{
            return DisciplineDeleteResponse(
                message = "Discipline does not exist."
            )
        }
    }

    override fun getDisciplineByCode(id: String): DisciplineResponse?{
        if (id.count() > MAX_CHARACTERS_FOR_DISC_CODE){
            return DisciplineResponse(
                discipline = null,
                message = "Too many characters!",
                _links = null
            )
        }
        val result = disciplinesRepository.findByDisciplineCode(id)
        if (result.isPresent){
            return DisciplineResponse(
                discipline = result.get(),
                message = "Discipline found",
                _links = null
            )
        }
        else{
            return DisciplineResponse(
                discipline = null,
                message = "Discipline not found",
                _links = null
            )
        }
    }

    override fun getAllDisciplinesByIdTitular(professor: Professor): List<Discipline> {
        return this.disciplinesRepository.findByProfessor(professor)
    }

    override fun getDisciplinesByIdTitular(professor: Professor, page: Optional<Int>, itemsPerPage: Optional<Int>): Page<Discipline>{
        var nrPage = DEFAULT_PAGE
        if (page.isPresent){
            nrPage = page.get()
        }
        var nrItemsPerPage = DEFAULT_ITEMS_PER_PAGE
        if (itemsPerPage.isPresent){
            nrItemsPerPage = itemsPerPage.get()
            if (nrItemsPerPage > MAX_ITEM_PER_PAGE){
                nrItemsPerPage = MAX_ITEM_PER_PAGE
            }
        }
        val result = disciplinesRepository.findByProfessor(professor, PageRequest.of(nrPage, nrItemsPerPage))
        return result
    }
}