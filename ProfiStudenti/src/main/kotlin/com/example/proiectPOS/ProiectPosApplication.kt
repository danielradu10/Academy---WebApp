package com.example.proiectPOS

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProiectPosApplication

fun main(args: Array<String>) {
	println("Aștept conexiunea la baza de date...")
	//Thread.sleep(5000)
	println("Incerc sa ma conectez la baza de date...")
	runApplication<ProiectPosApplication>(*args)
}
