package com.funchive.functionservice.pipeline.service;

import com.funchive.functionservice.function.FunctionService;
import com.funchive.functionservice.function.SandboxService;
import com.funchive.functionservice.function.model.document.Value;
import com.funchive.functionservice.function.model.dto.ExecutionTriggerDto;
import com.funchive.functionservice.pipeline.model.document.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineExecutionService {
    
    private final FunctionService functionService;
    private final SandboxService sandboxService;
    
    /**
     * Execute a pipeline by processing nodes in topological order
     */
    public void executePipeline(Pipeline pipeline, Map<String, Value<?>> initialInputs) {
        log.info("Starting execution of pipeline: {}", pipeline.getId());
        
        // Build dependency graph
        Map<String, Set<String>> dependencies = buildDependencyGraph(pipeline);
        
        // Perform topological sort to determine execution order
        List<String> executionOrder = topologicalSort(pipeline.getNodes(), dependencies);
        
        // Store node outputs for passing between nodes
        Map<String, Value<?>> nodeOutputs = new HashMap<>(initialInputs);
        
        // Execute nodes in order
        for (String nodeId : executionOrder) {
            Node node = findNodeById(pipeline.getNodes(), nodeId);
            if (node != null) {
                executeNode(node, nodeOutputs, pipeline.getConnections());
            }
        }
        
        log.info("Completed execution of pipeline: {}", pipeline.getId());
    }
    
    /**
     * Build a dependency graph showing which nodes depend on which other nodes
     */
    private Map<String, Set<String>> buildDependencyGraph(Pipeline pipeline) {
        Map<String, Set<String>> dependencies = new HashMap<>();
        
        // Initialize all nodes with empty dependencies
        pipeline.getNodes().forEach(node -> dependencies.put(node.getId(), new HashSet<>()));
        
        // Add dependencies based on connections
        pipeline.getConnections().forEach(connection -> {
            dependencies.get(connection.getTargetNodeId()).add(connection.getSourceNodeId());
        });
        
        return dependencies;
    }
    
    /**
     * Perform topological sort to determine execution order
     */
    private List<String> topologicalSort(List<Node> nodes, Map<String, Set<String>> dependencies) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();
        
        for (Node node : nodes) {
            if (!visited.contains(node.getId())) {
                topologicalSortUtil(node.getId(), dependencies, visited, visiting, result);
            }
        }
        
        return result;
    }
    
    private void topologicalSortUtil(String nodeId, Map<String, Set<String>> dependencies, 
                                   Set<String> visited, Set<String> visiting, List<String> result) {
        if (visiting.contains(nodeId)) {
            throw new RuntimeException("Circular dependency detected in pipeline");
        }
        
        if (visited.contains(nodeId)) {
            return;
        }
        
        visiting.add(nodeId);
        
        // Visit all dependencies first
        for (String dependency : dependencies.get(nodeId)) {
            topologicalSortUtil(dependency, dependencies, visited, visiting, result);
        }
        
        visiting.remove(nodeId);
        visited.add(nodeId);
        result.add(nodeId);
    }
    
    /**
     * Execute a single node in the pipeline
     */
    private void executeNode(Node node, Map<String, Value<?>> nodeOutputs, List<Connection> connections) {
        log.info("Executing node: {} ({})", node.getName(), node.getNodeType());
        
        if (node instanceof ValueNode) {
            // For value nodes, just store the value as output
            ValueNode valueNode = (ValueNode) node;
            nodeOutputs.put(node.getId(), valueNode.getValue());
            
        } else if (node instanceof FunctionNode) {
            // For function nodes, collect inputs and execute the function
            FunctionNode functionNode = (FunctionNode) node;
            
            // Collect input values from connected nodes
            Map<String, Value<?>> functionInputs = collectFunctionInputs(node.getId(), connections, nodeOutputs);
            
            // Execute the function
            var functionDetail = functionService.getFunctionDetail(functionNode.getFunctionId());
            var executionTrigger = new ExecutionTriggerDto();
            // TODO: Set up execution trigger with collected inputs
            
            try {
                sandboxService.executeFunction(functionDetail, executionTrigger);
                // TODO: Capture function output and store in nodeOutputs
                log.info("Function executed successfully: {}", functionNode.getFunctionId());
            } catch (Exception e) {
                log.error("Error executing function: {}", functionNode.getFunctionId(), e);
                throw new RuntimeException("Function execution failed", e);
            }
        }
    }
    
    /**
     * Collect input values for a function node from connected nodes
     */
    private Map<String, Value<?>> collectFunctionInputs(String nodeId, List<Connection> connections, 
                                                       Map<String, Value<?>> nodeOutputs) {
        Map<String, Value<?>> inputs = new HashMap<>();
        
        // Find all connections that target this node
        List<Connection> incomingConnections = connections.stream()
                .filter(conn -> conn.getTargetNodeId().equals(nodeId))
                .collect(Collectors.toList());
        
        for (Connection connection : incomingConnections) {
            Value<?> sourceValue = nodeOutputs.get(connection.getSourceNodeId());
            if (sourceValue != null) {
                String targetPort = connection.getTargetPort();
                if (targetPort != null) {
                    inputs.put(targetPort, sourceValue);
                }
            }
        }
        
        return inputs;
    }
    
    private Node findNodeById(List<Node> nodes, String nodeId) {
        return nodes.stream()
                .filter(node -> node.getId().equals(nodeId))
                .findFirst()
                .orElse(null);
    }
} 