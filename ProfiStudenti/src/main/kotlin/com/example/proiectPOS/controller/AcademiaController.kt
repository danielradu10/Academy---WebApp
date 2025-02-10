package com.example.proiectPOS.controller

import com.example.proiectPOS.interfaces.DisciplinesServiceInterface
import com.example.proiectPOS.interfaces.ProfessorServiceInterface
import com.example.proiectPOS.interfaces.StudentServiceInterface
import com.example.proiectPOS.persistance.CycleStudy
import com.example.proiectPOS.persistance.Degree
import com.example.proiectPOS.persistance.DisciplineCategory
import com.example.proiectPOS.persistance.DisciplineType
import com.example.proiectPOS.persistance.data.Professor
import com.example.proiectPOS.persistance.data.Student
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Optional


@RestController
class AcademiaController {
    @Autowired
    private lateinit var studentService: StudentServiceInterface

    @Autowired
    private lateinit var professorService: ProfessorServiceInterface

    @Autowired
    private lateinit var disciplineService: DisciplinesServiceInterface

    @GetMapping("/academia/professors/{id}")
    @Operation(
        summary = "Search a professor by ID.",
        description = "Retrieve details about a specific professor based on their unique ID.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved professor",
                content = [Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "ProfessorExample",
                            summary = "Example of a successful response",
                        )
                    ]
                )]),
            ApiResponse(responseCode = "404", description = "Professor not found", content = [Content()]),
            ApiResponse(responseCode = "422", description = "Invalid ID supplied", content = [Content()])
        ]
    )
    fun getProfessorById(
        @Parameter(description = "The ID of the professor to retrieve", required = true, example = "1")
        @PathVariable id: Int,
        httpServletRequest: HttpServletRequest): ResponseEntity<ProfessorResponse> {
        val role = httpServletRequest.getAttribute("role")
        val emailOfCaller = httpServletRequest.getAttribute("email")

        if (role == "student"){
               return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }

        val result = professorService.getProfessorById(id)
        if (role == "professor"){
            if (result!!.professor != null){
                if (emailOfCaller != result.professor!!.emailProfessor){
                    return ResponseEntity(null, HttpStatus.FORBIDDEN)
                }
            }
        }

        if (result!!.professor == null){
            if (result.message == "The id has to be greater than 0!"){
                return ResponseEntity.unprocessableEntity().body(result)
            }
            else if (result.message == "Professor not found") {
                return ResponseEntity.notFound().build()
            }
            return ResponseEntity.badRequest().build()
        }

        val linkToStudents = linkTo(methodOn(AcademiaController::class.java).getStudentsForProfessor(id, Optional.empty(), Optional.empty(), httpServletRequest)).toUri().toString()
        val linkToDisciplines = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForProfessors(id, Optional.empty(), Optional.empty(), httpServletRequest )).toUri().toString()
        val linkToDeleteProfessor = linkTo(methodOn(AcademiaController::class.java).deleteProfessorById(id,httpServletRequest)).toUri().toString()
        val links = ProfessorLinks(
            professorStudentsLink = Link(linkToStudents),
            professorDisciplinesLink = Link(linkToDisciplines),
            professorDelete = Link(linkToDeleteProfessor, "DELETE")
        )
        result._links = links
        return ResponseEntity.ok(result)

    }


    @GetMapping("/academia/professor")
    @Operation(
        summary = "Search a professor by email.",
        description = "Retrieve details about a specific professor based on their unique email.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200",
                description = "Successfully retrieved professor.",
                content = [Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "ProfessorExample",
                            summary = "Example of a successful response",
                        )
                    ]
                )]),
            ApiResponse(responseCode = "404", description = "Professor not found.", content = [Content()]),
            ApiResponse(responseCode = "422", description = "Invalid email supplied.", content = [Content()])
        ]
    )
    fun getProfessorByEmail(
        @Parameter(description = "The email of the professor to retrieve", required = true)
        @RequestParam(required = true) email: String,
        httpServletRequest: HttpServletRequest): ResponseEntity<ProfessorResponse> {
        val role = httpServletRequest.getAttribute("role")
        val emailOfCaller = httpServletRequest.getAttribute("email")
        if (role == "student"){
            return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }
        if (role == "professor"){
            if (emailOfCaller != email){
                return ResponseEntity(null, HttpStatus.FORBIDDEN)
            }
        }
        val result = professorService.getProfessorByEmail(email)
        if (result!!.professor == null){
            if (result.message == "Invalid email"){
                return ResponseEntity.unprocessableEntity().body(result)
            }
            else if (result.message == "Professor not found") {
                return ResponseEntity.notFound().build()
            }
            return ResponseEntity.badRequest().build()
        }

        val linkToStudents = linkTo(methodOn(AcademiaController::class.java).getStudentsForProfessor(result.professor!!.professorId, Optional.empty(), Optional.empty(), httpServletRequest)).toUri().toString()
        val linkToDisciplines = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForProfessors(result.professor.professorId, Optional.empty(), Optional.empty(), httpServletRequest )).toUri().toString()
        val linkToDeleteProfessor = linkTo(methodOn(AcademiaController::class.java).deleteProfessorById(result.professor.professorId, httpServletRequest)).toUri().toString()
        val links = ProfessorLinks(
            professorStudentsLink = Link(linkToStudents),
            professorDisciplinesLink = Link(linkToDisciplines),
            professorDelete = Link(linkToDeleteProfessor, "DELETE")
        )
        result._links = links
        return ResponseEntity.ok(result)
    }

    @GetMapping("/academia/disciplines/{id}")
    @Operation(
        summary = "Search a discipline by ID.",
        description = "Retrieve the details about a discipline by its unique id.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200",
                description = "Successfully retrieved discipline.",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                        ]
                    )
                ]),
            ApiResponse(responseCode = "404", description = "Discipline not found.", content = [Content()]),
            ApiResponse(responseCode = "422", description = "Invalid code supplied.", content = [Content()])
        ]
    )
    fun getDisciplineById(
        @Parameter(description = "The code of the discipline to be retrieved", required = true, example = "AM")
        @PathVariable id: String,
        httpServletRequest: HttpServletRequest): ResponseEntity<DisciplineResponse> {
        val role = httpServletRequest.getAttribute("role")
        val emailCaller = httpServletRequest.getAttribute("email")

        if (role == "student"){
            val studentCaller = this.studentService.getStudentByEmail(emailCaller.toString())
            if (studentCaller.student != null)
            {
                val disciplines =  studentCaller.student.disciplines
                val disciplineCodes = mutableListOf<String>()
                for (discipline in disciplines){
                    disciplineCodes.add(discipline.disciplineCode)
                }
                if(!disciplineCodes.contains(id)){
                    return ResponseEntity(null, HttpStatus.FORBIDDEN)
                }
            }
            else{
                return ResponseEntity(null, HttpStatus.FORBIDDEN)
            }
        }

        val result = disciplineService.getDisciplineByCode(id)
        if (result!!.discipline == null){
            if(result.message == "Too many characters!"){
                return ResponseEntity.unprocessableEntity().body(result)
            }
            else if (result.message == "Discipline not found"){
                return ResponseEntity.notFound().build()
            }
            return ResponseEntity.badRequest().build()
        }

        val selfLink = linkTo(methodOn(AcademiaController::class.java).getDisciplineById(id, httpServletRequest)).toUri().toString()
        val parentLink = linkTo(methodOn(AcademiaController::class.java).getDisciplines(Optional.empty(), Optional.empty(), null, null, httpServletRequest)).toUri().toString()
        val professorId = result.discipline!!.professor.professorId
        val linkToProfessorTitular = linkTo(methodOn(AcademiaController::class.java).getProfessorById(professorId, httpServletRequest)).toUri().toString()
        val linkToDeleteDiscipline = linkTo(methodOn(AcademiaController::class.java).deleteDisciplineByCode(id, httpServletRequest)).toUri().toString()
        val linkToRemoveDisciplineFromStudents = linkTo(methodOn(AcademiaController::class.java).removeDisciplineFromAllStudents(id, httpServletRequest)).toUri().toString()
        val disciplineLinks = DisciplineLinks(
            self = Link(selfLink),
            parent = Link(parentLink),
            linkToProfessorTitular = Link(linkToProfessorTitular),
            linkToDeleteDiscipline = Link(linkToDeleteDiscipline, "DELETE"),
            linkToRemoveDisciplineFromStudents = Link(linkToRemoveDisciplineFromStudents, "PATCH")
        )
        result._links = disciplineLinks
        return ResponseEntity.ok().body(result)
    }

    @GetMapping("/academia/students/{id}")
    @Operation(
        summary = "Search a student by ID.",
        description = "Retrieves a student searched by its unique identifier.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200",
                description = "Successfully retrieved student.",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                            )
                        ]
                    )
                ]),
            ApiResponse(responseCode = "404", description = "Student not found.", content = [Content()]),
            ApiResponse(responseCode = "422", description = "Invalid id supplied.", content = [Content()])
        ]
    )
    fun getStudentById(
        @Parameter(description = "The ID of the student to be retrieved.", required = true)
        @PathVariable id: Int, httpServletRequest: HttpServletRequest): ResponseEntity<StudentResponse> {
        val role = httpServletRequest.getAttribute("role")
        val emailOfCaller = httpServletRequest.getAttribute("email")
        if (role == "professor"){
            return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }
        val result = studentService.getStudentById(id.toLong())
        if (role == "student"){
            if (result!!.student != null){
                if (emailOfCaller != result.student!!.email){
                    return ResponseEntity(null, HttpStatus.FORBIDDEN)
                }
            }
        }
        if (result!!.student == null){
            if (result.message == "The id has to be greater than 0"){
                return ResponseEntity.unprocessableEntity( ).body(result)
            }
            else if (result.message == "Student not found") {
                return ResponseEntity.notFound().build()
            }
            return ResponseEntity.badRequest().build()
        }
        val removeStudent = null // TODO - implement this method
        val studentDisciplines = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForStudent(id, Optional.empty(), Optional.empty(), httpServletRequest)).toUri().toString()
        result._links = StudentLinks(
            studentDisciplines = Link(studentDisciplines),
            removeStudent = removeStudent
        )
        return ResponseEntity.ok(result)
    }

    @GetMapping("/academia/student")
    @Operation(
        summary = "Search a student by email.",
        description = "Retrieves a student searched by its unique email.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200",
                description = "Successfully retrieved student.",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                """
                                    {
                                        "student": {
                                            "studentId": 1,
                                            "lastName": "Vieru",
                                            "firstName": "Iosif",
                                            "email": "iosif.vieru@student.tuiasi.ro",
                                            "studyCycle": "Bachelor",
                                            "groupName": "1410A",
                                            "yearDegree": 4
                                        },
                                        "message": "Student found"
                                    }                             
                                """
                            )
                        ]
                    )
                ]),
            ApiResponse(responseCode = "404", description = "Student not found.", content = [Content()]),
            ApiResponse(responseCode = "422", description = "Invalid email supplied.", content = [Content()])
        ]
    )
    fun getStudentByEmail(@RequestParam(required = true) email: String,
                          httpServletRequest: HttpServletRequest): ResponseEntity<StudentResponse>{
        println("Inside get student by email")
        val role = httpServletRequest.getAttribute("role")
        val emailOfCaller = httpServletRequest.getAttribute("email")
        if (role == "student"){
            if (emailOfCaller != email){
                return ResponseEntity(null, HttpStatus.FORBIDDEN)
            }
        }
        if (role == "professor"){
            return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }

        val result = studentService.getStudentByEmail(email)
        println("Result: ")
        println(result)


        if (result.student == null){
            if (result.message == "Invalid email"){
                return ResponseEntity.unprocessableEntity().body(result)
            }
            else if (result.message == "Student not found") {
                return ResponseEntity.notFound().build()
            }
            return ResponseEntity.badRequest().build()
        }

        val removeStudent = null // TODO - implement this method
        val studentDisciplines = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForStudent(result.student.studentId, Optional.empty(), Optional.empty(), httpServletRequest)).toUri().toString()
        result._links = StudentLinks(
            studentDisciplines = Link(studentDisciplines),
            removeStudent = removeStudent
        )
        return ResponseEntity.ok(result)
    }

    @GetMapping("/academia/students")
    @Operation(
        summary = "Get all students.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getAllStudents(
        @Parameter(required = false, description = "Last name of student.")
        @RequestParam(required = false) lastName: Optional<String>,
        @Parameter(required = false, description = "Group name of student.")
        @RequestParam(required = false) groupName: Optional<String>,
        @Parameter(required = false, description = "Study cycle.")
        @RequestParam(required = false) study: Optional<CycleStudy>,
        @Parameter(required = false, description = "Year degree")
        @RequestParam(required = false) yearDegree: Optional<Int>,
        @RequestParam(required = false) nrPage: Optional<Int>,
        @RequestParam(required = false) itemsPerPage: Optional<Int>,
        httpServletRequest: HttpServletRequest): ResponseEntity<StudentsResponse> {
        println("Inside get all students")
        val role = httpServletRequest.getAttribute("role")
        if (role != "admin"){
            return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }

        var students =  studentService.getAllStudents(nrPage, itemsPerPage, lastName, groupName, study, yearDegree)
        if (students.message != "Found students."){
            return ResponseEntity.unprocessableEntity().body(students)
        }

        for (student in students.students){
            val linkToStudentDisciplines = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForStudent(id = student.student!!.studentId, Optional.empty(), Optional.empty(), httpServletRequest)).toUri().toString()
            val removeStudent = null
            val studentLinks = StudentLinks(
                studentDisciplines = Link(linkToStudentDisciplines),
                removeStudent = removeStudent
            )
            student._links = studentLinks
        }
        var page = 0
        if (nrPage.isPresent){
            page = nrPage.get()
        }

        val nextPage = linkTo(methodOn(AcademiaController::class.java).
            getAllStudents(lastName, groupName, study, yearDegree, Optional.of(page+1), itemsPerPage, httpServletRequest)).toUri().toString()
        val previousPage = linkTo(methodOn(AcademiaController::class.java).
            getAllStudents(lastName, groupName, study, yearDegree, if (page == 0) Optional.of(0) else Optional.of(page-1), itemsPerPage, httpServletRequest)).toUri().toString()
        students._links = StudentsResponseLinks(
            nextPage = Link(nextPage),
            previousPage = Link(previousPage)
        )

        return ResponseEntity.ok(students)
    }

    @GetMapping("/academia/professors")
    @Operation(
        summary = "Filter professors by different queries.",
        description = "Retrieves a list of professors filtered by different queries",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully returned the list of professors.",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "422",  description = "Invalid parameters.",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                """
                                    {
                                        "professors": "null",
                                        "message": "Last name is mandatory",
                                        "linkToNextPage": "null"
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    fun getProfessors(
        @Parameter(description = "Defines the academic rank.", required = false)
        @RequestParam(required = false) acadRank: Degree?,
        @Parameter(description = "Defines the last name.", required = false)
        @RequestParam(required = false) lastName: String?,
        @Parameter(description = "Defines the first name.", required = false)
        @RequestParam(required = false) firstName: String?,
        @Parameter(description = "Defines the page number.", required = false)
        @RequestParam(required = false) page: Optional<Int>,
        @Parameter(description = "Defines the number of items.", required = false)
        @RequestParam(required = false) itemsPerPage: Optional<Int>,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<GetProfessorsResponse> {
        val role = httpServletRequest.getAttribute("role")
        if (role != "admin"){
            return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }
        val result = professorService.getAllProfessors(acadRank, lastName, firstName, page, itemsPerPage)
        if (result.message == "Last name is mandatory!" ||
            result.message == "Do not pass empty data!" ||
            result.message == "Too many characters for first name!" ||
            result.message == "Too many characters for last name!" ||
            result.message == "Only alphabetic characters accepted"){
            return ResponseEntity.unprocessableEntity().body(result)
        }
        else{
            if (result.professors.content.isEmpty()){
                return ResponseEntity.ok().body(
                    GetProfessorsResponse(
                        professors = Page.empty(),
                        message = "Didn't find any professor for this specific query!",
                        _links = null
                    )
                )
            }
            var nrPage = 0
            if (page.isPresent){
                nrPage = page.get()
            }
            val nextPageLink =  linkTo(methodOn(AcademiaController::class.java).getProfessors(acadRank, lastName, firstName, Optional.of(nrPage+1), itemsPerPage, httpServletRequest)).toUri().toString()
            val previousPageLink = linkTo(methodOn(AcademiaController::class.java).getProfessors(acadRank, lastName, firstName, ( if (nrPage == 0) Optional.of(0) else Optional.of(nrPage-1)), itemsPerPage, httpServletRequest)).toUri().toString()
            val professorsLinks = GetProfessorsLinks(
                linkToNextPage = Link(nextPageLink),
                linkToPreviousPage = Link(previousPageLink)
            )
            result._links = professorsLinks
            return ResponseEntity.ok(result)
        }
    }


    @GetMapping("/academia/disciplines")
    @Operation(
        summary = "Get all disciplines.",
        description = "Retrieves a list of disciplines filtered by different queries",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getDisciplines(
        @Parameter(description = "Defines the page number.", required = false)
        @RequestParam(required = false) page: Optional<Int>,
        @Parameter(description = "Defines the number of items.", required = false)
        @RequestParam(required = false) itemsPerPage: Optional<Int>,
        @Parameter(description = "Defines the type of discipline.", required = false)
        @RequestParam(required = false) type: DisciplineType?,
        @Parameter(description = "Defines the category of discipline.", required = false)
        @RequestParam(required = false) category: DisciplineCategory?,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<DisciplinesResponse> {
        val role = httpServletRequest.getAttribute("role")
        if(role != "admin"){
            return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }
        val disciplines =  disciplineService.getAllDisciplines(page, itemsPerPage, type, category)
        val disciplineResponses = mutableListOf<DisciplineResponse>()
        for (discipline in disciplines){
            val selfLink = linkTo(methodOn(AcademiaController::class.java).getDisciplineById(discipline.disciplineCode, httpServletRequest)).toUri().toString()
            val parentLink = linkTo(methodOn(AcademiaController::class.java).getDisciplines(Optional.empty(), Optional.empty(), null, null, httpServletRequest)).toUri().toString()
            val professorId = discipline.professor.professorId
            val linkToProfessorTitular = linkTo(methodOn(AcademiaController::class.java).getProfessorById(professorId, httpServletRequest)).toUri().toString()
            val linkToDeleteDiscipline = linkTo(methodOn(AcademiaController::class.java).deleteDisciplineByCode(discipline.disciplineCode, httpServletRequest)).toUri().toString()
            val linkToRemoveDisciplineFromStudents = linkTo(methodOn(AcademiaController::class.java).removeDisciplineFromAllStudents(discipline.disciplineCode, httpServletRequest)).toUri().toString()
            val disciplineLinks = DisciplineLinks(
                self = Link(selfLink),
                parent = Link(parentLink),
                linkToProfessorTitular = Link(linkToProfessorTitular),
                linkToDeleteDiscipline = Link(linkToDeleteDiscipline, "DELETE"),
                linkToRemoveDisciplineFromStudents = Link(linkToRemoveDisciplineFromStudents, "PATCH")
            )
            var disciplineResponse = DisciplineResponse(
                message = "Discipline found",
                discipline = discipline,
                _links = disciplineLinks,
            )
            disciplineResponses.add(disciplineResponse)
        }
        var pageNr = 0
        if (page.isPresent) {
             pageNr = page.get()
        }
        var items = 1
        if (itemsPerPage.isPresent){
            items = itemsPerPage.get()
        }

        val nextPageLink = linkTo(methodOn(AcademiaController::class.java).getDisciplines(Optional.of(pageNr+1), Optional.of(items), type, category, httpServletRequest)).toUri().toString()
        val previousPageLink = linkTo(methodOn(AcademiaController::class.java).getDisciplines(if (pageNr != 0) Optional.of(pageNr-1) else Optional.of(0), Optional.of(items), type, category, httpServletRequest)).toUri().toString()
        val disciplinesResponseLinks = DisciplinesResponseLinks(
            nextPage = Link(nextPageLink),
            previousPage = Link(previousPageLink)
        )

        var response = DisciplinesResponse(
            message = "Found disciplines",
            disciplines = disciplineResponses,
            _links = disciplinesResponseLinks
        )
        return ResponseEntity(response, HttpStatus.OK)
    }


    @GetMapping("/academia/professors/{id}/disciplines")
    @Operation(
        summary = "Get all professor's disciplines.",
        description = "Retrieves all the disciplines for a professor searched by the unique id.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Returns a list of disciplines",
            content = [
                 Content(
                     mediaType = "application/json",
                     examples = [
                     ]
                 )
            ]
        ),
        ApiResponse(
            responseCode = "403",
            description = "Return forbidden in case of wrong role."
        )
    )
    fun getDisciplinesForProfessors(@PathVariable id: Int,
                                    @RequestParam page: Optional<Int>,
                                    @RequestParam itemsPerPage: Optional<Int>,
                                    httpServletRequest: HttpServletRequest): ResponseEntity<DisciplinesResponse> {
        val role = httpServletRequest.getAttribute("role")
        if (role != "student"){
            val professor = professorService.getProfessorById(id)

            if (professor!!.professor == null){
                return ResponseEntity.notFound().build()
            }

            if (role == "professor") {
                if (professor.professor!!.professorId != id) {
                    return ResponseEntity(null, HttpStatus.FORBIDDEN)
                }
            }

            val disciplines = disciplineService.getDisciplinesByIdTitular(professor.professor!!, page, itemsPerPage)
            val disciplinesResponses = mutableListOf<DisciplineResponse>()
            disciplines.content.forEach {
                val selfLink = linkTo(methodOn(AcademiaController::class.java).getDisciplineById(it.disciplineCode, httpServletRequest)).toUri().toString()
                val parentLink = linkTo(methodOn(AcademiaController::class.java).getDisciplines(Optional.empty(), Optional.empty(), null, null, httpServletRequest)).toUri().toString()
                val professorId = it.professor.professorId
                val linkToProfessorTitular = linkTo(methodOn(AcademiaController::class.java).getProfessorById(professorId, httpServletRequest)).toUri().toString()
                val linkToDeleteDiscipline = linkTo(methodOn(AcademiaController::class.java).deleteDisciplineByCode(it.disciplineCode, httpServletRequest)).toUri().toString()
                val linkToRemoveDisciplineFromStudents = linkTo(methodOn(AcademiaController::class.java).removeDisciplineFromAllStudents(it.disciplineCode, httpServletRequest)).toUri().toString()
                val disciplineLinks = DisciplineLinks(
                    self = Link(selfLink),
                    parent = Link(parentLink),
                    linkToProfessorTitular = Link(linkToProfessorTitular),
                    linkToDeleteDiscipline = Link(linkToDeleteDiscipline, "DELETE"),
                    linkToRemoveDisciplineFromStudents = Link(linkToRemoveDisciplineFromStudents, "PATCH")
                )
                var disciplineResponse = DisciplineResponse(
                    message = "Discipline found",
                    discipline = it,
                    _links = disciplineLinks,
                )
                disciplinesResponses.add(disciplineResponse)
            }

            var pageNr = 0
            if (page.isPresent) {
                pageNr = page.get()
            }
            var items = 1
            if (itemsPerPage.isPresent){
                items = itemsPerPage.get()
            }
            val nextPageLink = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForProfessors(id, Optional.of(pageNr+1), Optional.of(items), httpServletRequest)).toUri().toString()
            val previousPageLink = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForProfessors(id, if (pageNr != 0) Optional.of(pageNr-1) else Optional.of(0), Optional.of(items),httpServletRequest)).toUri().toString()
            val disciplinesResponseLinks = DisciplinesResponseLinks(
                nextPage = Link(nextPageLink),
                previousPage = Link(previousPageLink)
            )

            var response = DisciplinesResponse(
                message = "Found disciplines",
                disciplines = disciplinesResponses,
                _links = disciplinesResponseLinks
            )
            return ResponseEntity(response, HttpStatus.OK)
        } else{
            return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }
    }

    @GetMapping("/academia/professors/{id}/students")
    fun getStudentsForProfessor(@PathVariable id: Int,
                                @RequestParam page: Optional<Int>,
                                @RequestParam itemsPerPage: Optional<Int>,
                                httpServletRequest: HttpServletRequest): ResponseEntity<StudentsResponse>{
        val role = httpServletRequest.getAttribute("role")
        val emailCaller = httpServletRequest.getAttribute("email")
        if (role == "student"){
            return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }

        var nrPage = 0
        if (page.isPresent){
            nrPage = page.get()
        }
        var nrItems = 3
        if (itemsPerPage.isPresent){
            nrItems = itemsPerPage.get()
        }
        if (nrPage < 0 || nrItems < 0){
            return ResponseEntity(
                StudentsResponse(
                    message = "Page number should be > 0",
                    students = mutableListOf(),
                    _links = null
                ),
                HttpStatus.UNPROCESSABLE_ENTITY
            )
        }


        val professorFound = this.professorService.getProfessorById(id)
        if (professorFound!!.professor == null){
            return ResponseEntity(null, HttpStatus.NOT_FOUND)
        }
        if (role == "professor"){
            val professorFoundByEmail = this.professorService.getProfessorByEmail(email = emailCaller.toString())
            if (professorFoundByEmail?.professor != null && professorFoundByEmail.professor.professorId != id){
                return ResponseEntity(null, HttpStatus.FORBIDDEN)
            }
        }

        val disciplines = this.disciplineService.getAllDisciplinesByIdTitular(professorFound.professor!!)
        val studentResponses = mutableListOf<StudentResponse>()
        for (discipline in disciplines){
            val students = discipline.students
            for (student in students){
                val studentDisciplines = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForStudent(student.studentId, Optional.empty(), Optional.empty(), httpServletRequest)).toUri().toString()
                val studentResp = StudentResponse(
                    student = student,
                    message ="Found student ",
                    _links = StudentLinks(
                        studentDisciplines = Link(studentDisciplines),
                        removeStudent = null
                    )
                )
                studentResponses.add(studentResp)
            }
        }
        val startIndex = nrPage * nrItems
        val endIndex = minOf(startIndex + nrItems, studentResponses.size)
        if (startIndex >= studentResponses.size) {
            return ResponseEntity(StudentsResponse(
                message = "Found students",
                students = mutableListOf(),
                _links = StudentsResponseLinks(
                    nextPage = null,
                    previousPage = null
                )
            ), HttpStatus.OK)
        }

        val studentsResponse = StudentsResponse(
            message = "Found students",
            students = studentResponses.subList(startIndex, endIndex),
            _links = StudentsResponseLinks(
                nextPage = Link(linkTo(methodOn(AcademiaController::class.java).getStudentsForProfessor(id, Optional.of(nrPage+1), itemsPerPage, httpServletRequest)).toUri().toString()),
                previousPage = if (nrPage ==0) null else Link(linkTo(methodOn(AcademiaController::class.java).getStudentsForProfessor(id, Optional.of(nrPage-1), itemsPerPage, httpServletRequest)).toUri().toString())
            )
        )
        return ResponseEntity(studentsResponse, HttpStatus.OK)
    }


    @GetMapping("/academia/students/{id}/disciplines")
    @Operation(
        summary = "Get all student's disciplines.",
        description = "Get all disciplines of a student searched by its unique ID.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Returns the list of disciplines for a student."
        )
    )
    fun getDisciplinesForStudent(@PathVariable id: Int,
                                 @RequestParam page: Optional<Int>,
                                 @RequestParam itemsPerPage: Optional<Int>,
                                 httpServletRequest: HttpServletRequest): ResponseEntity<DisciplinesResponse> {
        val role = httpServletRequest.getAttribute("role")
        if (role == "professor"){
            return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }


        val disciplines = studentService.getDisciplinesForStudent(id)
        if (disciplines.message == "Student id should be greater than 0"){
            return ResponseEntity(null, HttpStatus.UNPROCESSABLE_ENTITY)
        }

        if (role == "student"){
            val studentFound = studentService.getStudentById(id.toLong())
            if (studentFound?.student == null){
                return ResponseEntity(null, HttpStatus.NOT_FOUND)
            }
            val emailCaller = httpServletRequest.getAttribute("email")
            if (studentFound.student.email != emailCaller){
                return ResponseEntity(null, HttpStatus.FORBIDDEN)
            }
        }

        var nrPage = 0
        if (page.isPresent){
            nrPage = page.get()
        }
        var nrItems = 3
        if (itemsPerPage.isPresent){
            nrItems = itemsPerPage.get()
        }
        if (nrPage < 0 || nrItems < 0){
            return ResponseEntity(
                DisciplinesResponse(
                    message = "Page number should be > 0",
                    disciplines = mutableListOf(),
                    _links = null
                ),
                HttpStatus.UNPROCESSABLE_ENTITY
            )
        }

        val disciplineResponses = mutableListOf<DisciplineResponse>()
        for (discipline in disciplines.disciplines){
            val selfLink = linkTo(methodOn(AcademiaController::class.java).getDisciplineById(discipline.discipline!!.disciplineCode, httpServletRequest)).toUri().toString()
            val parentLink = linkTo(methodOn(AcademiaController::class.java).getDisciplines(Optional.empty(), Optional.empty(), null, null, httpServletRequest)).toUri().toString()
            val professorId = discipline.discipline.professor.professorId
            val linkToProfessorTitular = linkTo(methodOn(AcademiaController::class.java).getProfessorById(professorId, httpServletRequest)).toUri().toString()
            val linkToDeleteDiscipline = linkTo(methodOn(AcademiaController::class.java).deleteDisciplineByCode(discipline.discipline.disciplineCode, httpServletRequest)).toUri().toString()
            val linkToRemoveDisciplineFromStudents = linkTo(methodOn(AcademiaController::class.java).removeDisciplineFromAllStudents(discipline.discipline.disciplineCode, httpServletRequest)).toUri().toString()
            val disciplineLinks = DisciplineLinks(
                self = Link(selfLink),
                parent = Link(parentLink),
                linkToProfessorTitular = Link(linkToProfessorTitular),
                linkToDeleteDiscipline = Link(linkToDeleteDiscipline, "DELETE"),
                linkToRemoveDisciplineFromStudents = Link(linkToRemoveDisciplineFromStudents, "PATCH")
            )
            val disciplineResponse = DisciplineResponse(
                message = "Discipline found",
                discipline = discipline.discipline,
                _links = disciplineLinks,
            )
            disciplineResponses.add(disciplineResponse)
        }

        val startIndex = nrPage * nrItems
        val endIndex = minOf(startIndex + nrItems, disciplineResponses.size)
        if (startIndex >= disciplineResponses.size) {
            val previousPageLink = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForStudent(id, Optional.of(nrPage-1), itemsPerPage, httpServletRequest)).toUri().toString()
            return ResponseEntity(DisciplinesResponse(
                message = "Start index too big.",
                disciplines = mutableListOf(),
                _links = DisciplinesResponseLinks(
                    nextPage = null,
                    previousPage = Link(previousPageLink)
                )
            ), HttpStatus.OK)
        }


        val nextPageLink = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForStudent(id, Optional.of(nrPage+1), itemsPerPage, httpServletRequest)).toUri().toString()
        val previousPageLink = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForStudent(id, Optional.of(nrPage-1), itemsPerPage, httpServletRequest)).toUri().toString()
        val disciplinesResponseLinks = DisciplinesResponseLinks(
            nextPage = Link(nextPageLink),
            previousPage = if (nrPage == 0) null else Link(previousPageLink)
        )

        val response = DisciplinesResponse(
            message = "Found disciplines",
            disciplines = disciplineResponses.subList(startIndex, endIndex),
            _links = disciplinesResponseLinks
        )
        return ResponseEntity.ok(response)
    }


    @PostMapping("/academia/students/insert")
    @Operation(
        summary = "Insert student.",
        description = "Inserts a student.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun insertStudent(@RequestBody student: Student, httpServletRequest: HttpServletRequest): ResponseEntity<InsertStudentResponse>{
        var role = httpServletRequest.getAttribute("role")
        if (role == "admin"){
            val result = studentService.insertStudent(student)
            if (result.message == "Student inserted with succes!"){
                val linkToStudent = linkTo(methodOn(AcademiaController::class.java).getStudentById(result.studentId, httpServletRequest)).toUri().toString()
                val linkToStudentDisciplines = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForStudent(result.studentId, Optional.empty(), Optional.empty(), httpServletRequest)).toUri().toString()
                val linkToDeleteStudent = null
                val allLinks = InsertStudentLinks(
                    linkToStudent = Link(linkToStudent),
                    linkToStudentDisciplines = Link(linkToStudentDisciplines),
                    linkToDeleteStudent = null
                )
                result._links = allLinks
                return ResponseEntity.status(HttpStatus.CREATED).body(result)
            }
            if (result.message == "Email already exists!"){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(result)
            }
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(result)
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null)
    }

    @PostMapping("/academia/professors/insert")
    @Operation(
        summary = "Insert professor.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun insertProfessor(@RequestBody professor: Professor, httpServletRequest: HttpServletRequest): ResponseEntity<InsertProfessorResponse>{
        val role = httpServletRequest.getAttribute("role")
        if (role == "admin"){
            val result = professorService.insertProfessor(professor)
            if (result.message != "Professor inserted successfully!"){
                if (result.message == "The email already exists"){
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(result)
                }
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(result)
            }
            val linkToProfessor = linkTo(methodOn(AcademiaController::class.java).getProfessorById(result.idProfessor, httpServletRequest)).toUri().toString()
            val linkToProfessorDisciplines = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForProfessors(result.idProfessor, Optional.empty(), Optional.empty(), httpServletRequest)).toUri().toString()
            val linkToProfessorStudents = null
            val linkToDelete = linkTo(methodOn(AcademiaController::class.java).deleteProfessorById(result.idProfessor, httpServletRequest)).toUri().toString()
            val allLinks = InsertProfessorLinks(
                linkToProfessor = Link(linkToProfessor),
                linkToProfessorDisciplines = Link(linkToProfessorDisciplines),
                linkToProfessorStudents = linkToProfessorStudents,
                linkToDeleteProfessor = Link(linkToDelete, "DELETE")
            )
            result._links = allLinks
            return ResponseEntity.status(HttpStatus.CREATED).body(result)
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(InsertProfessorResponse(
            message = "Access denied!"
        ))
    }

    @PostMapping("/academia/disciplines/insert")
    @Operation(
        summary = "Insert discipline.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun insertDiscipline(@RequestBody discipline: DisciplineInsertRequest,
                         httpServletRequest: HttpServletRequest): ResponseEntity<DisciplineResponse>{
        val role = httpServletRequest.getAttribute("role")
        if (role == "admin")
        {
            val response = disciplineService.insertDiscipline(discipline)
            if (response.message == "Discipline inserted with success!"){
                val selfLink = linkTo(methodOn(AcademiaController::class.java).getDisciplineById(response.discipline!!.disciplineCode, httpServletRequest)).toUri().toString()
                val parentLink = linkTo(methodOn(AcademiaController::class.java).getDisciplines(Optional.empty(), Optional.empty(), null, null, httpServletRequest)).toUri().toString()
                val professorId = response.discipline.professor.professorId
                val linkToProfessorTitular = linkTo(methodOn(AcademiaController::class.java).getProfessorById(professorId, httpServletRequest)).toUri().toString()
                val linkToDeleteDiscipline = linkTo(methodOn(AcademiaController::class.java).deleteDisciplineByCode(response.discipline.disciplineCode, httpServletRequest)).toUri().toString()
                val linkToRemoveDisciplineFromStudents = linkTo(methodOn(AcademiaController::class.java).removeDisciplineFromAllStudents(response.discipline.disciplineCode, httpServletRequest)).toUri().toString()
                val disciplineLinks = DisciplineLinks(
                    self = Link(selfLink),
                    parent = Link(parentLink),
                    linkToProfessorTitular = Link(linkToProfessorTitular),
                    linkToDeleteDiscipline = Link(linkToDeleteDiscipline, "DELETE"),
                    linkToRemoveDisciplineFromStudents = Link(linkToRemoveDisciplineFromStudents, "PATCH")
                )
                return ResponseEntity(DisciplineResponse(response.message, response.discipline, disciplineLinks), HttpStatus.CREATED)
            }else if(response.message == "Too many characters!" || response.message == "Invalid year degree!" ){
                return ResponseEntity(DisciplineResponse(response.message, null, null), HttpStatus.UNPROCESSABLE_ENTITY)
            }
            else if(response.message == "The professor does not exist!"){
                return ResponseEntity(DisciplineResponse(response.message, null, null), HttpStatus.UNPROCESSABLE_ENTITY)
            }
            else{
                return ResponseEntity(DisciplineResponse(response.message, null, null), HttpStatus.OK)
            }
        }else
        {
            return ResponseEntity(DisciplineResponse("Access denied!", null, null), HttpStatus.FORBIDDEN)
        }
    }

    @PatchMapping("/academia/student/add")
    @Operation(
        summary = "Add a discipline in specific student's disciplines list.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun insertDisciplineForStudent(
        @Parameter(description = "Structure of student ID and the codes of the discipline to be removed")
        @RequestBody request: PatchStudentDisciplinesRequest,
        httpServletRequest: HttpServletRequest): ResponseEntity<PatchStudentDisciplinesResponse>{
        val role = httpServletRequest.getAttribute("role")
        if (role == "admin"){
            val response = studentService.addDisciplinesForStudent(request)
            if (response.first == true){
                val linkToStudent = linkTo(methodOn(AcademiaController::class.java).getStudentById(request.studentId, httpServletRequest)).toUri().toString()
                val linkToStudentDisciplines = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForStudent(request.studentId, Optional.empty(), Optional.empty(), httpServletRequest)).toUri().toString()
                val allLinks = PatchStudentDisciplinesResponseLinks(
                    linkToStudent = Link(linkToStudent),
                    linkToStudentDisciplines = Link(linkToStudentDisciplines)
                )
                var result = PatchStudentDisciplinesResponse(response.second, null)
                result._links = allLinks
                return ResponseEntity(result, HttpStatus.OK)
            }
            else{
                return ResponseEntity(PatchStudentDisciplinesResponse(response.second, null), HttpStatus.NOT_FOUND)
            }
        }
        else{
            return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }
    }

    @PatchMapping("/academia/student/remove")
    @Operation(
        summary = "Remove a discipline from a specific student's disciplines list.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun removeDisciplineForStudent(
        @Parameter(description = "Structure of student ID and the codes of the discipline to be removed")
        @RequestBody request: PatchStudentDisciplinesRequest,
        httpServletRequest: HttpServletRequest): ResponseEntity<PatchStudentDisciplinesResponse>{
        val role = httpServletRequest.getAttribute("role")
        if (role == "admin"){
            val response = studentService.removeDisciplinesForStudent(request)
            if (response.first == true){
                val linkToStudent = linkTo(methodOn(AcademiaController::class.java).getStudentById(request.studentId, httpServletRequest)).toUri().toString()
                val linkToStudentDisciplines = linkTo(methodOn(AcademiaController::class.java).getDisciplinesForStudent(request.studentId, Optional.empty(), Optional.empty(), httpServletRequest)).toUri().toString()
                val allLinks = PatchStudentDisciplinesResponseLinks(
                    linkToStudent = Link(linkToStudent),
                    linkToStudentDisciplines = Link(linkToStudentDisciplines)
                )
                var result = PatchStudentDisciplinesResponse(response.second, null)
                result._links = allLinks
                return ResponseEntity(result, HttpStatus.OK)
            }
            else{
                return ResponseEntity(PatchStudentDisciplinesResponse(response.second, null), HttpStatus.NOT_FOUND)
            }
        }
        else{
            return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }
    }

    @PatchMapping("academia/discipline/{disciplineCode}/remove")
    @Operation(
        summary = "Remove a discipline from all students.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun removeDisciplineFromAllStudents(@PathVariable disciplineCode: String,
                                        httpServletRequest: HttpServletRequest): ResponseEntity<DeleteDisciplineFromStudents>{
        val role = httpServletRequest.getAttribute("role")
        if (role == "admin"){
            val response = this.studentService.removeDisciplineForAllStudents(disciplineCode)
            if (response.message != "Discipline removed from all students!")
            {
                return ResponseEntity(response, HttpStatus.NOT_FOUND)
            }
            val self = linkTo(methodOn(AcademiaController::class.java).removeDisciplineFromAllStudents(disciplineCode, httpServletRequest))
            val parent = linkTo(methodOn(AcademiaController::class.java).getDisciplineById(disciplineCode, httpServletRequest))
            val links = Links(
                self = Link(self.toString(), "PATCH"),
                parent = Link(parent.toString())
            )
            return ResponseEntity(DeleteDisciplineFromStudents(response.message, links), HttpStatus.OK)
        }
        else
        {
            return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }
    }

    @DeleteMapping("/academia/discipline/{disciplineCode}/delete")
    @Operation(
        summary = "Delete a discipline.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteDisciplineByCode(@PathVariable disciplineCode: String,
                               httpServletRequest: HttpServletRequest): ResponseEntity<DisciplineDeleteResponse>{
        val role = httpServletRequest.getAttribute("role")
        if (role == "admin"){
            val response = this.disciplineService.deleteDisciplineByCode(disciplineCode)
            if (response.message != "Discipline deleted!"){
                if (response.message == "Students are enrolled for this discipline."){
                    return ResponseEntity(response, HttpStatus.CONFLICT)
                }
                if(response.message == "Discipline does not exist"){
                    return ResponseEntity(response, HttpStatus.NOT_FOUND)
                }
            }
            response._links = Links(
                self = null,
                parent = Link(linkTo(methodOn(AcademiaController::class.java).getDisciplines(Optional.empty(), Optional.empty(), null, null, httpServletRequest)).toUri().toString()),
                nextPage = null
            )
            return ResponseEntity(response, HttpStatus.NO_CONTENT)
        }
        else{
            return ResponseEntity(null, HttpStatus.FORBIDDEN)
        }
    }

    @DeleteMapping("/academia/professors/{id}/delete")
    @Operation(
        summary = "Delete a professor.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun deleteProfessorById(@PathVariable id: Int,
                            httpServletRequest: HttpServletRequest): ResponseEntity<DeleteProfessorResponse>{
        val role = httpServletRequest.getAttribute("role")
        if (role == "admin"){
            val professor = this.professorService.getProfessorById(id)
            if (professor!!.professor != null){
                val disciplines = this.disciplineService.getDisciplinesByIdTitular(professor.professor!!, Optional.empty(), Optional.empty())
                if (disciplines.count() != 0){
                    return ResponseEntity(DeleteProfessorResponse("This professor has disciplines!"), HttpStatus.CONFLICT)
                }
                val result = this.professorService.deleteProfessorById(id)
                result._links = Links(
                    self = null,
                    parent = Link(linkTo(methodOn(AcademiaController::class.java).getProfessors(null, null, null,  Optional.empty(), Optional.empty(), httpServletRequest)).toUri().toString()),
                    nextPage = null
                )
                return ResponseEntity(result, HttpStatus.NO_CONTENT)
            }
            else{
                return ResponseEntity(DeleteProfessorResponse("Professor not found!"), HttpStatus.NOT_FOUND)
            }
        }
        return ResponseEntity(null, HttpStatus.FORBIDDEN)
    }
}