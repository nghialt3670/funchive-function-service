# Pipeline Feature

## Overview

The Pipeline feature allows you to create and execute complex workflows by connecting nodes together, similar to ComfyUI workflows. A pipeline consists of nodes (Values and Functions) and connections that define how data flows between them.

## Key Components

### Pipeline Structure
- **Pipeline**: The main workflow container that holds nodes and connections
- **Node**: Can be either a Value node (static data) or Function node (executable function)
- **Connection**: Defines how data flows from one node's output to another node's input
- **Position**: Visual coordinates for UI representation

### Node Types

#### ValueNode
- Contains static data (string, number, boolean, object, array, file)
- Acts as input or constant values in the pipeline
- Outputs its stored value when executed

#### FunctionNode
- References an existing function by ID
- Receives inputs from connected nodes
- Executes the function and produces outputs
- Can connect to other function nodes or be final outputs

### Execution Flow

1. **Dependency Analysis**: Build a graph of node dependencies based on connections
2. **Topological Sorting**: Determine the correct execution order to avoid circular dependencies
3. **Sequential Execution**: Execute nodes in order, passing outputs to connected inputs
4. **Data Flow**: Handle complex object field mappings when connecting to function parameters

## API Endpoints

### Create Pipeline
```http
POST /pipelines
Content-Type: application/json

{
  "name": "My Pipeline",
  "description": "Sample pipeline description",
  "nodes": [...],
  "connections": [...]
}
```

### Get Pipeline
```http
GET /pipelines/{pipelineId}
```

### Update Pipeline
```http
PUT /pipelines/{pipelineId}
Content-Type: application/json

{
  "name": "Updated Pipeline",
  "nodes": [...],
  "connections": [...]
}
```

### Delete Pipeline
```http
DELETE /pipelines/{pipelineId}
```

### Execute Pipeline
```http
POST /pipelines/{pipelineId}/execute
Content-Type: application/json

{
  "inputs": {
    "nodeId1": {
      "type": "STRING",
      "data": "input value"
    }
  }
}
```

### List Pipelines
```http
GET /pipelines?keyword=search&createdBy=user&page=0&size=10
```

## Example Usage

See `docs/pipeline-example.json` for a complete example of an image processing pipeline that:

1. Takes an input image and resize parameters
2. Resizes the image using a function
3. Applies a filter with specified parameters
4. Saves the processed image to an output path

## Connection Ports

- **sourcePort**: The output parameter name from the source node
- **targetPort**: The input parameter name or object field path for the target node

For object field mapping, you can use dot notation:
```json
{
  "targetPort": "config.image.width"
}
```

This allows connecting a simple value to a nested field in an object parameter.

## Error Handling

- **Circular Dependency**: Detected during topological sort, prevents infinite loops
- **Missing Dependencies**: Validation ensures all required inputs are connected
- **Function Execution Errors**: Properly caught and logged with context
- **Invalid Connections**: Validated during pipeline creation/update

## Future Enhancements

- Visual pipeline editor UI
- Real-time execution monitoring
- Pipeline templates and sharing
- Conditional execution paths
- Parallel execution optimization
- Pipeline versioning and rollback 