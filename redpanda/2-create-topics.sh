#!/bin/bash
docker exec -it redpanda-1 rpk topic create flight-data --brokers=localhost:9092
docker exec -it redpanda-1 rpk topic create radar-data --brokers=localhost:9092
docker exec -it redpanda-1 rpk topic create transponder-data --brokers=localhost:9092
