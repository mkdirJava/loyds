#!/bin/bash
kubectl delete secret loyds-secret
kubectl delete deploy wilson-loyds
kubectl apply -f loyds-secret-2.yaml
kubectl apply -f wilson.yaml
