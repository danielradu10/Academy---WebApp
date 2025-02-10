package com.example.proiectPOS.grpc

import Authentication.Auth
import Authentication.AuthServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder

class GrpcClient {
    private val channel: ManagedChannel = ManagedChannelBuilder
        //.forAddress("localhost", 50051)
        .forAddress("auth-service", 50051)
        .usePlaintext()
        .build()

    private val stub = AuthServiceGrpc.newBlockingStub(channel)

    fun authenticate(username: String, password: String): Auth.AuthenticationResponse {
        val request = Auth.AuthenticationMessage.newBuilder()
            .setUsername(username)
            .setPassword(password)
            .build()
        return stub.authenticate(request)
    }

    fun validate(token: String): Auth.ValidateResponse {
        val request = Auth.ValidateMessage.newBuilder()
            .setJwt(token)
            .build()
        return stub.validate(request)
    }

    fun shutdown() {
        channel.shutdown()
    }
}
