from abc import ABC, abstractmethod

from persistance.data.Item import InsertPonderiRequest


class DisciplinesServiceInterface(ABC):
    @abstractmethod
    def getAllDisciplines(self):
        pass

    @abstractmethod
    def getDisciplineByCode(self, disciplineCode: str):
        pass

    @abstractmethod
    def insertDisciplineByCode(self, disciplineCode: str, email_titular: str):
        pass

    @abstractmethod
    def insertPonderi(self, disciplineCode: str, ponderiRequest: InsertPonderiRequest):
        pass

    @abstractmethod
    def getPonderi(self, disciplineCode: str):
        pass