from http import HTTPStatus

from fastapi import APIRouter, Depends, File, UploadFile, HTTPException, Request
from fastapi.responses import StreamingResponse
from io import BytesIO


from persistance.data.Item import DisciplineItem, RemoveMaterialsRequest, InsertPonderiRequest
from services.DisciplinesService import DisciplinesService
from services.MaterialsService import MaterialsService

router = APIRouter()


materialsService = MaterialsService()
disciplinesService = DisciplinesService()

def inject_materials_service():
    return materialsService

def inject_disciplines_service():
    return disciplinesService

@router.get("/materials/disciplines")
def get_disciplines(request: Request, disciplines_service: DisciplinesService = Depends(inject_disciplines_service)):
    user = request.state.user
    if user["role"] != "admin":
        raise HTTPException(
            status_code=HTTPStatus.FORBIDDEN,
            detail="You do not have permission to perform this operation.",
        )
    disciplines = disciplines_service.getAllDisciplines()
    return {
        "data": [
            {
                "name": discipline,
                "_links": {
                    "self": f"/materials/disciplines/{discipline}",
                    "ponderi": f"/materials/disciplines/{discipline}/ponderi",
                    "labs": f"/materials/disciplines/{discipline}/labs",
                    "courses": f"/materials/disciplines/{discipline}/courses",
                    "insertLabMaterial": {
                        "href":f"/materials/disciplines/{discipline}/labs/insert",
                        "method": "PATCH",
                    },
                    "insertCourseMaterial": {
                        "href": f"/materials/disciplines/{discipline}/courses/insert",
                        "method": "PATCH",
                    },
                    "deleteDiscipline": {
                        "href": f"/materials/disciplines/{discipline}",
                        "method": "DELETE",
                    }
                },
            }
            for discipline in disciplines
        ],
        "_links": {"self": "/materials/disciplines"},
    }


@router.get("/materials/disciplines/{disciplineCode}")
def get_discipline(disciplineCode: str, disciplines_service: DisciplinesService = Depends(inject_disciplines_service),
                   ):
    discipline = disciplines_service.getDisciplineByCode(disciplineCode)
    if discipline is None:
        raise HTTPException(
            status_code=HTTPStatus.NOT_FOUND,
            detail="Discipline does not exist.",
        )
    print("Inside controller", discipline['code'])

    name = discipline['name'] if 'name' in discipline else discipline['code']
    return {
        "data": [
            {
                "name": name,
                "links": {
                    "self": f"/materials/disciplines/{discipline['code']}",
                    "ponderi": f"/materials/disciplines/{discipline['code']}/ponderi",
                    "labs": f"/materials/disciplines/{discipline['code']}/labs",
                    "courses": f"/materials/disciplines/{discipline['code']}/courses",
                    "insertLabMaterial": {
                        "href": f"/materials/disciplines/{discipline['code']}/labs/insert",
                        "method": "PATCH",
                    },
                    "insertCourseMaterial": {
                        "href": f"/materials/disciplines/{discipline['code']}/courses/insert",
                        "method": "PATCH",
                    },
                    "deleteDiscipline": {
                        "href": f"/materials/disciplines/{discipline}",
                        "method": "DELETE",
                    }
                },
            }
        ],
        "_links": {
            "self": f"/materials/disciplines/{discipline['code']}",
            "parent": "/materials/disciplines"
        }
    }


@router.get("/materials/disciplines/{disciplineCode}/labs")
def get_lab_materials(
        disciplineCode: str,
        page: int = 1,
        limit: int = 3,
        disciplines_service: DisciplinesService = Depends(inject_disciplines_service),
        material_service: MaterialsService = Depends(inject_materials_service),
):
    found_discipline = disciplines_service.getDisciplineByCode(disciplineCode)
    if found_discipline is None:
        raise HTTPException(
            status_code=HTTPStatus.NOT_FOUND,
            detail="Discipline not found",
        )

    materials = material_service.get_materials_names(found_discipline, "lab_materials")
    total_materials = len(materials)

    start_index = (page - 1) * limit
    end_index = start_index + limit
    paginated_materials = materials[start_index:end_index]

    next_page = page + 1 if end_index < total_materials else None
    previous_page = page - 1 if start_index > 0 else None

    return {
        "materials": [
            {
                "name": material,
                "_links": {
                    "self": f"/materials/disciplines/{disciplineCode}/labs/{material}",
                },
            }
            for material in paginated_materials
        ],
        "_links": {
            "self": f"/materials/disciplines/{disciplineCode}/labs?page={page}&limit={limit}",
            "nextPage": f"/materials/disciplines/{disciplineCode}/labs?page={next_page}&limit={limit}" if next_page else None,
            "previousPage": f"/materials/disciplines/{disciplineCode}/labs?page={previous_page}&limit={limit}" if previous_page else None,
        },
        "pagination": {
            "total_items": total_materials,
            "total_pages": (total_materials + limit - 1) // limit,
            "current_page": page,
            "items_per_page": limit,
        },
    }


@router.get("/materials/disciplines/{disciplineCode}/courses")
def get_course_materials(
        disciplineCode: str,
        page: int = 1,
        limit: int = 3,
        disciplines_service: DisciplinesService = Depends(inject_disciplines_service),
        material_service: MaterialsService = Depends(inject_materials_service),
):
    found_discipline = disciplines_service.getDisciplineByCode(disciplineCode)
    if found_discipline is None:
        raise HTTPException(
            status_code=HTTPStatus.NOT_FOUND,
            detail="Discipline not found",
        )

    materials = material_service.get_materials_names(found_discipline, "course_materials")
    total_materials = len(materials)

    start_index = (page - 1) * limit
    end_index = start_index + limit
    paginated_materials = materials[start_index:end_index]

    next_page = page + 1 if end_index < total_materials else None
    previous_page = page - 1 if start_index > 0 else None

    return {
        "materials": [
            {
                "name": material,
                "_links": {
                    "self": f"/materials/disciplines/{disciplineCode}/course/{material}",
                },
            }
            for material in paginated_materials
        ],
        "_links": {
            "self": f"/materials/disciplines/{disciplineCode}/courses?page={page}&limit={limit}",
            "nextPage": f"/materials/disciplines/{disciplineCode}/courses?page={next_page}&limit={limit}" if next_page else None,
            "previousPage": f"/materials/disciplines/{disciplineCode}/courses?page={previous_page}&limit={limit}" if previous_page else None,
        },
        "pagination": {
            "total_items": total_materials,
            "total_pages": (total_materials + limit - 1) // limit,
            "current_page": page,
            "items_per_page": limit,
        },
    }



@router.get("/materials/disciplines/{disciplineCode}/labs/{materialName}")
def get_lab_material(disciplineCode: str, materialName: str, disciplines_service: DisciplinesService = Depends(inject_disciplines_service),
                     material_service: MaterialsService = Depends(inject_materials_service),
                     ):
    print("Specific material called")
    found_discipline = disciplines_service.getDisciplineByCode(disciplineCode)
    message, materialFound = material_service.getLabMaterialByName(materialName, found_discipline)
    if materialFound is None:
        raise HTTPException(
            status_code=HTTPStatus.NOT_FOUND,
            detail="Material not found",
        )
    return StreamingResponse(
        BytesIO(materialFound["content"]),
        media_type=materialFound["content_type"],
        headers={"Content-Disposition": f"attachment; filename={materialFound['name']}"},
    )

@router.get("/materials/disciplines/{disciplineCode}/ponderi")
def get_ponderi(disciplineCode: str, disciplines_service: DisciplinesService = Depends(inject_disciplines_service),
                ):
    ponderi = disciplines_service.getPonderi(disciplineCode)
    return {
        "data": ponderi,
        "_links": {
            "self": f"/materials/disciplines/{disciplineCode}/ponderi",
            "discipline": f"/materials/disciplines/{disciplineCode}",
            "insertLabMaterial": {
                "href": f"/materials/disciplines/{disciplineCode}/labs/insert",
                "method": "PATCH",
            },
            "insertCourseMaterial": {
                "href": f"/materials/disciplines/{disciplineCode}/courses/insert",
                "method": "PATCH",
            },
            "deleteDiscipline": {
                "href": f"/materials/disciplines/{disciplineCode}",
                "method": "DELETE",
            }
        },
    }


@router.get("/materials/disciplines/{disciplineCode}/courses/{materialName}")
def get_course_material(disciplineCode: str, materialName: str, disciplines_service: DisciplinesService = Depends(inject_disciplines_service),
                         material_service: MaterialsService = Depends(inject_materials_service),
                         ):
    found_discipline = disciplines_service.getDisciplineByCode(disciplineCode)
    message, materialFound = material_service.getCourseMaterialByName(materialName, found_discipline)
    if materialFound is None:
        return {"message": message}
    return StreamingResponse(
        BytesIO(materialFound["content"]),
        media_type=materialFound["content_type"],
        headers={"Content-Disposition": f"attachment; filename={materialFound['name']}"},
    )


@router.post("/materials/disciplines/insert")
def insert_discipline(request: Request, discipline: DisciplineItem, disciplines_service: DisciplinesService = Depends(inject_disciplines_service),
                     ):
    user = request.state.user
    if user["role"] == "student" or user["role"] == "professor":
        raise HTTPException(
            status_code=HTTPStatus.FORBIDDEN,
            detail="You are not allowed to insert discipline",
        )
    message = disciplines_service.insertDisciplineByCode(discipline.code, discipline.titular)
    return {
        "message": message,
        "links": {
            "self": f"/materials/disciplines/{discipline.code}",
            "ponderi": f"/materials/disciplines/{discipline.code}/ponderi",
            "labs": f"/materials/disciplines/{discipline.code}/labs",
            "courses": f"/materials/disciplines/{discipline.code}/courses",
            "insertLabMaterial": {
                "href": f"/materials/disciplines/{discipline.code}/labs/insert",
                "method": "PATCH",
            },
            "insertCourseMaterial": {
                "href": f"/materials/disciplines/{discipline.code}/courses/insert",
                "method": "PATCH",
            }
        },
    }


@router.post("/materials/disciplines/{disciplineCode}/ponderi/insert")
def insert_ponderi(request: Request, disciplineCode: str, ponderi: InsertPonderiRequest, disciplines_service: DisciplinesService = Depends(inject_disciplines_service),
                   ):
    user = request.state.user
    if user["role"] == "student":
        raise HTTPException(
            status_code=HTTPStatus.FORBIDDEN,
            detail="You are not allowed to insert ponderi",
        )

    if user["role"] == "professor":
        emailCaller = user["email"]
        discipline = disciplines_service.getDisciplineByCode(disciplineCode)
        if discipline is None:
            print("Discipline not found")
            raise HTTPException(
                status_code=HTTPStatus.NOT_FOUND,
                detail="Discipline not found",
            )
        if discipline["titular"] != emailCaller:
            raise HTTPException(
                status_code=HTTPStatus.FORBIDDEN,
                detail="You are not allowed to insert ponderi to this discipline",
            )

    print("Inserting ponderi")
    message = disciplines_service.insertPonderi(disciplineCode, ponderi)
    return {
        "message": message,
        "links": {
            "self": f"/materials/disciplines/{disciplineCode}/ponderi",
            "parent": f"/materials/disciplines/{disciplineCode}",
            "labs": f"/materials/disciplines/{disciplineCode}/labs",
            "courses": f"/materials/disciplines/{disciplineCode}/courses",
            "insertLabMaterial": {
                "href": f"/materials/disciplines/{disciplineCode}/labs/insert",
                "method": "PATCH",
            },
            "insertCourseMaterial": {
                "href": f"/materials/disciplines/{disciplineCode}/courses/insert",
                "method": "PATCH",
            },
            "deleteDiscipline": {
                "href": f"/materials/disciplines/{disciplineCode}",
                "method": "DELETE",
            }
        }
    }

@router.patch("/materials/disciplines/{disciplineCode}/labs/insert")
def insert_new_lab_materials(
    request: Request,
    disciplineCode: str,
    files: list[UploadFile] = File(...),
    material_service: MaterialsService = Depends(inject_materials_service),
    disciplines_service: DisciplinesService = Depends(inject_disciplines_service),
):

    user = request.state.user
    if user["role"] == "student":
        raise HTTPException(
            status_code=HTTPStatus.FORBIDDEN,
            detail="You are not allowed to insert materials",
        )

    found_discipline = disciplines_service.getDisciplineByCode(disciplineCode)
    if found_discipline is not None:
        if user["role"] == "professor":
            emailCaller = user["email"]
            if found_discipline["titular"] != emailCaller:
                raise HTTPException(
                    status_code=HTTPStatus.FORBIDDEN,
                    detail="You are not allowed to insert materials to this discipline",
                )

        message, file_names = material_service.insertLabMaterial(files, found_discipline)
        return {
            "message": message,
            "file_names": file_names,
            "links": {
                "self": f"/materials/disciplines/{disciplineCode}/labs",
                "parent": f"/materials/disciplines/{disciplineCode}",
                "materials": [
                    {"name": file_name, "link": f"/materials/disciplines/{disciplineCode}/labs/{file_name}"}
                    for file_name in file_names
                ],
            },
        }

    raise HTTPException(
        status_code=HTTPStatus.NOT_FOUND,
        detail="Discipline not found",
    )


@router.patch("/materials/disciplines/{disciplineCode}/labs/remove")
def remove_lab_materials(request: Request, disciplineCode: str, removeRequest: RemoveMaterialsRequest, material_service: MaterialsService = Depends(inject_materials_service),
                         disciplines_service: DisciplinesService = Depends(inject_disciplines_service),
                         ):
    user = request.state.user
    if user["role"] == "student":
        raise HTTPException(
            status_code=HTTPStatus.FORBIDDEN,
            detail="You are not allowed to remove materials",
        )

    found_discipline = disciplines_service.getDisciplineByCode(disciplineCode)
    if found_discipline is not None:
        if user["role"] == "professor":
            emailCaller = user["email"]
            if found_discipline["titular"] != emailCaller:
                raise HTTPException(
                    status_code=HTTPStatus.FORBIDDEN,
                    detail="You are not allowed to insert ponderi to this discipline",
                )

        rez = material_service.removeLabMaterials(removeRequest.materials_to_remove, found_discipline)
        return {
            "message": rez,
            "_links": {
                "parent": f"/materials/disciplines/{disciplineCode}/labs",
                "remained_materials": f"/materials/disciplines/{disciplineCode}/labs",
            }
        }

    raise HTTPException(
        status_code=HTTPStatus.NOT_FOUND,
        detail="Discipline not found",
    )


@router.patch("/materials/disciplines/{disciplineCode}/courses/insert")
def insert_new_course_materials(
    request: Request,
    disciplineCode: str,
    files: list[UploadFile] = File(...),
    material_service: MaterialsService = Depends(inject_materials_service),
    disciplines_service: DisciplinesService = Depends(inject_disciplines_service),
):
    user = request.state.user
    if user["role"] == "student":
        raise HTTPException(
            status_code=HTTPStatus.FORBIDDEN,
            detail="You are not allowed to insert materials",
        )

    found_discipline = disciplines_service.getDisciplineByCode(disciplineCode)
    if found_discipline is not None:
        if user["role"] == "professor":
            emailCaller = user["email"]
            if found_discipline["titular"] != emailCaller:
                raise HTTPException(
                    status_code=HTTPStatus.FORBIDDEN,
                    detail="You are not allowed to insert materials to this discipline",
                )

        message, file_names = material_service.insertCourseMaterial(files, found_discipline)
        return {
            "message": message,
            "file_names": file_names,
            "links": {
                "self": f"/materials/disciplines/{disciplineCode}/courses",
                "parent": f"/materials/disciplines/{disciplineCode}",
                "materials": [
                    {"name": file_name, "link": f"/materials/disciplines/{disciplineCode}/courses/{file_name}"}
                    for file_name in file_names
                ],
            },
        }

    raise HTTPException(
        status_code=HTTPStatus.NOT_FOUND,
        detail="Discipline not found",
    )


@router.patch("/materials/disciplines/{disciplineCode}/courses/remove")
def remove_course_materials(request: Request, disciplineCode: str, removeRequest: RemoveMaterialsRequest, material_service: MaterialsService = Depends(inject_materials_service),
                            disciplines_service: DisciplinesService = Depends(inject_disciplines_service),
                            ):
    user = request.state.user
    if user["role"] == "student":
        raise HTTPException(
            status_code=HTTPStatus.FORBIDDEN,
            detail="You are not allowed to remove materials",
        )
    found_discipline = disciplines_service.getDisciplineByCode(disciplineCode)
    if found_discipline is not None:
        if user["role"] == "professor":
            emailCaller = user["email"]
            if found_discipline["titular"] != emailCaller:
                raise HTTPException(
                    status_code=HTTPStatus.FORBIDDEN,
                    detail="You are not allowed to insert ponderi to this discipline",
                )

        rez = material_service.removeCourseMaterials(removeRequest.materials_to_remove, found_discipline)
        return {
            "message": rez,
            "_links": {
                "parent": f"/materials/disciplines/{disciplineCode}/courses",
                "remained_materials": f"/materials/disciplines/{disciplineCode}/courses",
            }
        }

    raise HTTPException(
        status_code=HTTPStatus.NOT_FOUND,
        detail="Discipline not found",
    )

@router.delete("/materials/disciplines/{disciplineCode}")
def delete_discipline(
    disciplineCode: str,
    request: Request,
    disciplines_service: DisciplinesService = Depends(inject_disciplines_service),
):
    user = request.state.user
    if user["role"] != "admin":
        raise HTTPException(
            status_code=HTTPStatus.FORBIDDEN,
            detail="You do not have permission to delete disciplines.",
        )

    message = disciplines_service.deleteDisciplineByCode(disciplineCode)
    return {
        "message": message,
        "_links": {
            "parent": "/materials/disciplines",
        },
    }









