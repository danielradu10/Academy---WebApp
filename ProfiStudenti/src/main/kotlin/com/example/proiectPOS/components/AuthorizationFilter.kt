package com.example.proiectPOS.components
import com.example.proiectPOS.grpc.GrpcClient
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class AuthorizationFilter: Filter {
    override fun doFilter(p0: ServletRequest?, p1: ServletResponse?, p2: FilterChain?) {
        val httpRequest = p0 as HttpServletRequest
        val httpResponse = p1 as HttpServletResponse

        println("Entered the filter...")

        if (httpRequest.requestURI.startsWith("/v3/api-docs") ||
            httpRequest.requestURI.startsWith("/swagger-ui") ||
            httpRequest.requestURI.startsWith("/swagger-resources") ||
            httpRequest.requestURI.startsWith("/docs") ||
            httpRequest.requestURI.startsWith("/webjars")) {
            println("Skipping authentication for Swagger UI and related endpoints")
            p2!!.doFilter(p0, p1)
            return
        }

        println("Request method: ${httpRequest.method}")
        println("Headers: ${httpRequest.headerNames.toList().joinToString { "$it: ${httpRequest.getHeader(it)}" }}")

        httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:3000")
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH")
        httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type")
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true")

        if (httpRequest.method.equals("OPTIONS", ignoreCase = true)) {
            httpResponse.status = HttpServletResponse.SC_OK
            println("Exiting because of OPTIONS")
            return
        }

        val authorizationHeader = httpRequest.getHeader("Authorization")
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            println("Bad authorization header: $authorizationHeader")
            httpResponse.status = HttpServletResponse.SC_UNAUTHORIZED
            httpResponse.writer.write("{\"message\": \"Missing authorization header\"}")
            return
        }


        println("Authorization header: $authorizationHeader")

        val jws = authorizationHeader.split(" ")[1]
        println("JWS: $jws")
        println("Creating the grpc client")

        val client = GrpcClient()
        val response = client.validate(jws)
        if (response.valid == true){
            p0.setAttribute("role", response.role)
            p0.setAttribute("sub", response.sub)
            p0.setAttribute("email", response.email)
            p2!!.doFilter(p0, p1)
        }
        else {
            httpResponse.status = HttpServletResponse.SC_BAD_REQUEST
            httpResponse.writer.write("{\"message\": \"Invalid token\"}")
            return
        }
    }
}