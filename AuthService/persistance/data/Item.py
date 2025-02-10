from pydantic import BaseModel
from enum import Enum

class Role(Enum):
    professor = 'professor'
    student = 'student'
    admin = 'admin'

class User:
    email: str
    password: str

    def __init__(self, email: str, password: str, role: Role):
        self.email = email
        self.password = password
        self.role = role

class LoginItem(BaseModel):
    username: str
    password: str

class LogoutItem(BaseModel):
    jwt: str

class CreateAccountItem(BaseModel):
    username: str
    password: str
    role: str


