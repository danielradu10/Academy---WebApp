from abc import ABC, abstractmethod

from fastapi import UploadFile, File

from persistance.data.Item import Material, DisciplineItem


class MaterialsServiceInterface(ABC):
    @abstractmethod
    def getLabMaterialByName(self, name: str, disciplineFound: DisciplineItem):
        pass

    @abstractmethod
    def getCourseMaterialByName(self, name: str, disciplineFound: DisciplineItem):
        pass

    @abstractmethod
    def removeLabMaterials(self, lab_materials: list[str], disciplineFound: DisciplineItem):
        pass

    @abstractmethod
    def removeCourseMaterials(self, lab_materials: list[str], disciplineFound: DisciplineItem):
        pass

    @abstractmethod
    def insertLabMaterial(self, files: list[UploadFile], disciplineFound: DisciplineItem):
        pass

    @abstractmethod
    def insertCourseMaterial(self, files: list[UploadFile], disciplineFound: DisciplineItem):
        pass

    @abstractmethod
    def getLabMaterialsName(self, disciplineFound: DisciplineItem):
        pass


