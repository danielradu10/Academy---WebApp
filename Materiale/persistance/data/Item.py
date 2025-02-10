from fastapi import FastAPI
from pydantic import BaseModel

class Material(BaseModel):
    name: str
    content: bytes
    content_type: str

class ProbaPondere(BaseModel):
    proba: str | None = None
    pondere: str | None = None

class DisciplineItem(BaseModel):
    code: str
    name: str | None = ""
    probe_ponderi: list[ProbaPondere] | None = []
    lab_materials: list[Material] | None = []
    course_materials: list[Material] | None = []
    titular: str


class RemoveMaterialsRequest(BaseModel):
    materials_to_remove: list[str]

class InsertPonderiRequest(BaseModel):
    probe: list[str]
    ponderi: list[str]

