from http import HTTPStatus

from fastapi import UploadFile, File, HTTPException

from interfaces.MaterialsServiceInterface import MaterialsServiceInterface
from persistance.data.Item import DisciplineItem, Material
from persistance.database import getCollection, connectDatabase


class MaterialsService(MaterialsServiceInterface):
    def __init__(self):
        db = connectDatabase()
        self._collection = getCollection(db, "materials")

    def getLabMaterialByName(self, name: str, disciplineFound: DisciplineItem):
        return self.get_material(name, disciplineFound, "lab_materials")


    def getCourseMaterialByName(self, name: str, disciplineFound: DisciplineItem):
        return self.get_material(name, disciplineFound, "course_materials")


    def insertLabMaterial(self, files: list[UploadFile], disciplineFound: DisciplineItem):
        return self.insert_material( disciplineFound, files, "lab_materials")

    def insertCourseMaterial(self, files: list[UploadFile], disciplineFound: DisciplineItem):
        return self.insert_material( disciplineFound, files, "course_materials")


    def removeLabMaterials(self, lab_materials: list[str], disciplineFound: DisciplineItem):
        return self.remove_material(lab_materials, disciplineFound, "lab_materials")

    def removeCourseMaterials(self, course_materials: list[str], disciplineFound: DisciplineItem):
        return self.remove_material(course_materials, disciplineFound, "course_materials")

    def getLabMaterialsName(self, disciplineFound: DisciplineItem):
        return self.get_materials_names(disciplineFound, "lab_materials")

    def get_materials_names(self, disciplineFound: DisciplineItem, type: str):
        print("Inside get_materials_names")
        if disciplineFound is None:
            raise HTTPException(
                status_code=HTTPStatus.NOT_FOUND,
                detail="Discipline not found")
        materials = []
        if type not in ["lab_materials", "course_materials"]:
            raise HTTPException(
                status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                detail=f"Invalid type {type}",
            )

        if type == "lab_materials":
            materials = disciplineFound["lab_materials"]
        elif type == "course_materials":
            materials = disciplineFound["course_materials"]

        if materials is None:
            print("Inside get_materials_names, materials is none")
            return []

        if len(materials) == 0:
            return []

        name_materials = []
        for material in materials:
            name_materials.append(material["name"])

        return name_materials


    def get_material(self, name: str, disciplineFound: DisciplineItem, type: str):
        if disciplineFound is None:
            raise HTTPException(
                status_code=HTTPStatus.NOT_FOUND,
                detail="Discipline not found",
            )
        materials = []
        if type not in ["lab_materials", "course_materials"]:
            raise HTTPException(
                status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                detail=f"Invalid type {type}",
            )

        if type == "lab_materials":
            materials = disciplineFound["lab_materials"]
        elif type == "course_materials":
            materials = disciplineFound["course_materials"]

        if len(materials) == 0:
            raise HTTPException(
                status_code=HTTPStatus.NOT_FOUND,
                detail=f"There are not materials for this discipline",
            )
        found_material = None
        for material in materials:
            if material["name"] == name:
                found_material = material
                break
        if found_material is None:
            raise HTTPException(
                status_code=HTTPStatus.NOT_FOUND,
                detail=f"Material {name} not found",
            )
        return "Material found", found_material

    def insert_material(self, disciplineFound: DisciplineItem, files:list[UploadFile], type: str):
        print("Inside materials service. Trying to insert materials.")
        existing_materials = []
        if type not in ["lab_materials", "course_materials"]:
            raise HTTPException(
                status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                detail=f"Invalid type {type}",
            )
        if type == "lab_materials":
            existing_materials = disciplineFound["lab_materials"]
        elif type == "course_materials":
            existing_materials = disciplineFound["course_materials"]

        existing_names = []
        if existing_materials is not None:
            print("Existing materials is not none")
            existing_names = [material["name"] for material in existing_materials]
        else:
            existing_materials = []

        print("Files iteration")
        materials_to_add = []
        for file in files:
            if file is None:
                raise HTTPException(
                    status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                    detail=f"File {file} not found",
                )
            name = file.filename
            if name is None:
                raise HTTPException(
                    status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                    detail=f"File has no name",
                )

            if existing_names is not None:
                if name in existing_names:
                    raise HTTPException(
                        status_code=HTTPStatus.CONFLICT,
                        detail=f"Material with name: {name} already exists",
                    )

            print("Reading file")
            content = file.file.read()
            content_type = file.content_type
            material = Material(name=name, content=content, content_type=content_type)
            materials_to_add.append(material)
            print("Material appended")

        existing_materials.extend([material.model_dump() for material in materials_to_add])
        if type == "lab_materials":
            disciplineFound["lab_materials"] = existing_materials
        elif type == "course_materials":
            disciplineFound["course_materials"] = existing_materials

        self._collection.update_one(
            {"code": disciplineFound["code"]},
            {"$set": disciplineFound
            },
        )

        file_names_added = []
        for material in materials_to_add:
            file_names_added.append(material.name)
        return "ok", file_names_added

    def remove_material(self, materials: list[str], disciplineFound: DisciplineItem, type: str):
        if type not in ["lab_materials", "course_materials"]:
            raise HTTPException(
                status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                detail=f"Invalid type {type}",
            )

        nonexistent_materials = ""
        existing_materials = []
        if type == "lab_materials":
            existing_materials = disciplineFound["lab_materials"]
        elif type == "course_materials":
            existing_materials = disciplineFound["course_materials"]

        if len(existing_materials) == 0:
            raise HTTPException(
                status_code=HTTPStatus.NOT_FOUND,
                detail=f"There are not materials for this discipline",
            )

        if not materials:
            raise HTTPException(
                status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                detail=f"There are not materials specified for this removal",
            )

        all_material_names = []
        for material in existing_materials:
            all_material_names.append(material["name"])

        for material_name in materials:
            if material_name not in all_material_names:
                nonexistent_materials += material_name
                continue
            for existing_material in existing_materials:
                if existing_material["name"] == material_name:
                    existing_materials.remove(existing_material)
                    break

        if type == "lab_materials":
            disciplineFound["lab_materials"] = existing_materials
        else:
            disciplineFound["course_materials"] = existing_materials

        self._collection.update_one(
            {"code": disciplineFound["code"]},
            {"$set": disciplineFound},
        )

        if len(nonexistent_materials) > 0:
            return "Not all materials were removed! {}".format(nonexistent_materials)

        return "All materials were removed!"

