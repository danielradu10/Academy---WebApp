from http import HTTPStatus
from urllib.request import Request

from fastapi import APIRouter, HTTPException, Header, Depends

from AuthenticationClient import AuthenticationClient
from persistance.data.Item import LoginItem, LogoutItem, CreateAccountItem, User
from services.AccountsManager import AccountsManager

router = APIRouter()

def get_account_manager():
    return AccountsManager()


@router.post('/login')
def login(loginItem: LoginItem):
    client = AuthenticationClient()
    message = {
        "username": loginItem.username,
        "password": loginItem.password
    }
    print(message)
    try:
        resultJWT = client.authenticate(message)
        print(resultJWT)
        return {
            "jwt": resultJWT.jwt
        }
    except Exception as e:
        raise HTTPException (
            status_code=HTTPStatus.UNAUTHORIZED,
            detail="Unauthorized" + str(e),
        )


@router.post('/logout')
def login(logoutItem: LogoutItem):
    try:
        client = AuthenticationClient()
        response = client.invalidate(message=logoutItem.jwt)
        return {
            "successful": response.successful
        }
    except Exception as e:
        raise HTTPException(
            status_code=HTTPStatus.BAD_REQUEST,
            detail= repr(e),
        )


@router.post('/create_account')
def create_account(
        createAccountItem: CreateAccountItem,
        authorization: str = Header(...),
        account_creator = Depends(get_account_manager)
):
    if not authorization.startswith("Bearer "):
        raise HTTPException(
            status_code=HTTPStatus.UNAUTHORIZED,
            detail="Authorization header must be of type Bearer token",
        )

    token = authorization.split(" ")[1]
    client = AuthenticationClient()

    try:
        user_info = client.validate(token)
        if user_info.role != "admin":
            raise HTTPException(
                status_code=HTTPStatus.FORBIDDEN,
                detail="You do not have permission to create accounts",
            )

        account_creator.create_account(
            username=createAccountItem.username,
            password=createAccountItem.password,
            role=createAccountItem.role
        )
        return {"message": "Account created successfully"}

    except Exception as e:
        raise e


@router.post('/accounts')
def get_accounts(
        authorization: str = Header(...),
        account_manager = Depends(get_account_manager)
):
    if not authorization.startswith("Bearer "):
        raise HTTPException(
            status_code=HTTPStatus.UNAUTHORIZED,
            detail="Authorization header must be of type Bearer token",
        )

    token = authorization.split(" ")[1]
    client = AuthenticationClient()

    try:
        user_info = client.validate(token)
        if user_info.role != "admin":
            raise HTTPException(
                status_code=HTTPStatus.FORBIDDEN,
                detail="You do not have permission to get all accounts",
            )

        accounts = account_manager.get_all_accounts()
        print("Accounts: ", accounts)
        return {
            "accounts": accounts
        }
    except Exception as e:
        raise e

