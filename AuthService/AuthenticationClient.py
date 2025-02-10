import grpc

import auth_pb2
import auth_pb2_grpc


class AuthenticationClient(object):

    def __init__(self):
        self.host = 'localhost'
        #self.host = 'auth-service'
        self.server_port = 50051

        # instantiate a channel
        self.channel = grpc.insecure_channel(
            '{}:{}'.format(self.host, self.server_port))

        # bind the client and the server
        self.stub = auth_pb2_grpc.AuthServiceStub(self.channel)

    def authenticate(self, message):

        message = auth_pb2.AuthenticationMessage(username=message["username"], password=message["password"])
        print("Calling the authenticate service")
        return self.stub.authenticate(message)

    def validate(self, message):

        message = auth_pb2.ValidateMessage(jwt=message)
        print("Calling the validate service")
        print("The token received: " + str(message))
        return self.stub.validate(message)

    def invalidate(self, message):
        message = auth_pb2.InvalidateMessage(jwt=message)
        print("Calling the invalidate service")
        print("The token received: " + str(message))
        return self.stub.invalidate(message)


if __name__ == '__main__':
    client = AuthenticationClient()

    message = {
        "username": "admin@gmail.com",
        "password": "passwordA"
    }
    resultJWT = client.authenticate(message=message)
    print(f'{resultJWT}')

    result = client.validate(resultJWT.jwt)
    print(f'{result}')

    # result = client.validate("e3JhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwIiwic3ViIjoiNCIsImV4cCI6MTczMzQwNjc1MywianRpIjoidGVzdF91c2VybmFtZSIsInJvbGUiOiJzdHVkZW50In0.M0E_tgK6uAX2XNyax6mmlUSC5SCotTcOhuFQ_kYP-Qc")
    # print(result)