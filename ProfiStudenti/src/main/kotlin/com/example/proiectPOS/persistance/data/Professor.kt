package com.example.proiectPOS.persistance.data

import com.example.proiectPOS.persistance.AssociationType
import com.example.proiectPOS.persistance.Degree
import jakarta.persistence.*


@Table(name = "professors",
    uniqueConstraints = [UniqueConstraint(columnNames = ["email"])])
@Entity
class Professor(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_professor", nullable = false)
    var professorId: Int,

    @Column(name = "last_name", nullable = false)
    var lastName: String,

    @Column(name = "first_name", nullable = false)
    var firstName: String,

    @Column(name = "email", nullable = false, unique = true)
    var emailProfessor: String,

    @Column(name = "degree", nullable = true)
    var degree: Degree? = null,

    @Column(name = "association_type", nullable = false)
    var associationType: AssociationType,

    @Column(name = "affiliation", nullable = true)
    var affiliation: String? = null
)
