package com.falcao.desafioluizalabs.controller

import com.falcao.desafioluizalabs.service.ConversaoService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("conversao")
class ConversaoController(
    private val conversaoService: ConversaoService
) {


    @PostMapping("upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun converteArquivo(@RequestPart("arquivo") arquivo: MultipartFile,
                        @RequestParam(required = false) name: String) : ResponseEntity<Any> {
        return try {
            if (arquivo.isEmpty) {
                return ResponseEntity.badRequest().body(
                    "Arquivo vazio")

            }

            if (name.isNotBlank()) {
                val result = conversaoService.retornaDadosByName(String(arquivo.bytes, Charsets.UTF_8),name)
                return ResponseEntity.ok(result)
            }
            val content = String(arquivo.bytes, Charsets.UTF_8)
            val result = conversaoService.retornaTodosDados(content)
            return ResponseEntity.ok(result)

        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(
                "Erro interno")
        }
    }
}