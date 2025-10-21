#!/usr/bin/bash
curl -H "Content-Type:application/json" http://kafka-connect:8083/connectors -d @/from_source.json |
curl -H "Content-Type:application/json" http://kafka-connect:8083/connectors -d @/to_targetjson 