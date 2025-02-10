import re
from http import HTTPStatus

from fastapi import HTTPException

from persistance.data.Item import User, Role
from persistance.database import Database


class AccountsManager:
    def __init__(self):
        self.database = Database()


    def create_account(self, username: str, password: str, role: str) -> bool:
        user = None
        if role == "professor":
            user = User(username, password, Role.professor)
        if role == "admin":
            user = User(username, password, Role.admin)
        if role == "student":
            user = User(username, password, Role.student)
        print("User in create account: ", user)
        try:
            valid = self.validateUsername(username, role)
            if valid:
                print("Username is valid. Adding")
                self.database.add_element(user)
                print("Added")
                return True
        except Exception as e:
            if "Duplicate entry" in str(e):
                raise HTTPException(status_code=HTTPStatus.CONFLICT, detail=str(e))
            raise HTTPException(
                status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                detail=str(e))


    def validateUsername(self, username: str, role: str) -> bool:
        if role != "professor" and (role != "admin") and (role != "student"):
            raise HTTPException(
                status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                detail="Role is invalid",
            )
        if role is None:
            raise HTTPException(
                detail="Role is none",
                status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
            )
        if username is None:
            raise HTTPException(
                detail="Username is none",
                status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
            )
        if role == "professor":
            if "academic" not in username:
                raise HTTPException(
                    detail="For professor role you should use @academic emails",
                    status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                )
        if role == "student":
            if "student" not in username:
                raise HTTPException(
                    detail="For student role you should use @student emails",
                    status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
                )
        if self.is_valid_email(username) is False:
            raise HTTPException(
                detail="Username is invalid",
                status_code=HTTPStatus.UNPROCESSABLE_ENTITY,
            )
        return True

    def get_all_accounts(self):
        accounts = self.database.get_all_users()
        users = []
        for account in accounts:
            user = User(account[1], account[2], account[3])
            users.append(user)
        return users

    def is_valid_email(self, email):
        pattern = r'^[a-z]+\.[a-z]+@(student|academic)\.tuiasi\.ro$'
        return re.match(pattern, email) is not None






