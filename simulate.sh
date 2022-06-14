#!/bin/bash
set -o xtrace
curl -X PUT -i http://localhost:8091/simulate -H "Content-type: application/json" -d '{"source":"transponder","departure":{"x":825,"y":750},"arrival":{"x":475,"y":30},"aircraft":"F-GGOD","speed":25}'
curl -X PUT -i http://localhost:8091/simulate -H "Content-type: application/json" -d '{"source":"radar","departure":{"x":150,"y":500},"arrival":{"x":650,"y":700},"aircraft":"F-GGOK","speed":15}'
