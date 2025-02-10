from http import HTTPStatus

from fastapi import HTTPException

from interfaces.DisciplinesServiceInterface import DisciplinesServiceInterface
from persistance.data.Item import DisciplineItem, InsertPonderiRequest, ProbaPondere
from persistance.database import getCollection, connectDatabase


class DisciplinesService(DisciplinesServiceInterface):
    def __init__(self):
        db = connectDatabase()
        self._collection = getCollection(db, "materials")


    def getDisciplineByCode(self, disciplineCode: str):
        discipline = self._collection.find_one({"code": disciplineCode})
        if discipline is None:
            print("Discipline not found")
        else:
            print("Discipline", discipline["code"])
        return discipline

    def getAllDisciplines(self):
        disciplines =  self._collection.find({})
        disciplineNames = []
        for discipline in disciplines:
            if "name" in discipline and discipline["name"] is not None:
                disciplineNames.append(discipline["name"])
            else:
                print(discipline["code"])
                disciplineNames.append(discipline["code"])

        return disciplineNames

    def insertDisciplineByCode(self, disciplineCode: str, email_titular: str):
        if self.getDisciplineByCode(disciplineCode) is not None:
            raise HTTPException(
                status_code=HTTPStatus.CONFLICT,
                detail="Discipline already exists",
            )
        if len(disciplineCode) >= 5:
            raise HTTPException(
                status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                detail="Discipline code must be less than 5",
            )

        discipline = DisciplineItem(code = disciplineCode, titular = email_titular, name=disciplineCode)
        discipline = discipline.model_dump()
        self._collection.insert_one(discipline)
        return "Discipline inserted successfully"

    def insertPonderi(self, disciplineCode: str, ponderiRequest: InsertPonderiRequest):
        discipline_found = self.getDisciplineByCode(disciplineCode)
        if discipline_found is None:
            print("Discipline not found")
            return "Discipline not found!"

        if len(ponderiRequest.ponderi) != len(ponderiRequest.probe):
            print("Different dimensions")
            raise HTTPException(
                status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                detail="Length of ponderi has to be equal to number of probes",
            )
        suma = 0
        for pondere in ponderiRequest.ponderi:
            try:
                pondere_int = int(pondere)
            except ValueError:
                raise HTTPException(
                    status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                    detail="Pondere value must be an integer",
                )
            suma += pondere_int

        if suma != 100:
            print("Sum is not 100")
            raise HTTPException(
                status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                detail="Sum of ponderi must be 100",
            )

        probe_ponderi = []
        for i, proba in enumerate(ponderiRequest.probe):
            probe_ponderi.append(ProbaPondere(proba=proba, pondere=ponderiRequest.ponderi[i]).model_dump())

        print("Updating")
        discipline_found["probe_ponderi"] = probe_ponderi
        self._collection.update_one(
            {"code": discipline_found["code"]},
            {"$set": discipline_found},
        )

        return "Ponderi inserted successfully"

    def getPonderi(self, disciplineCode: str):
        discipline_found = self.getDisciplineByCode(disciplineCode)
        if discipline_found is None:
            raise HTTPException(
                status_code=HTTPStatus.NOT_FOUND,
                detail="Discipline not found",
            )
        ponderi = discipline_found["probe_ponderi"]
        return ponderi

    def deleteDisciplineByCode(self, disciplineCode: str):
        discipline_found = self.getDisciplineByCode(disciplineCode)
        if discipline_found is None:
            raise HTTPException(
                status_code=HTTPStatus.NOT_FOUND,
                detail="Discipline not found",
            )

        self._collection.delete_one({"code": disciplineCode})
        return f"Discipline with code '{disciplineCode}' deleted successfully."

    def _serialize(self, material):
        if "_id" in material:
            material["_id"] = str(material["_id"])
        return material