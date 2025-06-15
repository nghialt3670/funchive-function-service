import json
import os
import sys
import traceback
from typing import Any, Dict


### BEGIN IMPORTS
### END IMPORTS

def process_input(input_value: Dict[str, Any]) -> Dict[str, Any]:
    if input_value['type'] == 'STRING':
        return str(input_value['data'])
    elif input_value['type'] == 'NUMBER':
        return float(input_value['data'])
    elif input_value['type'] == 'BOOLEAN':
        return bool(input_value['data'])
    elif input_value['type'] == 'ARRAY':
        return [process_input(item) for item in input_value['data']]
    elif input_value['type'] == 'OBJECT':
        return {key: process_input(value) for key, value in input_value['data'].items()}
    elif input_value['type'] == 'FILE':
        return input_value['data']['filename']
    else:
        raise ValueError(f"Invalid value type: {input_value['type']}")


def process_output(output_type: Dict[str, Any], output: Any) -> Dict[str, Any]:
    type_name = output_type.get('name')

    if type_name == 'STRING':
        if not isinstance(output, str):
            raise ValueError(f"Expected str for STRING type, got {type(output)}")

        return {
            "type": "STRING",
            "data": str(output) if output is not None else ""
        }

    elif type_name == 'NUMBER':
        if not isinstance(output, (int, float)):
            raise ValueError(f"Expected int or float for NUMBER type, got {type(output)}")

        return {
            "type": "NUMBER",
            "data": float(output) if output is not None else 0.0
        }

    elif type_name == 'BOOLEAN':
        if not isinstance(output, bool):
            raise ValueError(f"Expected bool for BOOLEAN type, got {type(output)}")

        return {
            "type": "BOOLEAN",
            "data": bool(output) if output is not None else False
        }

    elif type_name == 'ARRAY':
        if not isinstance(output, list):
            raise ValueError(f"Expected list for ARRAY type, got {type(output)}")

        element_type = output_type['elementType']

        return {
            "type": "ARRAY",
            "data": [process_output(element_type, item) for item in output]
        }

    elif type_name == 'OBJECT':
        if not isinstance(output, dict):
            raise ValueError(f"Expected dict for OBJECT type, got {type(output)}")

        processed_data = {}

        for key, key_type in output_type['schema'].items():
            if key in output:
                processed_data[key] = process_output(key_type, output[key])
            else:
                raise ValueError(f"Missing key {key} in output")

        return {
            "type": "OBJECT",
            "data": processed_data
        }

    elif type_name == 'FILE':
        if not isinstance(output, str):
            raise ValueError(f"Expected filename string for FILE type, got {type(output)}")

        extension = output.split('.')[-1]

        if extension != output_type['extension']:
            raise ValueError(f"Invalid file extension: {extension}, expected {output_type['extension']}")

        return {
            "type": "FILE",
            "data": {
                "filename": output,
            }
        }

    else:
        raise ValueError(f"Unknown output type: {type_name}")


def function(input):
    ### BEGIN FUNCTION BODY
    ### END FUNCTION BODY
    pass


def main():
    try:
        input_value_json = os.environ.get('INPUT_VALUE_JSON')

        if not input_value_json:
            raise ValueError("INPUT_VALUE_JSON environment variable not set")

        input_value = json.loads(input_value_json)
        processed_input = process_input(input_value)

        output_type_json = os.environ.get('OUTPUT_TYPE_JSON')

        if not output_type_json:
            raise ValueError("OUTPUT_TYPE_JSON environment variable not set")

        output_type = json.loads(output_type_json)

        output = function(processed_input)
        output_value = process_output(output_type, output)

        output_value_json = json.dumps(output_value, ensure_ascii=False, indent=2)

        # Set the output value as environment variable
        os.environ['OUTPUT_VALUE_JSON'] = output_value_json

        # Also print to stdout for logging
        print(output_value_json)

        sys.exit(0)

    except Exception as e:
        error_output = {
            "success": False,
            "error": {
                "type": type(e).__name__,
                "message": str(e)
            }
        }
        error_json = json.dumps(error_output, ensure_ascii=False, indent=2)
        os.environ['OUTPUT_VALUE_JSON'] = error_json
        print(error_json)
        sys.exit(1)


if __name__ == "__main__":
    main()
