#!/bin/bash

set -e
lein uberjar

scp -P64646 target/uberjar/graphgenerator.jar root@franky.pub:/var/lib/lxd/containers/graphgenerator/rootfs/root/graphgenerator/
