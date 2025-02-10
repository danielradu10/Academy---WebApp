package com.example.proiectPOS.persistance.data

import com.example.proiectPOS.persistance.DisciplineCategory
import com.example.proiectPOS.persistance.DisciplineType
import com.example.proiectPOS.persistance.ExaminationType
import jakarta.persistence.*

@Table(name = "disciplines")
@Entity
class Discipline(
    @Id
    @Column(name = "code_discipline", nullable = false)
    var disciplineCode: String,

    @ManyToOne
    @JoinColumn(name = "id_titular", referencedColumnName = "id_professor", nullable = false)
    var professor: Professor,


    @Column(name = "discipline_name", nullable = false)
    var disciplineName: String,

    @Column(name = "degree_year", nullable = false)
    var yearDegree: Int,

    @Column(name = "discipline_type", nullable = false)
    var disciplineType: DisciplineType,

    @Column(name = "category_discipline", nullable = false)
    var disciplineCategory: DisciplineCategory,

    @Column(name = "type_exam", nullable = false)
    var examType: ExaminationType,

    @ManyToMany(mappedBy = "disciplines")
    var students: MutableList<Student> = mutableListOf()
){
    @Override
    override fun toString(): String {
        var returnedString = "Discipline(" +
                "disciplineCode='$disciplineCode', " +
                "disciplineName='$disciplineName', " +
                "yearDegree=$yearDegree, " +
                "disciplineType=$disciplineType, " +
                "disciplineCategory=$disciplineCategory, " +
                "examType=$examType, " +
                "professor=${professor.firstName} ${professor.lastName}" +
                ")"

        for (student in students){
            returnedString += student.toString()
        }
        return returnedString
    }
}



