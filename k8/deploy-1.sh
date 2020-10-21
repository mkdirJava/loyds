#!/bin/bash
kubectl delete secret loyds-secret
kubectl delete deploy wilson-loyds
kubectl apply -f loyds-secret-1.yaml
kubectl apply -f wilson.yaml

