import uuid
from datetime import datetime, timedelta
from random import random

import grpc
from grpc.beta.interfaces import StatusCode
from jwt import ExpiredSignatureError, InvalidTokenError

import auth_pb2
import jwt
from auth_pb2_grpc import AuthServiceServicer, add_AuthServiceServicer_to_server

from concurrent import futures

from persistance.data.Item import User, Role
from persistance.database import Database
from persistance.redis.redisclient import RedisClient


class AuthenticationService(AuthServiceServicer):
    def __init__(self):
        self.dbService = Database()
        self.blacklist = RedisClient()
        self.secret_key = "just_some_secret_key?"

    def authenticate(self, request, context):
        print("Username received: " + str(request.username))
        print("Password received: " + str(request.password))

        # here we check if the username and the password exists in the
        try:
            user = User(request.username, request.password, role=Role.student)
            found = self.dbService.verify_user(user)
            print("Finished found")
            if found:
                print("Authentication successful. Creating the jws")
                userFound = self.dbService.get_user_by_email(email=request.username)
                payload = {
                    "iss": "http",
                    "sub": str(userFound[0]),
                    "exp": datetime.now() + timedelta(hours=1),
                    "jti": str(uuid.uuid4()),
                    "role": userFound[3],
                    "email": user.email
                }

                token = jwt.encode(payload, self.secret_key, algorithm="HS256")
                context.set_code(grpc.StatusCode.OK)
                context.set_details("Valid token")
                print("JWS created!")
                return auth_pb2.AuthenticationResponse(jwt=token)
            else:
                print("Did not find the credentials.")
                context.set_code(grpc.StatusCode.UNAUTHENTICATED)
                context.set_details("Invalid username or password")
                print(auth_pb2.AuthenticationResponse(jwt="None"))
                return auth_pb2.AuthenticationResponse(jwt="None")

        except Exception as e:
            context.set_code(grpc.StatusCode.ABORTED)
            context.set_details("Exception at handling the database: {}".format(e))
            return auth_pb2.AuthenticationResponse(jwt="None")


    def invalidate(self, request, context):
        try:
            self.blacklist.add_token(request.jwt)
            print("Blacklisted")
            context.set_code(grpc.StatusCode.OK)
            context.set_details("Token invalided successfully")
            print(auth_pb2.InvalidateResponse(successful=True))
            return auth_pb2.InvalidateResponse(successful=True)
        except Exception as e:
            print("Exception")
            context.set_code(grpc.StatusCode.ABORTED)
            context.set_details(e)
            return auth_pb2.InvalidateResponse(successful=False)


    def validate(self, request, context):
        print("The validation was called.")
        token = request.jwt
        if self.blacklist.exists(token):
            print("The token is blacklisted")
            context.set_code(StatusCode.OK)
            context.set_details("The token expired")
            return auth_pb2.ValidateResponse(valid=False, sub="", role="", email="")
        else:
            try:
                payload = jwt.decode(token, self.secret_key, algorithms=["HS256"])
                return auth_pb2.ValidateResponse(
                    valid=True,
                    sub=payload.get("sub", ""),
                    role=payload.get("role", ""),
                    email=payload.get("email", "")
                )
            except ExpiredSignatureError:
                print("Token expired")
                return auth_pb2.ValidateResponse(valid=False,sub="", role="", email="")

            except InvalidTokenError:
                print("Invalid token")
                context.set_code(grpc.StatusCode.OK)
                context.set_details("The token is not valid")
                return auth_pb2.ValidateResponse(valid=False,sub="", role="", email="")



def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    add_AuthServiceServicer_to_server(AuthenticationService(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    server.wait_for_termination()


if __name__ == '__main__':
    serve()

