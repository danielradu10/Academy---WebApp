from http import HTTPStatus

from fastapi import Header, HTTPException

from grpcDir.AuthenticationClient import AuthenticationClient
from interfaces.ValidatorServiceInterface import ValidatorServiceInterface


class ValidatorService(ValidatorServiceInterface):
    def validate(self, authorization: str):
        print("Validation step")
        client = AuthenticationClient()
        if not authorization.startswith("Bearer "):
            raise HTTPException(
                status_code=HTTPStatus.UNAUTHORIZED,
                detail="Authorization header must start with Bearer token",
            )
        token = authorization.split(" ")[1]
        response = client.validate(token)
        if not response.valid:
            raise HTTPException(
                status_code=HTTPStatus.UNAUTHORIZED,
                detail="Invalid token",
            )
        return {
            "role": response.role,
            "sub": response.sub,
            "email": response.email,
        }
