package com.example.proiectPOS.persistance.data

import com.example.proiectPOS.persistance.CycleStudy
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*


@Table(name = "students",
    uniqueConstraints = [UniqueConstraint(columnNames = ["email"])]
)
@Entity
class Student(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_student", nullable = false)
    var studentId: Int,

    @Column(name = "last_name", nullable = false)
    var lastName: String,

    @Column(name = "first_name", nullable = false)
    var firstName: String,

    @Column(name = "email", nullable = false, unique = true)
    var email: String,

    @Column(name = "study_cicle", nullable = false)
    var studyCycle: CycleStudy,

    @Column(name = "group_name", nullable = false)
    var groupName: String,

    @Column(name = "year_degree", nullable = false)
    var yearDegree: Int,

    @ManyToMany
    @JoinTable(
        name = "join_ds",
        joinColumns = [JoinColumn(name = "student_id", referencedColumnName = "id_student")],
        inverseJoinColumns = [JoinColumn(name = "discipline_id", referencedColumnName = "code_discipline")]
    )
    @JsonIgnore
    var disciplines: MutableList<Discipline> = mutableListOf()
){
    @Override
    override fun toString(): String {
        return "Student(" +
                "studentId=$studentId, " +
                "lastName='$lastName', " +
                "firstName='$firstName', " +
                "email='$email', " +
                "studyCycle=$studyCycle, " +
                "groupName='$groupName', " +
                "yearDegree=$yearDegree" +
                ")"
    }
}
