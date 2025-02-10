package com.example.proiectPOS

import com.example.proiectPOS.config.MockAuthorizationFilterConfig
import com.example.proiectPOS.persistance.*
import com.example.proiectPOS.persistance.data.Discipline
import com.example.proiectPOS.persistance.data.Professor
import com.example.proiectPOS.persistance.data.Student
import com.example.proiectPOS.persistance.repositories.DisciplineRepositoryInterface
import com.example.proiectPOS.persistance.repositories.ProfessorsRepositoryInterface
import com.example.proiectPOS.persistance.repositories.StudentsRepositoryInterface
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@AutoConfigureMockMvc
@Import(MockAuthorizationFilterConfig::class)
@ActiveProfiles("test")
class AcademiaControllerIntegrationTests {
    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var professorsRepository: ProfessorsRepositoryInterface

    @Autowired
    private lateinit var disciplineRepository: DisciplineRepositoryInterface

    @Autowired
    private lateinit var studentRepository: StudentsRepositoryInterface


    private var idsProfessor = mutableListOf<Int>()


    companion object CONSTANTS{
        val listOfProfessorResponse = mutableListOf<Professor>(
            Professor(
                professorId = 0,
                lastName = "Popescu",
                firstName = "Ion",
                emailProfessor = "ion.popescu@academic.tuiasi.ro",
                degree = Degree.Main,
                associationType = AssociationType.Main,
                affiliation = ""
            ),
            Professor(
                professorId = 1,
                lastName = "Radu",
                firstName = "Daniel",
                emailProfessor = "daniel.radu@academic.tuiasi.ro",
                degree = Degree.Professor,
                associationType = AssociationType.Associate,
                affiliation = ""
            ),
            Professor(
                professorId = 2,
                lastName = "Strugariu",
                firstName = "Radu",
                emailProfessor = "radu.strugariu@academic.tuiasi.ro",
                degree = Degree.Professor,
                associationType = AssociationType.Associate,
                affiliation = ""
            )
        )

        val listOfDisciplines = mutableListOf<Discipline>(
            Discipline(
                disciplineCode = "SPD",
                professor = listOfProfessorResponse[0],
                disciplineName = "Statistica",
                yearDegree = 2,
                disciplineType = DisciplineType.Mandatory,
                disciplineCategory = DisciplineCategory.Speciality,
                examType = ExaminationType.Exam,
            ),
            Discipline(
                disciplineCode = "AM",
                professor = listOfProfessorResponse[1],
                disciplineName = "Analiza",
                yearDegree = 2,
                disciplineType = DisciplineType.Mandatory,
                disciplineCategory = DisciplineCategory.Adjacency,
                examType = ExaminationType.Exam,
            ),
            Discipline(
                disciplineCode = "APD",
                professor = listOfProfessorResponse[0],
                disciplineName = "Algoritmi",
                yearDegree = 2,
                disciplineType = DisciplineType.Mandatory,
                disciplineCategory = DisciplineCategory.Adjacency,
                examType = ExaminationType.Exam,
            ))

            val listOfStudents = mutableListOf<Student>(
                Student(
                    studentId = 0,
                    lastName = "Radu",
                    firstName = "Andrei",
                    email = "andrei.radu@student.tuiasi.ro",
                    studyCycle = CycleStudy.Bachelor,
                    groupName = "1409A",
                    yearDegree = 4,
                    disciplines = mutableListOf(
                        Discipline(
                                disciplineCode = "APD",
                                professor = listOfProfessorResponse[0],
                                disciplineName = "Algoritmi",
                                yearDegree = 2,
                                disciplineType = DisciplineType.Mandatory,
                                disciplineCategory = DisciplineCategory.Adjacency,
                                examType = ExaminationType.Exam,))
                )
            )


    }
    @BeforeEach
    fun setUp() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//            .build()


        // professor repository
        professorsRepository.deleteAll()
        var savedProf = professorsRepository.save(
            listOfProfessorResponse[0]
        )
        idsProfessor.add(savedProf.professorId)

        savedProf = professorsRepository.save(
            listOfProfessorResponse[1]
        )
        idsProfessor.add(savedProf.professorId)

        savedProf = professorsRepository.save(
            listOfProfessorResponse[2]
        )
        idsProfessor.add(savedProf.professorId)

        // disciplines repository
        disciplineRepository.deleteAll()
        disciplineRepository.save(listOfDisciplines[0])
        disciplineRepository.save(listOfDisciplines[1])
        disciplineRepository.save(listOfDisciplines[2])

        // students repository
        studentRepository.deleteAll()
        studentRepository.save(listOfStudents[0])

    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getProfessorById should return first professor`() {
        val id = idsProfessor[0]
        val result = mockMvc.get("/academia/professors/$id") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println("Response " + result.response.contentAsString)
        assert(result.response.status == 200)

    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getProfessorById should return  invalid id`() {
        val result = mockMvc.get("/academia/professors/-1") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println(result.response.status)
        println(result.response.contentAsString)
        assert(result.response.contentAsString.contains("The id has to be greater than 0!"))
        assert(result.response.status == 422)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getProfessorById should return professor not found`() {
        val result = mockMvc.get("/academia/professors/12342") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println(result.response.status)
        println(result.response.contentAsString)
        assert(result.response.status == 404)
    }


    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getDisciplineById should return Too many characters!`() {
        val result = mockMvc.get("/academia/disciplines/AMDRFG") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println(result.response.status)
        println(result.response.contentAsString)
        assert(result.response.contentAsString.contains("Too many characters!"))
        assert(result.response.status == 422)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getDisciplineById should return 404`() {
        val result = mockMvc.get("/academia/disciplines/AMD") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println(result.response.status)
        println(result.response.contentAsString)
        assert(result.response.status == 404)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getDisciplineById should return first discipline`() {
        val result = mockMvc.get("/academia/disciplines/SPD") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println(result.response.status)
        println(result.response.contentAsString)

        assert(result.response.contentAsString.contains("Discipline found"))
        assert(result.response.status == 200)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getProfessors should return Last name is mandatory!`() {
        val result = mockMvc.get("/academia/professors?firstName=abc") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println(result.response.status)
        println(result.response.contentAsString)

        assert(result.response.contentAsString.contains("Last name is mandatory!"))
        assert(result.response.status == 422)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getProfessors should return Do not pass empty data for last name!`() {
        val result = mockMvc.get("/academia/professors?lastName=") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println(result.response.status)
        println(result.response.contentAsString)

        assert(result.response.contentAsString.contains("Do not pass empty data!"))
        assert(result.response.status == 422)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getProfessors should return Do not pass empty data for first name!`() {
        val result = mockMvc.get("/academia/professors?lastName=abc&firstName=") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println(result.response.status)
        println(result.response.contentAsString)

        assert(result.response.contentAsString.contains("Do not pass empty data!"))
        assert(result.response.status == 422)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getProfessors should return Too many characters for last name!!`() {

        var invalidLastName= "a"
        repeat(31, {invalidLastName+="a"})
        val result = mockMvc.get("/academia/professors?lastName=$invalidLastName") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println(result.response.status)
        println(result.response.contentAsString)

        assert(result.response.contentAsString.contains("Too many characters for last name!"))
        assert(result.response.status == 422)
    }


    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getProfessors should return Too many characters for first name!!`() {

        var invalidFirstName= "a"
        repeat(31, {invalidFirstName+="a"})
        val result = mockMvc.get("/academia/professors?lastName=a&firstName=$invalidFirstName") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println(result.response.status)
        println(result.response.contentAsString)

        assert(result.response.contentAsString.contains("Too many characters for first name!"))
        assert(result.response.status == 422)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getProfessors should return Only alphabetic characters accepted for first name`() {

        var invalidFirstName= "a12"
        val result = mockMvc.get("/academia/professors?lastName=a&firstName=$invalidFirstName") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println(result.response.status)
        println(result.response.contentAsString)

        assert(result.response.contentAsString.contains("Only alphabetic characters accepted"))
        assert(result.response.status == 422)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getProfessors should return Only alphabetic characters accepted for last name`() {

        var invalidlLastName= "a12"
        val result = mockMvc.get("/academia/professors?lastName=$invalidlLastName") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println(result.response.status)
        println(result.response.contentAsString)

        assert(result.response.contentAsString.contains("Only alphabetic characters accepted"))
        assert(result.response.status == 422)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getProfessors should return first professor`() {
        val result = mockMvc.get("/academia/professors?lastName=Popescu") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println(result.response.status)
        println(result.response.contentAsString)

        assert(result.response.status == 200)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getProfessors should return Didn't find any professor for this specific query!`() {
        val result = mockMvc.get("/academia/professors?lastName=NoName") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andDo { println() }
            .andReturn()
        println(result.response.status)
        println(result.response.contentAsString)

        assert(result.response.contentAsString.contains("Didn't find any professor for this specific query!"))
        assert(result.response.status == 200)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getDisciplines should return second page`(){
        val result = mockMvc.get("/academia/disciplines?page=1&itemsPerPage=1") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andReturn()

        println(result.response.contentAsString)

        assert(result.response.contentAsString.contains("AM"))
        assert(!result.response.contentAsString.contains("SPD"))
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getDisciplines should return by discipline type`(){
        val result = mockMvc.get("/academia/disciplines?page=0&itemsPerPage=2&type=Mandatory") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andReturn()

        println(result.response.contentAsString)
        assert(result.response.contentAsString.contains("AM"))
        assert(result.response.contentAsString.contains("SPD"))
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getDisciplines should return by discipline category`(){
        val result = mockMvc.get("/academia/disciplines?page=0&itemsPerPage=2&type=Mandatory&category=Adjacency") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "admin@academic.tuiasi.ro")
        }
            .andReturn()

        println(result.response.contentAsString)
        assert(result.response.contentAsString.contains("AM"))
        assert(!result.response.contentAsString.contains("SPD"))
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getDisciplinesForProfessors should return forbidden for student`(){
        val result = mockMvc.get("/academia/professors/${idsProfessor[0]}/disciplines") {
            header("role", "student")
            header("sub", "67890")
            header("email", "student@academic.tuiasi.ro")
        }
            .andReturn()

        println(result.response.contentAsString)
        assert(result.response.status == 403)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getDisciplinesForProfessors should return professor not found`(){
        val result = mockMvc.get("/academia/professors/12/disciplines") {
            header("role", "professor")
            header("sub", "67890")
            header("email", "student@academic.tuiasi.ro")
        }
            .andReturn()

        println(result.response.contentAsString)
        assert(result.response.status == 404)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getDisciplinesForProfessors should return disciplines`(){
        val result = mockMvc.get("/academia/professors/${idsProfessor[0]}/disciplines") {
            header("role", "professor")
            header("sub", "67890")
            header("email", "student@academic.tuiasi.ro")
        }
            .andReturn()

        println(result.response.contentAsString)
        assert(result.response.status == 200)
        assert(result.response.contentAsString.contains("Discipline found"))
        assert(result.response.contentAsString.contains("http://localhost/academia/disciplines/SPD"))
        assert(result.response.contentAsString.contains("http://localhost/academia/disciplines"))
        assert(result.response.contentAsString.contains("http://localhost/academia/professors/1/disciplines"))
        assert(result.response.contentAsString.contains("http://localhost/academia/professors/1"))
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getDisciplinesForProfessors should return empty list`(){
        val result = mockMvc.get("/academia/professors/${idsProfessor[2]}/disciplines") {
            header("role", "professor")
            header("sub", "67890")
            header("email", "student@academic.tuiasi.ro")
        }
            .andReturn()

        println(result.response.contentAsString)
        assert(result.response.status == 200)
        assert(!result.response.contentAsString.contains("Discipline found"))
        assert(result.response.contentAsString.contains("http://localhost/academia/professors/${idsProfessor[2]}/disciplines"))
        assert(result.response.contentAsString.contains("http://localhost/academia/professors/${idsProfessor[2]}"))
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getDisciplinesForStudent should return APD`(){
        val result = mockMvc.get("/academia/students/1/disciplines") {
            header("role", "student")
            header("sub", "67890")
            header("email", "andrei.radu@student.tuiasi.ro")
        }
            .andReturn()

        println(result.response.contentAsString)
        assert(result.response.status == 200)
        assert(result.response.contentAsString.contains("http://localhost/academia/students/1/disciplines"))
        assert(result.response.contentAsString.contains("http://localhost/academia/students/1"))
        assert(result.response.contentAsString.contains("Found disciplines"))
        assert(result.response.contentAsString.contains("APD"))
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getDisciplinesForStudent should return Forbidden for another emails`(){
        val result = mockMvc.get("/academia/students/1/disciplines") {
            header("role", "student")
            header("sub", "67890")
            header("email", "not.me@student.tuiasi.ro")
        }
            .andReturn()

        println(result.response.contentAsString)
        assert(result.response.status == 403)
    }


    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getDisciplinesForStudent should return Student id should be greater than 0`(){
        val result = mockMvc.get("/academia/students/-1/disciplines") {
            header("role", "student")
            header("sub", "67890")
            header("email", "not.me@student.tuiasi.ro")
        }
            .andReturn()

        println(result.response.contentAsString)
        assert(result.response.status == 422)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `getDisciplinesForStudent should return Student not found`(){
        val result = mockMvc.get("/academia/students/3/disciplines") {
            header("role", "student")
            header("sub", "67890")
            header("email", "andrei.radu@student.tuiasi.ro")
        }
            .andReturn()

        println(result.response.contentAsString)
        assert(result.response.status == 404)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `insertStudent should return forbidden`(){
        val studentJson = """
        {
            "lastName": "Popovici",
            "firstName": "Alin",
            "email": "alin.popovici@student.tuiasi.ro",
            "studyCycle": "Bachelor",
            "groupName": "1409A",
            "yearDegree": 4
        }
    """.trimIndent()

        val result = mockMvc.post("/academia/students/insert") {
            header("role", "student")
            header("sub", "67890")
            header("email", "student@academic.tuiasi.ro")
            contentType = MediaType.APPLICATION_JSON
            content = studentJson
        }.andReturn()

        println("Response Status: ${result.response.status}")
        println("Response Body: ${result.response.contentAsString}")

        assert(result.response.status == 403)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `insertStudent should return invalid email`(){
        val studentJson = """
        {
            "lastName": "Popovici",
            "firstName": "Alin",
            "email": "alin.popovici@something.tuiasi.ro",
            "studyCycle": "Bachelor",
            "groupName": "1409A",
            "yearDegree": 4
        }
    """.trimIndent()

        val result = mockMvc.post("/academia/students/insert") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "student@academic.tuiasi.ro")
            contentType = MediaType.APPLICATION_JSON
            content = studentJson
        }.andReturn()

        println("Response Status: ${result.response.status}")
        println("Response Body: ${result.response.contentAsString}")

        assert(result.response.status == 422)
        assert(result.response.contentAsString.contains("Invalid email!"))
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `insertStudent should return Last name too long!`(){
        var longName = ""
        repeat(31, {longName+="i"})
        val studentJson = """
        {
            "lastName": "$longName",
            "firstName": "Alin",
            "email": "alin.popovici@student.tuiasi.ro",
            "studyCycle": "Bachelor",
            "groupName": "1409A",
            "yearDegree": 4
        }
    """.trimIndent()

        val result = mockMvc.post("/academia/students/insert") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "student@academic.tuiasi.ro")
            contentType = MediaType.APPLICATION_JSON
            content = studentJson
        }.andReturn()

        println("Response Status: ${result.response.status}")
        println("Response Body: ${result.response.contentAsString}")

        assert(result.response.status == 422)
        assert(result.response.contentAsString.contains("Last name too long!"))
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `insertStudent should return First name too long!`(){
        var longName = ""
        repeat(31, {longName+="i"})
        val studentJson = """
        {
            "lastName": "a",
            "firstName": "$longName",
            "email": "alin.popovici@student.tuiasi.ro",
            "studyCycle": "Bachelor",
            "groupName": "1409A",
            "yearDegree": 4
        }
    """.trimIndent()

        val result = mockMvc.post("/academia/students/insert") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "student@academic.tuiasi.ro")
            contentType = MediaType.APPLICATION_JSON
            content = studentJson
        }.andReturn()

        println("Response Status: ${result.response.status}")
        println("Response Body: ${result.response.contentAsString}")

        assert(result.response.status == 422)
        assert(result.response.contentAsString.contains("First name too long!"))
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `insertStudent should return Invalid first name for student!`(){
        var longName = ""
        repeat(31, {longName+="i"})
        val studentJson = """
        {
            "lastName": "a",
            "firstName": "12%",
            "email": "alin.popovici@student.tuiasi.ro",
            "studyCycle": "Bachelor",
            "groupName": "1409A",
            "yearDegree": 4
        }
    """.trimIndent()

        val result = mockMvc.post("/academia/students/insert") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "student@academic.tuiasi.ro")
            contentType = MediaType.APPLICATION_JSON
            content = studentJson
        }.andReturn()

        println("Response Status: ${result.response.status}")
        println("Response Body: ${result.response.contentAsString}")

        assert(result.response.status == 422)
        assert(result.response.contentAsString.contains("Invalid first name for student!"))
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `insertStudent should return Invalid last name for student!`(){
        var longName = ""
        repeat(31, {longName+="i"})
        val studentJson = """
        {
            "lastName": "12$",
            "firstName": "a",
            "email": "alin.popovici@student.tuiasi.ro",
            "studyCycle": "Bachelor",
            "groupName": "1409A",
            "yearDegree": 4
        }
    """.trimIndent()

        val result = mockMvc.post("/academia/students/insert") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "student@academic.tuiasi.ro")
            contentType = MediaType.APPLICATION_JSON
            content = studentJson
        }.andReturn()

        println("Response Status: ${result.response.status}")
        println("Response Body: ${result.response.contentAsString}")

        assert(result.response.status == 422)
        assert(result.response.contentAsString.contains("Invalid last name for student!"))
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `insertStudent should return Invalid group!`(){
        var longName = ""
        repeat(31, {longName+="i"})
        val studentJson = """
        {
            "lastName": "b",
            "firstName": "a",
            "email": "alin.popovici@student.tuiasi.ro",
            "studyCycle": "Bachelor",
            "groupName": "1111409A",
            "yearDegree": 4
        }
    """.trimIndent()

        val result = mockMvc.post("/academia/students/insert") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "student@academic.tuiasi.ro")
            contentType = MediaType.APPLICATION_JSON
            content = studentJson
        }.andReturn()

        println("Response Status: ${result.response.status}")
        println("Response Body: ${result.response.contentAsString}")

        assert(result.response.status == 422)
        assert(result.response.contentAsString.contains("Invalid group!"))
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `insertStudent should return Year degree should be greater than 0!`(){
        var longName = ""
        repeat(31, {longName+="i"})
        val studentJson = """
        {
            "lastName": "b",
            "firstName": "a",
            "email": "alin.popovici@student.tuiasi.ro",
            "studyCycle": "Bachelor",
            "groupName": "1409A",
            "yearDegree": -1
        }
    """.trimIndent()

        val result = mockMvc.post("/academia/students/insert") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "student@academic.tuiasi.ro")
            contentType = MediaType.APPLICATION_JSON
            content = studentJson
        }.andReturn()

        println("Response Status: ${result.response.status}")
        println("Response Body: ${result.response.contentAsString}")

        assert(result.response.status == 422)
        assert(result.response.contentAsString.contains("Year degree should be greater than 0!"))
    }



    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `insertStudent should return Student inserted with succes!`(){
        var longName = ""
        repeat(31, {longName+="i"})
        val studentJson = """
        {
            "lastName": "b",
            "firstName": "a",
            "email": "alin.popovici@student.tuiasi.ro",
            "studyCycle": "Bachelor",
            "groupName": "1409A",
            "yearDegree": 4
        }
    """.trimIndent()

        val result = mockMvc.post("/academia/students/insert") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "student@academic.tuiasi.ro")
            contentType = MediaType.APPLICATION_JSON
            content = studentJson
        }.andReturn()

        println("Response Status: ${result.response.status}")
        println("Response Body: ${result.response.contentAsString}")

        assert(result.response.status == 201)
        assert(result.response.contentAsString.contains("Student inserted with succes!"))
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `insertProfessor should return forbidden`(){
        var longName = ""
        repeat(31, {longName+="i"})
        val profJson = """
        {
        "lastName": Mirea,
        "firstName": "Letitia",
        "emailProfessor": "letitia.mirea@academic.tuiasi.ro",
        "degree": "Professor",
        "associationType":"Main",
        "affiliation":"af1"
        }
    """.trimIndent()

        val result = mockMvc.post("/academia/professors/insert") {
            header("role", "professor")
            header("sub", "67890")
            header("email", "professor@academic.tuiasi.ro")
            contentType = MediaType.APPLICATION_JSON
            content = profJson
        }.andReturn()

        println("Response Status: ${result.response.status}")
        println("Response Body: ${result.response.contentAsString}")

       // assert(result.response.status == 403)
    }

    @Test
    @AutoConfigureMockMvc(addFilters = true)
    fun `insertProfessor should return Too many characters!`(){
        var longName = ""
        repeat(31, {longName+="i"})
        val profJson = """
        {
        "lastName": $longName,
        "firstName": "Letitia",
        "emailProfessor": "letitia.mirea@academic.tuiasi.ro",
        "degree": "Professor",
        "associationType":"Main",
        "affiliation":"af1"
        }
    """.trimIndent()

        val result = mockMvc.post("/academia/professors/insert") {
            header("role", "admin")
            header("sub", "67890")
            header("email", "student@academic.tuiasi.ro")
            contentType = MediaType.APPLICATION_JSON
            content = profJson
        }.andReturn()

        println("Response Status: ${result.response.status}")
        println("Response Body: ${result.response.contentAsString}")
        assert(result.response.contentAsString.contains("Too many characters!"))
        assert(result.response.status == 422)
    }
}