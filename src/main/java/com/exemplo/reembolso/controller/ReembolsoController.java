package com.exemplo.reembolso.controller;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // necessário para @PathVariable
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/reembolsos")
public class ReembolsoController {

    @Autowired
    private RuntimeService runtimeService;

@PostMapping
public ResponseEntity<Map<String, String>> criarReembolso(@RequestBody Map<String, Object> payload) {
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("processo_reembolso", payload);
    Map<String, String> response = new HashMap<>();
    response.put("processInstanceId", instance.getProcessInstanceId());
    return ResponseEntity.ok(response);
}



    @GetMapping
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("API de reembolso está online.");
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<String> verificarStatus(@PathVariable String id) {
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(id)
                .singleResult();

        if (instance == null) {
            return ResponseEntity.ok("Finalizado");
        } else {
            return ResponseEntity.ok("Em andamento");
        }
    }
}
