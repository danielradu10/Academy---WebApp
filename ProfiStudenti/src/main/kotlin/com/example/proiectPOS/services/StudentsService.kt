package com.example.proiectPOS.services

import com.example.proiectPOS.controller.*
import com.example.proiectPOS.interfaces.DisciplinesServiceInterface
import com.example.proiectPOS.interfaces.StudentServiceInterface
import com.example.proiectPOS.persistance.CycleStudy
import com.example.proiectPOS.persistance.data.Discipline
import com.example.proiectPOS.persistance.data.Student
import com.example.proiectPOS.persistance.repositories.DisciplineRepositoryInterface
import com.example.proiectPOS.persistance.repositories.StudentsRepositoryInterface
import com.example.proiectPOS.services.ProfessorService.CONSTANTS.MAX_EMAIL_LENGTH
import com.google.common.annotations.VisibleForTesting
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Service
import java.util.*

@Service
class StudentsService: StudentServiceInterface {
    @Autowired
    private lateinit var  studentsRepository: StudentsRepositoryInterface

    @Autowired
    private lateinit var  disciplinesRepository: DisciplineRepositoryInterface

    companion object CONSTANTS{
        const val MAX_NAME_LENGTH = 25
        const val GROUP_LENGTH = 5
        const val DEFAULT_PAGE = 0
        const val DEFAULT_ITEMS_PER_PAGE = 2
        const val MAX_NUMBER_OF_ITEMS = 20
    }

    @VisibleForTesting
    fun initRepositories(studentsRepositoryInterface: StudentsRepositoryInterface, disciplineRepositoryInterface: DisciplineRepositoryInterface){
        this.studentsRepository = studentsRepositoryInterface
        this.disciplinesRepository = disciplineRepositoryInterface
    }



    override fun getStudentById(id: Long): StudentResponse? {
        if (id < 0){
            return StudentResponse(
                student = null,
                message = "The id has to be greater than 0!"
            )
        }
        val result = studentsRepository.findById(id.toLong())
        if (result.isPresent){
            return StudentResponse(
                student = result.get(),
                message = "Student found"
            )
        }

        return StudentResponse(
                student = null,
                message = "Student not found"
            )

    }

    override fun insertStudent(student: Student): InsertStudentResponse {
        val result = validateStudent(student)
        var id = 0
        if (result.message == "Student inserted with succes!") {
            try {
                studentsRepository.save(
                    student
                )
                val studentFound = this.studentsRepository.findStudentByEmail(student.email)
                if (studentFound.isPresent){
                    id = studentFound.get().studentId
                    result.studentId = id
                }
            }
            catch (e: DataIntegrityViolationException){
                return InsertStudentResponse(
                    message = "Email already exists!"
                )
            }
        }

        return result
    }

    override fun getDisciplinesForStudent(studentId: Int): DisciplinesResponse{
        if (studentId < 0){
            return DisciplinesResponse(
                message = "Student id should be greater than 0",
                disciplines = mutableListOf(),
                _links = null
            )
        }
        val student =  getStudentById(studentId.toLong())
        if (student!!.student != null){
            val disciplines = student.student!!.disciplines
            val disciplineResponses = mutableListOf<DisciplineResponse>()
            for (discipline in disciplines){
                disciplineResponses.add(
                    DisciplineResponse(
                        message = "Discipline found",
                        discipline = discipline,
                        _links = null
                )
                )
            }
            return DisciplinesResponse(
                message = "Found disciplines",
                disciplines = disciplineResponses,
                _links = null
            )
        }
        return DisciplinesResponse(
            "Student not found",
            disciplines = mutableListOf(),
            _links = null
        )
    }

    override fun addDisciplinesForStudent(request: PatchStudentDisciplinesRequest): Pair<Boolean,String>{
        val studentId = request.studentId
        val disciplineCodes = request.disciplineCodes

        val student = studentsRepository.findById(studentId.toLong())
        if (!student.isPresent){
            return Pair(false, "Student not present")
        }

        val notFoundDisciplines = mutableListOf<String>()
        var message = "All disciplines were added"
        for (disciplineCode in disciplineCodes){
            val discipline = disciplinesRepository.findByDisciplineCode(disciplineCode)
            if (!discipline.isPresent){
                notFoundDisciplines.add(disciplineCode)
                message = "Not all disciplines were added: "
            }
            else{
                if (student.get().disciplines.contains(discipline.get())){
                    message = "Not all disciplines were added: "
                    notFoundDisciplines.add("This discipline already exists for student: $disciplineCode")
                }
                else{
                    student.get().disciplines.add(discipline.get())
                }
            }
        }

        studentsRepository.save(student.get())
        if(message != "All disciplines were added"){
            if (notFoundDisciplines.isNotEmpty()){
                for (disciplineCode in notFoundDisciplines){
                    message += disciplineCode
                    message += " "
                }
            }
            return Pair(false, message)
        }

        return Pair(true, message)
    }

    override fun removeDisciplinesForStudent(request: PatchStudentDisciplinesRequest): Pair<Boolean,String>{
        val studentId = request.studentId
        val disciplineCodes = request.disciplineCodes

        val student = studentsRepository.findById(studentId.toLong())
        if (!student.isPresent){
            return Pair(false, "Student not present")
        }

        val notAddedDisciplines = mutableListOf<String>()
        var message = "All disciplines were removed"
        for (disciplineCode in disciplineCodes){
            val discipline = disciplinesRepository.findByDisciplineCode(disciplineCode)
            if (!discipline.isPresent){
                notAddedDisciplines.add(disciplineCode)
                message = "Not all disciplines were removed. Discipline does not exist"
            }
            else{
                if (!student.get().disciplines.contains(discipline.get())){
                    message = "Not all disciplines were removed. Discipline does not exist inside students list"
                }
                student.get().disciplines.remove(discipline.get())
            }
        }

        studentsRepository.save(student.get())
        if(message != "All disciplines were removed"){
            return Pair(false, message)
        }

        return Pair(true, message+notAddedDisciplines.toString())
    }

    override fun getAllStudents(
        page: Optional<Int>,
        nrItems: Optional<Int>,
        lastName: Optional<String>,
        group: Optional<String>,
        studyCycle: Optional<CycleStudy>,
        yearDegree: Optional<Int>
    ): StudentsResponse {
        var nrPage = DEFAULT_PAGE
        var nrOfItems = DEFAULT_ITEMS_PER_PAGE
        if (page.isPresent){
            nrPage = page.get()
        }
        if (nrItems.isPresent){
            nrOfItems = nrItems.get()
            if(nrItems.get() > MAX_NUMBER_OF_ITEMS){
                nrOfItems = MAX_NUMBER_OF_ITEMS
            }

        }

        var last_name: String? = null
        if (lastName.isPresent){
            if (lastName.get().length > MAX_NAME_LENGTH){
                return StudentsResponse(
                    message = "Last name too long!",
                    students = mutableListOf(),
                    _links = null,
                )
            }
            if (!validateName(lastName.get())){
                return StudentsResponse(
                    message = "Invalid name!",
                    students = mutableListOf(),
                    _links = null,
                )
            }
            last_name = lastName.get()
        }

        var group_name: String? = null
        if (group.isPresent){
            if (group.get().length > GROUP_LENGTH){
                return StudentsResponse(
                    message = "Invalid group!",
                    students = mutableListOf(),
                    _links = null,
                )
            }
            group_name = group.get()
        }

        var studyCycle_name: CycleStudy? = null
        if (studyCycle.isPresent){
            studyCycle_name = studyCycle.get()
        }

        var yearDegree_value: Int? = null
        if (yearDegree.isPresent){
            if (yearDegree.get() < 0){
                return StudentsResponse(
                    message = "Year degree should be greater than 0!",
                    students = mutableListOf(),
                    _links = null,
                )
            }
            if (yearDegree.get() > 4){
                return StudentsResponse(
                    message = "Invalid year! Should be lower than 4.",
                    students = mutableListOf(),
                    _links = null,
                )
            }
            yearDegree_value = yearDegree.get()
        }

        if (last_name != null && group_name != null && studyCycle_name != null && yearDegree_value != null){
            val students = this.studentsRepository.findStudentByLastNameAndGroupNameAndYearDegreeAndStudyCycle(last_name, group_name, yearDegree_value, studyCycle_name, PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }
        if (last_name != null && group_name != null && studyCycle_name != null){
            val students = this.studentsRepository.findStudentByLastNameAndGroupNameAndStudyCycle(last_name, group_name, studyCycle_name, PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }
        if (last_name != null && group_name != null && yearDegree_value != null){
            val students = this.studentsRepository.findStudentByLastNameAndGroupNameAndYearDegree(last_name, group_name, yearDegree_value, PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }
        if (last_name != null && studyCycle_name != null && yearDegree_value != null) {
            val students = this.studentsRepository.findStudentByLastNameAndStudyCycleAndYearDegree(last_name, studyCycle_name, yearDegree_value, PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }

        if (group_name != null && studyCycle_name != null && yearDegree_value != null) {
            val students = this.studentsRepository.findStudentByGroupNameAndStudyCycleAndYearDegree(group_name, studyCycle_name, yearDegree_value, PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }

        if (last_name != null && group_name != null) {
            val students = this.studentsRepository.findStudentByLastNameAndGroupName(last_name, group_name, PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }

        if (last_name != null && studyCycle_name != null) {
            val students = this.studentsRepository.findStudentByLastNameAndStudyCycle(last_name, studyCycle_name,PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }

        if (last_name != null && yearDegree_value != null) {
            val students = this.studentsRepository.findStudentByLastNameAndYearDegree(last_name, yearDegree_value, PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }

        if (group_name != null && studyCycle_name != null) {
            val students = this.studentsRepository.findStudentByGroupNameAndStudyCycle(group_name, studyCycle_name, PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }

        if (group_name != null && yearDegree_value != null) {
            val students = this.studentsRepository.findStudentByGroupNameAndYearDegree(group_name, yearDegree_value, PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }

        if (studyCycle_name != null && yearDegree_value != null) {
            val students = this.studentsRepository.findStudentByStudyCycleAndYearDegree(studyCycle_name, yearDegree_value, PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }

        if (last_name != null) {
            if (last_name.length <= 4){
                val students = this.studentsRepository.findAll(PageRequest.of(nrPage, nrOfItems))
                return identifyPartialMatchingByLastName(last_name, students)
            }
            val students = this.studentsRepository.findStudentByLastName(last_name, PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }

        if (group_name != null) {
            val students = this.studentsRepository.findStudentByGroupName(group_name, PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }

        if (studyCycle_name != null) {
            val students = this.studentsRepository.findStudentByStudyCycle(studyCycle_name, PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }

        if (yearDegree_value != null) {
            val students = this.studentsRepository.findStudentByYearDegree(yearDegree_value, PageRequest.of(nrPage, nrOfItems))
            return fromPageOfStudentsToListOfStudentsResponse(students)
        }

        val students =  this.studentsRepository.findAll(PageRequest.of(nrPage, nrOfItems))
        return fromPageOfStudentsToListOfStudentsResponse(students)
    }

    fun identifyPartialMatchingByLastName(last_name: String, students: Page<Student>): StudentsResponse{
        val partialStudents = mutableListOf<StudentResponse>()
        for(student in students){
            if (last_name in student.lastName){
                val studentResp = StudentResponse(
                    student = student,
                    message = "Found student by partial matching of lastname.",
                    _links = null
                )
                partialStudents.add(studentResp)
            }
        }
        return StudentsResponse(
            message = "Found students.",
            students = partialStudents,
            _links = null
        )
    }

    fun fromPageOfStudentsToListOfStudentsResponse(students: Page<Student>): StudentsResponse{
        val studentResponses = mutableListOf<StudentResponse>()
        for(student in students){
            val studentResp = StudentResponse(
                student = student,
                message = "Found student",
                _links = null
            )
            studentResponses.add(studentResp)
        }
        return StudentsResponse(
            message = "Found students.",
            students = studentResponses,
            _links = null
        )
    }

    override fun getStudentByEmail(email: String): StudentResponse {
        if (validateEmail(email) == false){
            return StudentResponse(
                student = null,
                message = "Invalid email"
            )
        }
        val result = studentsRepository.findStudentByEmail(email)
        if (result.isPresent){
            return StudentResponse(
                student = result.get(),
                message = "Student found"
            )
        }
       return StudentResponse(
           student = null,
           message = "Student not found"
       )
    }

    override fun removeDisciplineForAllStudents(disciplineCode: String): DeleteDisciplineFromStudents {
        val discipline = this.disciplinesRepository.findByDisciplineCode(disciplineCode)
        if(discipline.isPresent){
            val students = discipline.get().students
            for (student in students){
                 this.removeDisciplinesForStudent(
                    PatchStudentDisciplinesRequest(
                        studentId = student.studentId,
                        disciplineCodes = mutableListOf(disciplineCode)
                    )
                 )
            }


            return DeleteDisciplineFromStudents(
                message = "Discipline removed from all students!",
                _links = null
            )
        }
        else{
            return DeleteDisciplineFromStudents(message = "Discipline does not exist!", _links = null)
        }
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
        val emailRegex = Regex("^[a-zA-Z0-9]+\\.[a-zA-Z0-9]+@student\\.tuiasi\\.ro$")
        return email.matches(emailRegex)
    }

    fun validateStudent(student: Student): InsertStudentResponse{
        if (!validateEmail(student.email)){
            return InsertStudentResponse(
                message = "Invalid email!"
            )
        }
        if (student.lastName.length > MAX_NAME_LENGTH){
            return InsertStudentResponse(
                message = "Last name too long!"
            )
        }

        if (student.firstName.length > MAX_NAME_LENGTH){
            return InsertStudentResponse(
                message = "First name too long!"
            )
        }

        if (!validateName(student.firstName)){
            return InsertStudentResponse(
                message = "Invalid first name for student!"
            )
        }

        if (!validateName(student.lastName)){
            return InsertStudentResponse(
                message = "Invalid last name for student!"
            )
        }

        if (student.groupName.length != GROUP_LENGTH){
            return InsertStudentResponse(
                message = "Invalid group!"
            )
        }

        if (student.yearDegree <= 0){
            return InsertStudentResponse(
                message = "Year degree should be greater than 0!"
            )
        }


        return InsertStudentResponse(
            message = "Student inserted with succes!"
        )
    }
}