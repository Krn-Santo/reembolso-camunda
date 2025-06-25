package com.exemplo.reembolso.controller;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    private TaskService taskService;

    // ✅ Iniciar novo processo de reembolso
   @PostMapping
public ResponseEntity<Map<String, String>> criarReembolso(@RequestBody Map<String, Object> payload) {
    try {
        VariableMap variables = Variables.createVariables();
        payload.forEach(variables::put);

        ProcessInstance instance = runtimeService.startProcessInstanceByKey("processo_reembolso", variables);

        Map<String, String> response = new HashMap<>();
        response.put("processInstanceId", instance.getProcessInstanceId());

        return ResponseEntity.ok(response);
    } catch (Exception e) {
        System.err.println("Erro ao iniciar processo: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", e.getMessage()));
    }
}
// 🔧 NOVO ENDPOINT PARA BUSCAR processInstanceId a partir do taskId
@GetMapping("/processo-id/{taskId}")
public ResponseEntity<?> buscarProcessInstanceId(@PathVariable String taskId) {
    try {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task != null) {
            Map<String, String> response = new HashMap<>();
            response.put("processInstanceId", task.getProcessInstanceId());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");
        }

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao buscar processInstanceId: " + e.getMessage());
    }
}



    // ✅ Verificar status do processo (Em andamento / Finalizado)
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

    // ✅ Verificar se a API está online
    @GetMapping
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("API de reembolso está online.");
    }

    // ✅ Buscar tarefa ativa da instância de processo
    @GetMapping("/tarefas/{processInstanceId}")
    public ResponseEntity<?> buscarTarefa(@PathVariable String processInstanceId) {
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .singleResult();

        if (task != null) {
            Map<String, String> response = new HashMap<>();
            response.put("taskId", task.getId());
            response.put("name", task.getName());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma tarefa ativa encontrada.");
        }
    }

    // ✅ Completar tarefa do FORMULÁRIO inicial (com motivo, valor, etc.)
    @PostMapping("/formulario/{taskId}")
    public ResponseEntity<Void> completarFormulario(@PathVariable String taskId, @RequestBody Map<String, Object> body) {
        try {
            VariableMap vars = Variables.createVariables();
            body.forEach(vars::put);
            System.out.println("Tentando completar tarefa: " + taskId);
            System.out.println("Variáveis recebidas: " + body.toString());
            taskService.complete(taskId, vars);
            System.out.println("Tarefa " + taskId + " completada com sucesso.");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("Erro ao completar formulário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ Completar tarefa de APROVAÇÃO DO GERENTE (com variável 'aprovado')
   @PostMapping("/aprovar/{taskId}")
public ResponseEntity<Void> aprovarOuRejeitarTarefa(@PathVariable String taskId, @RequestBody Map<String, Object> body) {
    try {
        boolean aprovado = Boolean.parseBoolean(body.get("aprovado").toString());

        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("aprovado", aprovado); // ✅ adiciona diretamente a variável de processo

        System.out.println("Aprovando ou rejeitando a tarefa: " + taskId);
        System.out.println("Valor da decisão: " + aprovado);

        taskService.complete(taskId, variaveis); // ✅ chamada direta

        return ResponseEntity.noContent().build();
    } catch (Exception e) {
        System.err.println("Erro ao completar tarefa de aprovação: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}




    // ✅ Buscar variáveis da tarefa do gerente (para exibir na tela)
   @GetMapping("/tarefas/detalhes/{taskId}")
public ResponseEntity<?> buscarVariaveisTarefa(@PathVariable String taskId) {
    try {
        System.out.println("Recebido taskId: " + taskId);
        Map<String, Object> variaveis = taskService.getVariables(taskId);
        return ResponseEntity.ok(variaveis);
    } catch (Exception e) {
        System.err.println("Erro ao buscar variáveis da tarefa: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao buscar variáveis da tarefa: " + e.getMessage());
    }
}

}
