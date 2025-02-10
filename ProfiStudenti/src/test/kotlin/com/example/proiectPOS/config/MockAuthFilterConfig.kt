package com.example.proiectPOS.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("test")
class MockAuthorizationFilterConfig: Filter{
    override fun doFilter(p0: ServletRequest?, p1: ServletResponse?, p2: FilterChain?) {
        val httpRequest = p0 as HttpServletRequest
        val role = httpRequest.getHeader("role") ?: "admin"
        val sub = httpRequest.getHeader("sub") ?: "12345"
        val email = httpRequest.getHeader("email") ?: "test@academic.tuiasi.ro"

        println("Here is the -> Role: $role, Sub: $sub, Email: $email")

        httpRequest.setAttribute("role", role)
        httpRequest.setAttribute("sub", sub)
        httpRequest.setAttribute("email", email)
        p2?.doFilter(p0, p1)
    }
}
